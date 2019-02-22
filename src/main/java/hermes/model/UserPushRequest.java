package hermes.model;

import java.util.UUID;

public class UserPushRequest {

    public final UUID userId;
    public final String message;
    public final long requestId;

    public UserPushRequest(UUID userId, String message, long requestId) {
        this.userId = userId;
        this.message = message;
        this.requestId = requestId;
    }
}
