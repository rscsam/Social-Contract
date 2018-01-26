package jd7337.socialcontract.controller.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ali Khosravi on 1/22/2018.
 */

public class RestClient {

    public static jd7337.socialcontract.controller.rest.RetrofitService getRetrofitService() {
        return new Retrofit.Builder()
                .baseUrl(jd7337.socialcontract.controller.Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(jd7337.socialcontract.controller.rest.RetrofitService.class);
    }
}
