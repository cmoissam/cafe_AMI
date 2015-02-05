package co.geeksters.hq.global;

import com.facebook.android.Facebook;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.models.Hub;
import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 26/12/14.
 */
public class GlobalVariables {
    // Radar configuration
    public static int MAX_SLICE_NUMBER = 5;
    public static final int MAX_INTERVAL_DISTANCE_FINDER = 30;
    public static float RADIUS = (float) 0.1;

    // Contact configuration
    public static Facebook facebook = new Facebook(Config.FACEBOOK_API_KEY);

    // Search configuration
    public static int SEARCH_SIZE = 10;
    public static String ORDER_TYPE = "asc";
    public static String ORDER_COLUMN = "full_name";

    // Last search number
    public static int LIMIT_NUMBER_OF_LAST_SEARCHED_HUB = 5;

    public static boolean isCurrentMember = false;
    public static boolean isMenuOnPosition = false;
    public static int MENU_POSITION = 0;
    public static boolean afterViewsRadar = true;
    public static boolean finderRadar = false;
    public static boolean finderList = false;
    public static boolean directory = false;
    public static boolean editMyInformation = false;
    public static boolean hubInformation = false;
    public static boolean hubMember = false;

    public static List<String> emails;
    public static int indexPreference = 0;
    public static List<Member> membersAroundMe = new ArrayList<Member>();

    public static float listViewPostsHeight = 0;

    // Getting the API Key for the app by registering it at Google Cloud Console
    // NOM DU PROJET : HQ project
    // IDENTIFIANT DU PROJET : weighty-wonder-819
}
