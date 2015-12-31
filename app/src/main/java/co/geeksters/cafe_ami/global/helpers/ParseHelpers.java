package co.geeksters.cafe_ami.global.helpers;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by soukaina on 04/12/14.
 */
public class ParseHelpers {

    public static TypedInput createTypedInputFromModel(Object modelObject){
        TypedInput inputHttpRequest = null;
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        try {
            inputHttpRequest = new TypedByteArray("application/json", gson.toJson(modelObject).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static TypedInput createTypedInputFromModelByMethod(Object modelObject, String method){
        TypedInput inputHttpRequest = null;
        try {
            JSONObject modelJson = new JSONObject(new Gson().toJson(modelObject));
            modelJson.put("_method", method);

            inputHttpRequest = createTypedInputFromJsonObject(modelJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static TypedInput createTypedInputFromOneKeyValue(String key, Object value){
        TypedInput inputHttpRequest = null;
        try {
            JSONObject modelJson = new JSONObject();
            modelJson.put(key, value);

            inputHttpRequest = createTypedInputFromJsonObject(modelJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static TypedInput createTypedInputFromJsonObject(JSONObject object){
        TypedInput inputHttpRequest = null;
        try {
            inputHttpRequest = new TypedByteArray("application/json", object.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return inputHttpRequest;
    }

    public static String createJsonStringFromModel(Object model){
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.toJson(model);
    }

    public static JsonElement createJsonElementFromString(String model){
        JsonParser parser = new JsonParser();
        return parser.parse(model);
    }
}
