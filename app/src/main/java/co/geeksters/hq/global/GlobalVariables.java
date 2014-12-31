package co.geeksters.hq.global;

import com.facebook.android.Facebook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soukaina on 26/12/14.
 */
public class GlobalVariables {
    public static Facebook facebook = new Facebook(Config.FACEBOOK_API_KEY);
    public static List<String> emails;
    public static int SEARCH_SIZE = 10;
}
