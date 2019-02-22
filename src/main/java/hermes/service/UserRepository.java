package hermes.service;

import hermes.model.DeviceBrand;
import hermes.model.User;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserRepository {

    private Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public Single<List<User>> getUsers(Set<UUID> userIds) {
        return executeNetworkCall(userIds);
    }

    private Single<List<User>> executeNetworkCall(Set<UUID> userIds) {
        return Single.create(emitter -> {
            final Disposable disposable = Schedulers.io().createWorker().schedule(() -> {
                logger.info("receive request {}", userIds);
                networkDelay();
                final DeviceBrand deviceBrand = new Random().nextInt() % 2 == 0
                                                ? DeviceBrand.Apple
                                                : DeviceBrand.Android;
                final List<User> result = userIds.stream().map(userId -> new User(userId, deviceBrand)).collect(Collectors.toList());
                emitter.onSuccess(result);
                logger.info("send users data for - {}", userIds);
            });

            emitter.setDisposable(disposable);
        });

    }

    private void networkDelay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
