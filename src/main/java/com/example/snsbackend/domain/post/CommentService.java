package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.CommentRequest;
import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.*;
import com.example.snsbackend.repository.CommentLikeRepository;
import com.example.snsbackend.repository.CommentRepository;
import com.example.snsbackend.repository.PostRepository;
import com.example.snsbackend.repository.ReplyRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final CountUpdater countUpdater;

    void createComment(String postId, CommentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        log.info("사용자 {}가 게시물 {}에 댓글 작성을 시도합니다.", userId, postId);

        if (!postRepository.existsById(postId)) {
            log.error("유효하지 않은 게시물 ID: {}", postId);
            throw new NoSuchElementException("게시물 ID가 유효하지 않습니다.");
        }

        if (request.getParentId() == null) {
            // 일반 댓글인 경우 (parentId가 없음)
            commentRepository.save(new Comment(postId, userId, request.getContent()));
            log.info("사용자 {}가 게시물 {}에 댓글을 추가했습니다.", userId, postId);
        } else {
            //  대댓글(리플)인 경우 (부모 ID가 있음)
            if (isCommentValid(request.getParentId(), postId)) {
                replyRepository.save(new Reply(postId, userId, request.getContent(), request.getParentId()));
                log.info("사용자 {}가 댓글{}에 대댓글을 추가했습니다.", userId, request.getParentId());
                // 부모 댓글의 리플 개수 증가
                countUpdater.incrementCount(request.getParentId(), "replies_count", Comment.class);
                log.info("댓글 {}의 리플 수가 증가했습니다.", request.getParentId());
            } else {
                log.error("유효하지 않은 부모 댓글 ID: {}", request.getParentId());
                throw new NoSuchElementException("입력한 부모 댓글이 유효하지 않거나 존재하지 않습니다.");
            }

        }
        // 부모 게시글의 댓글 개수 증가
        countUpdater.incrementCount(postId, "stat.comments_count", Post.class);
        log.info("게시물 {}의 댓글 수가 증가했습니다.", postId);
    }

    boolean isCommentValid(String id, String postId) {
        // 요청한 postId와 부모 댓글의 postId가 일치 하는지 확인
        return mongoTemplate.exists(new Query(Criteria
                .where("id").is(id)
                .and("post_id").is(postId)
                .and("type").is("comment")), Comment.class);
    }

    void likeComment(String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            log.error("유효하지 않은 댓글 ID: {}", commentId);
            return new NoSuchElementException("유효하지 않은 댓글 ID");
        });
        comment.setLikesCount(comment.getLikesCount() + 1);
        commentLikeRepository.save(new CommentLike(commentId, userId, new Date(), comment.getPostId()));
        commentRepository.save(comment);

        log.info("사용자 {}가 댓글 {}에 좋아요를 추가했습니다.", userId, commentId);
    }

    void undoLikeComment(String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        DeleteResult result = mongoTemplate.remove(
                new Query(Criteria.where("userId").is(userId)
                        .and("commentId").is(commentId)), CommentLike.class);

        if (result.getDeletedCount() > 0) {
            mongoTemplate.updateFirst(
                    new Query(Criteria.where("id").is(commentId)),
                    new Update().inc("likes_count", -1),
                    Comment.class);
            log.info("사용자 {}가 댓글 {}에 좋아요를 삭제했습니다.", userId, commentId);
        } else {
            log.error("사용자 {}가 댓글 {}을 좋아요한 기록이 없습니다.", userId, commentId);
            throw new NoSuchElementException("사용자의 댓글 좋아요 기록이 없음");
        }
    }

    NoOffsetPage<Comment> getComments(String postId, PageParam pageParam) {
        log.info("사용자가 게시물 {}의 댓글을 요청합니다.", postId);

        if (!postRepository.existsById(postId)) {
            log.error("유효하지 않은 게시물 ID: {}", postId);
            throw new NoSuchElementException("유효하지 않은 게시물 ID");
        }

        // No offset 방식 페이징
        Query query = new Query().addCriteria(Criteria.where("post_id").is(postId));
        query.addCriteria(Criteria.where("parent_id").isNull());

        // object id는 생성 시간이 포함되어 있어 시간순 정렬이 가능
        // .lt() : 특정 값보다 작은 문서만 조회함
        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
            log.info("마지막 댓글 ID: {} 이후의 댓글을 조회합니다.", pageParam.getLastId());
        }
        // 최신순으로 정렬
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        if (comments.isEmpty()) {
            log.info("게시물 {}에 댓글이 없습니다.", postId);
            return new NoOffsetPage<>(Collections.emptyList(), null, pageParam.getSize());
        }

        log.info("게시물 {}의 댓글 {}개를 조회했습니다.", postId, comments.size());
        return new NoOffsetPage<>(comments, comments.getLast().getId(), pageParam.getSize());
    }

    NoOffsetPage<Reply> getReplies(String commentId, PageParam pageParam) {
        log.info("사용자가 댓글 {}의 리플을 요청합니다.", commentId);

        if (!commentRepository.existsById(commentId)) {
            log.error("유효하지 않은 댓글 ID: {}", commentId);
            throw new NoSuchElementException("유효하지 않은 댓글 ID");
        }

        Query query = new Query().addCriteria(Criteria.where("parent_id").is(commentId));

        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
            log.info("마지막 댓글 ID: {} 이후의 댓글을 조회합니다.", pageParam.getLastId());
        }
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        List<Reply> replies = mongoTemplate.find(query, Reply.class);
        if (replies.isEmpty()) {
            log.info("댓글 {}에 리플이 없습니다.", commentId);
            return new NoOffsetPage<>(Collections.emptyList(), null, pageParam.getSize());
        }

        log.info("댓글 {}의 리플 {}개를 조회했습니다.", commentId, replies.size());
        return new NoOffsetPage<>(replies, replies.getLast().getId(), pageParam.getSize());
    }
}
