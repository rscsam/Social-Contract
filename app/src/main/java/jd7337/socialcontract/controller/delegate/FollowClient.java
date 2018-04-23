package jd7337.socialcontract.controller.delegate;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class FollowClient extends TwitterApiClient {
    public FollowClient(TwitterSession session) {
        super(session);
    }

    public FollowService getCustomService() {
        return getService(FollowService.class);
    }

    public interface FollowService {
        @POST("/1.1/friendships/create.json")
        Call<User> follow(@Query("user_id") long id, @Query("follow") boolean tr);
    }
}


