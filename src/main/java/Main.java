import com.google.common.collect.ImmutableMap;
import hermes.model.UserPushRequest;
import hermes.service.ApnsService;
import hermes.service.GoogleService;
import hermes.service.UserPushService;
import hermes.service.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

import static hermes.model.DeviceBrand.Android;
import static hermes.model.DeviceBrand.Apple;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        UserPushService userPushService = new UserPushService(ImmutableMap.of(Android, new GoogleService(), Apple, new ApnsService()),
            new UserRepository(), 2);
        userPushService.start();

        long counter = 0;
        while (true) {
            final UUID userId = UUID.randomUUID();
            final UserPushRequest userPushRequest = new UserPushRequest(userId, "message for " + userId, counter++, Instant.now());
            logger.info("Client Send Request " + userPushRequest);

            userPushService.sendPush(userPushRequest);
            Thread.sleep(100);
        }
    }

}
