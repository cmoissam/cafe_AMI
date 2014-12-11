package co.geeksters.hq.services;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class BaseService {

    public static String END_POINT_URL = "http://192.168.0.8:8000/api/v1";
    // public static String END_POINT_URL = "http://75371954.ngrok.com/api/v1";

    public static RestAdapter adapterWithoutToken() {

            RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                    }
                })
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
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("access_token", token);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }
}