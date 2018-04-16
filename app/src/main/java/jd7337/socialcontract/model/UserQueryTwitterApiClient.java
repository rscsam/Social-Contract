package jd7337.socialcontract.model;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

public class UserQueryTwitterApiClient extends TwitterApiClient {

    public UserQueryTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public TwitterUserService getTwitterUserService() {
        return getService(TwitterUserService.class);
    }

}
