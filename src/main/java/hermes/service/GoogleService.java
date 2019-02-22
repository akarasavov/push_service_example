package hermes.service;

import hermes.model.User;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class GoogleService implements ExternalPushService {

    private Logger logger = LoggerFactory.getLogger(GoogleService.class);

    @Override
    public Single<Boolean> send(User user, String pushMessage) {
        return Single.create(emitter -> {
            final Disposable disposable = Schedulers.io().createWorker().schedule(() -> {
                logger.info("receive request for user={} , message={}", user, pushMessage);
                networkDelay();
                final boolean result = new Random().nextInt() % 2 == 0;
                emitter.onSuccess(result);
                logger.info("send message successfully={}", result);
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
