package hermes.service;

import hermes.model.User;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.processors.PublishProcessor;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserRepository {

    private PublishProcessor<User> userPublishProcessor = PublishProcessor.create();

    public Flowable<User> subscribeOnUsersData() {
        return userPublishProcessor;
    }

    public Single<List<User>> askForUserIds(Set<UUID> userIds) {
        return executeNetworkCall(userIds);
    }

    private Single<List<User>> executeNetworkCall(Set<UUID> userIds) {
        return Single.create(emitter -> {
            new Thread(() -> {
                networkDelay();
                final List<User> result = userIds.stream().map(User::new).collect(Collectors.toList());
                emitter.onSuccess(result);
            }).start();
        });

    }

    private void networkDelay() {
        try {
            Thread.sleep(new Random(500).nextLong());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
