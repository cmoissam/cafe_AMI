package co.geeksters.hq.global;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import co.geeksters.hq.models.Member;

/**
 * Created by soukaina on 26/12/14.
 */

public class GlobalVariables {
    // Radar configuration
    public static int MAX_SLICE_NUMBER = 5;
    public static final int MAX_INTERVAL_DISTANCE_FINDER = 1000;
    public static float RADIUS = (float) 6.0;

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

    public static Menu menu = null;
    public static List<String> emails;
    public static int indexPreference = 0;
    public static List<Member> membersAroundMe = new ArrayList<Member>();

    public static float listViewPostsHeight = 0;

    public static int fromPaginationDirectory = 0;
    public static int commentClickedIndex = 0;
    public static int postClickedIndex = -1;
    public static boolean onDeleteComment = false;
    public static boolean onReply = false;
    public static boolean onClickComment = false;
    public static boolean inMarketPlaceFragment = false;
    public static boolean inMyTodosFragment = false;
    public static boolean notifiyedByPost = false;
    public static int notificationPostId = -1;
    public static boolean notifiyedByTodo = false;
    public static boolean notifiyedByInterestsOnPost = false;

    public static boolean inRadarFragement = false;
    public static boolean radarLock = true;
    public static boolean listRadarLock = true;
    public static boolean updatePosition = false;
    public static boolean updatePositionFromRadar = false;
    public static boolean getPeopleAroundMe = false;
    public static boolean sessionExpired = false;
    public static boolean replyFromMyMarket = false;
    public static boolean replyToAll = false;
    public static boolean inMyProfileFragment = false;
    public static boolean commentClicked = false;
    public static boolean isInMyProfileFragmentFromOpportunities = false;


    public static List<Member> lastMemberSearchPeopleDirectory = new ArrayList<Member>();
    public static  String lastSearchPeopleDirectory;
    public static Boolean backtosearch = false;

    public static int width = 0;
    public static  int height = 0;

    public static Member actualMember = null;

    public static int menuPart = 0;
    public static int menuDeep = 0;

    public static boolean needReturnButton = false;

    public static float d;
    public static Typeface typeface;
    // Notifications constants
    public static String PROJECT_NUMBER = "773290153741";
    public  static Activity activity = null;

    // TODO GET IMAGE FROM MAC

    public static String UrlApiImage = "http://104.131.22.196/images/";
    public static String UrlApi = "http://104.131.22.196/api/v1";


    // Getting the API Key for the app by registering it at Google Cloud Console
    // NOM DU PROJET : Thousand Network project
    // IDENTIFIANT DU PROJET : weighty-wonder-819
}
