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
    public static String ORDER_TYPE = "asc";
    public static String ORDER_COLUMN = "full_name";
    public static float RADIUS = (float) 10.0;
    public static boolean isCurrentMember = false;
    public static boolean isMenuOnPosition = false;
    public static int MENU_POSITION = 0;

    // Getting the API Key for the app by registering it at Google Cloud Console
    // NOM DU PROJET : HQ project
    // IDENTIFIANT DU PROJET : weighty-wonder-819
}
