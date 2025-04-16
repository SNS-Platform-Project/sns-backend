package com.example.snsbackend.domain.post;

import com.example.snsbackend.model.Profile;
import com.example.snsbackend.model.post.Post;
import com.example.snsbackend.repository.PostLikeRepository;
import com.example.snsbackend.repository.PostRepository;
import com.example.snsbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class PostDetailFactory {
    private final ProfileRepository profileRepository;
    private final PostLikeRepository postLikeRepository;
    private final MongoTemplate mongoTemplate;
    private final PostRepository postRepository;

    // 재귀 호출 횟수 제한
    private static final int MAX_DEPTH = 1;

    public PostDetail createFrom(Post post, String viewerUserId, int depth) {
        if (depth > MAX_DEPTH) {
            return null; // 중첩이 너무 깊으면 null 반환
        }

        Profile user = profileRepository.findById(post.getUserId()).orElseThrow();
        boolean liked = postLikeRepository.existsByPostIdAndUserId(post.getId(), viewerUserId);
        boolean reposted = mongoTemplate.exists(new Query(Criteria.where("original_post_id").is(post.getId())
                .and("user_id").is(viewerUserId).and("type").is("repost")), Post.class);

        PostDetail.PostInfo.ShareInfo shareInfo;
        if (Objects.equals(post.getType(), "quote")) {
            Post quotedPost = postRepository.findById(post.getOriginalPostId()).orElse(null);
            shareInfo = PostDetail.PostInfo.ShareInfo.builder()
                    .quotedPostUnavailable(quotedPost == null)
                    .quotedPost(quotedPost != null ? createFrom(quotedPost, viewerUserId, depth + 1) : null)
                    .build();

        } else if (Objects.equals(post.getType(), "repost")) {
            Post repostedPost = postRepository.findById(post.getOriginalPostId()).orElse(null);
            if (repostedPost == null) {
                throw new RuntimeException("[정합성 오류] 리포스트 게시글의 원본 게시글이 존재하지 않음.");
            } else {
                shareInfo = PostDetail.PostInfo.ShareInfo.builder()
                        .quotedPost(null)
                        .quotedPostUnavailable(false)
                        .repostedPost(createFrom(repostedPost, viewerUserId, depth + 1))
                        .build();
            }
        } else {
            shareInfo = PostDetail.PostInfo.ShareInfo.builder()
                    .quotedPost(null)
                    .quotedPostUnavailable(false)
                    .repostedPost(null)
                    .build();
        }

        // TODO : 게시글 작성자와의 관계 정보 추가해야됨 (FriendshipStatus)
        return PostDetail.builder()
                .id(post.getId())
                .type(post.getType())
                .user(PostDetail.User.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .lastVisitedTime(user.getLastActive())
                        .followerCount(user.getFollowersCount())
                        .profilePicUrl(user.getProfilePictureUrl())
                        .friendshipStatus(PostDetail.User.FriendshipStatus.builder()
                                .muting(false)
                                .following(false)
                                .followedBy(false)
                                .outgoingRequest(false).build())
                        .build())

                .postInfo(PostDetail.PostInfo.builder()
                        .isPostUnavailable(false)
                        .createAt(post.getCreatedAt())
                        .content(post.getContent())
                        .entities(post.getEntities())
                        .shareInfo(shareInfo)

                        .bookmarkCount(post.getStat().getBookmarkCount())
                        .bookmarked(false)
                        .likeCount(post.getStat().getLikesCount())
                        .liked(liked)
                        .repostCount(post.getStat().getRepostCount())
                        .reposted(reposted)
                        .commentCount(post.getStat().getCommentsCount())
                        .quoteCount(0)
                        .build())
                .build();
    }

    public PostDetail createFrom(Post post, String viewerUserId) {
        return createFrom(post, viewerUserId, 0); // 처음 호출할 때 depth는 0부터 시작
    }
}
