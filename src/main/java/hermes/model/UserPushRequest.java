package hermes.model;

import java.time.Instant;
import java.util.UUID;

public class UserPushRequest {

    public final UUID userId;
    public final String message;
    public final long requestId;
    public final Instant creationTime;

    public UserPushRequest(UUID userId, String message, long requestId, Instant creationTime) {
        this.userId = userId;
        this.message = message;
        this.requestId = requestId;
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "UserPushRequest{" +
            "userId=" + userId +
            ", message='" + message + '\'' +
            ", requestId=" + requestId +
            ", creationTime=" + creationTime +
            '}';
    }
}
