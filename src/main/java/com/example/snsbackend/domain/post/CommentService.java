package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.CommentRequest;
import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.dto.NoOffsetPage;
import com.example.snsbackend.dto.PageParam;
import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.model.Comment;
import com.example.snsbackend.model.CommentLike;
import com.example.snsbackend.model.Post;
import com.example.snsbackend.model.PostLike;
import com.example.snsbackend.repository.CommentLikeRepository;
import com.example.snsbackend.repository.CommentRepository;
import com.example.snsbackend.repository.PostRepository;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    void createComment(String postId, CommentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        if (postRepository.existsById(postId)) {
            if (request.getParentId() != null) {
                if (!commentRepository.existsById(request.getParentId())) {
                    throw new NoSuchElementException();
                }
            }

            commentRepository.save(Comment.builder()
                    .postId(postId)
                    .userId(userId)
                    .parentId(request.getParentId())
                    .content(request.getContent())
                    .createdAt(new Date()).build());
        } else {
            throw new NoSuchElementException();
        }
    }

    void likeComment(String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();

        Comment comment = commentRepository.findById(commentId).orElseThrow();
        comment.setLikesCount(comment.getLikesCount() + 1);
        commentLikeRepository.save(new CommentLike(commentId, userId, new Date(), comment.getPostId()));
        commentRepository.save(comment);
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
        } else {
            throw new NoSuchElementException();
        }
    }

    NoOffsetPage<Comment> getComments(String postId, PageParam pageParam) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException();
        }
        // No offset 방식 페이징
        Query query = new Query().addCriteria(Criteria.where("post_id").is(postId));
        query.addCriteria(Criteria.where("parent_id").isNull());
        // object id는 생성 시간이 포함되어 있어 시간순 정렬이 가능
        // .lt() : 특정 값보다 작은 문서만 조회함
        if (pageParam.getLastId() != null) {
            query.addCriteria(Criteria.where("id").lt(new ObjectId(pageParam.getLastId())));
        }
        // 최신순으로 정렬
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageParam.getSize());

        List<Comment> comments = mongoTemplate.find(query, Comment.class);
        if (comments.isEmpty()) {
            return new NoOffsetPage<>(Collections.emptyList(), null, pageParam.getSize());
        }

        return new NoOffsetPage<>(comments, comments.getLast().getId(), pageParam.getSize());
    }
}
