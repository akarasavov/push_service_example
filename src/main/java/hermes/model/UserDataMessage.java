package hermes.model;

public class UserDataMessage {

    public final User user;
    public final UserPushRequest userPushRequest;
    public final boolean sent;

    public UserDataMessage(User user, UserPushRequest userPushRequest, boolean sent) {
        this.user = user;
        this.userPushRequest = userPushRequest;
        this.sent = sent;
    }

    @Override
    public String toString() {
        return "UserDataMessage{" +
            "user=" + user +
            ", userPushRequest=" + userPushRequest +
            ", sent=" + sent +
            '}';
    }
}
