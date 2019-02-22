package hermes.service;

import hermes.model.UserDataHolder;
import hermes.model.UserPushRequest;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserPushService {

    private final PushService pushService;
    private final UserRepository userRepository;

    private PublishProcessor<UserPushRequest> requestPublishProcessor = PublishProcessor.create();

    public UserPushService(PushService pushService, UserRepository userRepository) {
        this.pushService = pushService;
        this.userRepository = userRepository;

        subscribeOnUserPushRequests();
    }

    public void sendPush(UserPushRequest userPushRequest) {
        requestPublishProcessor.onNext(userPushRequest);
    }

    private Disposable subscribeOnUserPushRequests() {
        return requestPublishProcessor.buffer(100)
            .map(r -> {
                final Map<UUID, List<UserPushRequest>> userPushRequestMap = r.stream().collect(Collectors.groupingBy(k -> k.userId));
                return getUserData(userPushRequestMap);
            })
            .flatMap(Single::toFlowable)
            .subscribeOn(Schedulers.computation())
            .subscribe(this::processUserPushRequests);
    }

    private Single<List<UserDataHolder>> getUserData(Map<UUID, List<UserPushRequest>> userPushRequestMap) {
        return userRepository.askForUserIds(userPushRequestMap.keySet())
            .subscribeOn(Schedulers.io())
            .map(userIds -> userIds.stream()
                .filter(ud -> userPushRequestMap.get(ud.userId) != null)
                .map(user -> userPushRequestMap.get(user.userId).stream().map(ur -> new UserDataHolder(user, ur))
                    .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }

    private void processUserPushRequests(List<UserDataHolder> userDataHolders) {
        //TODO  - notify push service
    }
}
