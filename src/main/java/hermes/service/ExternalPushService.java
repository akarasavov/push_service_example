package hermes.service;

import hermes.model.User;
import io.reactivex.Single;

public interface ExternalPushService {

    Single<Boolean> send(User user, String pushMessage);
}
