package com.example.snsbackend.domain.post;

import com.example.snsbackend.dto.CommentRequest;
import com.example.snsbackend.model.Comment;
import com.example.snsbackend.model.CommentLike;
import com.example.snsbackend.repository.CommentLikeRepository;
import com.example.snsbackend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MongoTemplate mongoTemplate;

    // TODO: 정밀한 테스트 필요.
    void createComment(String postId, CommentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        commentRepository.save(
                new Comment(postId, userId, request.getContent(),
                        request.getParentId(), new Date(), 0));
    }

    // TODO: 정밀한 테스트 필요.
    @Transactional
    void likeComment(String commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        commentLikeRepository.save(new CommentLike(commentId, userId, new Date()));
        mongoTemplate.updateFirst(
                new Query(Criteria.where("commentId").is(commentId)),
                new Update().inc("likesCount", 1),
                Comment.class);
    }
}
