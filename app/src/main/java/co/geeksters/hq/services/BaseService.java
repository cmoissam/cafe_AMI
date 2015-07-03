package co.geeksters.hq.services;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class BaseService {

    public static String END_POINT_URL = "http://192.168.0.10:8000/api/v1";
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
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                        request.addQueryParam("access_token", token);
                    }
                })
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }
}