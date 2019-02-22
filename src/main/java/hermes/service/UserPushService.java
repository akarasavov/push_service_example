package hermes.service;

import hermes.model.DeviceBrand;
import hermes.model.User;
import hermes.model.UserDataMessage;
import hermes.model.UserPushRequest;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserPushService implements Cancellable {

    private final Map<DeviceBrand, ExternalPushService> pushServices;
    private final UserRepository userRepository;
    private final int bufferSize;
    private Disposable subscribeOnUserPushRequests;

    private PublishProcessor<UserPushRequest> requestPublishProcessor = PublishProcessor.create();
    private Logger logger = LoggerFactory.getLogger(UserPushService.class);

    public UserPushService(Map<DeviceBrand, ExternalPushService> pushServices, UserRepository userRepository,
                           int bufferSize) {
        this.bufferSize = bufferSize;
        this.pushServices = pushServices;
        this.userRepository = userRepository;

    }

    public void start() {
        this.subscribeOnUserPushRequests = subscribeOnUserPushRequests();
    }

    @Override
    public void cancel() {
        if (subscribeOnUserPushRequests != null) {
            subscribeOnUserPushRequests.dispose();
        }
    }

    public void sendPush(UserPushRequest userPushRequest) {
        requestPublishProcessor.onNext(userPushRequest);
    }

    private Disposable subscribeOnUserPushRequests() {
        final Flowable<UserDataMessage> userDataMessages = requestPublishProcessor.buffer(bufferSize)
            .map(r -> {
                final Map<UUID, List<UserPushRequest>> userPushRequestMap = r.stream().collect(Collectors.groupingBy(k -> k.userId));
                return getUserData(userPushRequestMap);
            })
            .flatMap(Single::toFlowable)
            .flatMap(this::executePushMessage);

        return userDataMessages
            .subscribeOn(Schedulers.computation())
            .subscribe(userDataMessage -> {
                logger.info("Receive " + userDataMessage);
            });
    }

    private Single<List<Pair<User, UserPushRequest>>> getUserData(Map<UUID, List<UserPushRequest>> userPushRequestMap) {
        return userRepository.getUsers(userPushRequestMap.keySet())
            .subscribeOn(Schedulers.io())
            .map(userIds -> userIds.stream()
                .filter(ud -> userPushRequestMap.get(ud.userId) != null)
                .map(user -> userPushRequestMap.get(user.userId)
                    .stream()
                    .map(ur -> Pair.of(user, ur))
                    .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }

    private Flowable<UserDataMessage> executePushMessage(List<Pair<User, UserPushRequest>> requests) {
        final List<Single<UserDataMessage>> singles = requests.stream().map(request ->
            pushServices.get(request.getKey().deviceBrand).send(request.getLeft(), request.getRight().message)
                .map(result -> new UserDataMessage(request.getLeft(), request.getRight(), result))
        ).collect(Collectors.toList());

        return Single.merge(singles);
    }
}
