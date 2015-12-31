package co.geeksters.cafe_ami.global.helpers;

import com.google.gson.annotations.SerializedName;

/**
 * Created by soukaina on 02/02/15.
 */
public class RestError {
//    @SerializedName("code")
//    public int code;
    @SerializedName("error")
    public String errorDetails;
}