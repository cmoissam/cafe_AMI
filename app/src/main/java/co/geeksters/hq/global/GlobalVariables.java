package co.geeksters.hq.global;

import com.facebook.android.Facebook;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.models.Hub;

/**
 * Created by soukaina on 26/12/14.
 */
public class GlobalVariables {
    public static final int MAX_INTERVAL_DISTANCE_FINDER = 500;
    public static Facebook facebook = new Facebook(Config.FACEBOOK_API_KEY);
    public static List<String> emails;
    public static int SEARCH_SIZE = 10;
    public static String ORDER_TYPE = "asc";
    public static String ORDER_COLUMN = "full_name";
    public static float RADIUS = (float) 1000.0;
    public static boolean isCurrentMember = false;
    public static boolean isMenuOnPosition = false;
    public static int MENU_POSITION = 0;
    public static int LIMIT_NUMBER_OF_LAST_SEARCHED_HUB = 5;
    public static List<Hub> lastSharedHubs = new ArrayList<Hub>();
    public static int indexPreference = 0;

    // Getting the API Key for the app by registering it at Google Cloud Console
    // NOM DU PROJET : HQ project
    // IDENTIFIANT DU PROJET : weighty-wonder-819
}
