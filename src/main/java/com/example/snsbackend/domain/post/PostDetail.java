package com.example.snsbackend.domain.post;

import com.example.snsbackend.model.post.PostEntities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class PostDetail {
    private String id;
    private String type;
    private PostInfo postInfo;
    private User user;

    @Data
    @Builder
    public static class PostInfo {
        private boolean isPostUnavailable;
        private ShareInfo shareInfo;
        private int bookmarkCount;
        private boolean bookmarked;
        private int likeCount;
        private boolean liked;
        private int quoteCount;
        private int commentCount;
        private int repostCount;
        private boolean reposted;
        private LocalDateTime createAt;
        private PostEntities entities;
        private String content;

        @Data
        @Builder
        public static class ShareInfo {
            private PostDetail repostedPost;
            private boolean quotedPostUnavailable;
            private PostDetail quotedPost;
        }
    }

    @Data
    @Builder
    public static class User {
        @Data
        @Builder
        public static class FriendshipStatus {
            private boolean muting;
            private boolean following;
            private boolean followedBy;
            private boolean outgoingRequest;
        }
        private String id;
        private LocalDateTime lastVisitedTime;
        private String profilePicUrl;
        private String username;
        private int followerCount;
        private boolean isPrivate;
        private FriendshipStatus friendshipStatus;
    }
}
