package jd7337.socialcontract.model;

import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TwitterUserService {

    @GET("/1.1/users/show.json")
    Call<User> show(@Query("user_id") long id);

}
