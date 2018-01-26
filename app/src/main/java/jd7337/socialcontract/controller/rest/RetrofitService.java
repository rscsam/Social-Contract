package jd7337.socialcontract.controller.rest;

import jd7337.socialcontract.controller.POJO.InstagramResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ali Khosravi on 1/22/2018.
 */


public interface RetrofitService {

    @GET("v1/tags/{tag_name}/media/recent")
    Call<InstagramResponse> getTagPhotos(@Path("tag_name") String tag_name,
                                         @Query("access_token") String access_token);
}

