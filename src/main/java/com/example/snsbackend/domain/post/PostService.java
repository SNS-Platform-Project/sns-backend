package com.example.snsbackend.domain.post;

import com.example.snsbackend.jwt.CustomUserDetails;
import com.example.snsbackend.dto.PostRequest;
import com.example.snsbackend.model.*;
import com.example.snsbackend.repository.PostRepository;
import com.example.snsbackend.repository.QuotePostRepository;
import com.example.snsbackend.repository.RegularPostRepository;
import com.example.snsbackend.repository.RepostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final RegularPostRepository regularPostRepository;
    private final QuotePostRepository quotePostRepository;
    private final RepostRepository repostRepository;
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    public Post getPost(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void createPost(PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        RegularPost newPost = new RegularPost();
        newPost.setType("post");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setImages(content.getImages());
        newPost.setCreatedAt(new Date());
        newPost.setUser(new User(userDetails.getUserId(), userDetails.getUsername()));
        newPost.setStat(new Stat());

        regularPostRepository.save(newPost);
    }

    public void createQuote(String originalPostId, PostRequest content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        QuotePost newPost = new QuotePost();
        newPost.setType("quote");
        newPost.setContent(content.getContent());
        newPost.setHashtags(content.getHashtags());
        newPost.setMentions(content.getMentions());
        newPost.setImages(content.getImages());
        newPost.setCreatedAt(new Date());
        newPost.setOriginal_post_id(originalPostId);
        newPost.setUser(new User(userDetails.getUserId(), userDetails.getUsername()));
        newPost.setStat(new Stat());

        quotePostRepository.save(newPost);
    }

    public void createRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Repost repost = new Repost();
        repost.setType("repost");
        repost.setOriginal_post_id(originalPostId);
        repost.setUser(new User(userDetails.getUserId(), userDetails.getUsername()));
        repost.setCreatedAt(new Date());

        // 기존 게시글의 repost_count 수치 증가
        mongoTemplate.updateFirst(
                new Query(Criteria.where("id").is(originalPostId)),
                new Update().inc("stat.repost_count", 1),
                Post.class);

        repostRepository.save(repost);
    }

    public void undoRepost(String originalPostId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Query query = new Query()
                .addCriteria(Criteria.where("original_post").is(originalPostId))
                .addCriteria(Criteria.where("user.user_id").is(userDetails.getUserId()));
        List<Repost> repost = mongoTemplate.find(query, Repost.class);

        repostRepository.delete(repost.getFirst());

        // 기존 게시글의 repost_count 수치 증가
        mongoTemplate.updateFirst(
                new Query(Criteria.where("id").is(originalPostId)),
                new Update().inc("stat.repost_count", -1),
                Post.class);
    }

    public void deletePost(String postId) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Post post = postRepository.findById(postId).orElseThrow(() -> new Exception("not found"));
        if (post.getUser().getUserId().equals(userDetails.getUserId())) {
            postRepository.delete(post);
        } else {
            throw new Exception("Unauthorized");
        }
    }
}
