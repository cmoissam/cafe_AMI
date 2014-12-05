package co.geeksters.hq.services;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class BaseService {

    //public static String END_POINT_URL = "http://hq-api.com/api/v1";

    public static String END_POINT_URL = "http://192.168.0.8:3000";

    public static RestAdapter adapterWithoutToken() {

            RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }

    public static RestAdapter adapterWithToken(final String token) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addQueryParam("access_token", token);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }
}