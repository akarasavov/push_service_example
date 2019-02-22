package hermes.model;

public class UserDataHolder {

    public final User user;
    public final UserPushRequest userPushRequest;

    public UserDataHolder(User user, UserPushRequest userPushRequest) {
        this.user = user;
        this.userPushRequest = userPushRequest;
    }
}
