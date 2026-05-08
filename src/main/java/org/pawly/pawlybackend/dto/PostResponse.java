package org.pawly.pawlybackend.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class PostResponse {
    private UUID id;
    private String content;
    private String imageUrl;
    private Instant createdAt;
    private UserSummaryDto author;
    private int likeCount;
    private int replyCount;
    private UUID parentPostId;
    private boolean deleted;
}
