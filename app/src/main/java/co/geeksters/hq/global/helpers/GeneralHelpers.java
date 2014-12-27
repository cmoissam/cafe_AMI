package co.geeksters.hq.global.helpers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by soukaina on 11/12/14.
 */
public class GeneralHelpers {

    public static String generateEmailsStringFromList(List<String> emails){
        String emailsString = "";

        for(int i=0; i<emails.size(); i++){
            emailsString += "," + emails.get(i);
        }

        return emailsString;
    }

    public static List<String> generateEmailsListFromString(String emails){
        List<String> emailsList = Arrays.asList(emails.trim().split(","));

        return emailsList;
    }

    public static String generateIdsStringFromList(ArrayList<Integer> ids){
        String idsString = "";

        for(int i=0; i<ids.size(); i++){
            idsString += "," + ids.get(i);
        }

        return idsString;
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            return false;
        } else
            return true;
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static boolean isPasswordConfirmed(String password, String passwordConfirmation) {
        Boolean test = password.equals(passwordConfirmation);
        return password.equals(passwordConfirmation);
    }

    public static String emptyString(String emptyString){
        if(emptyString == null)
            return "";
        else
            return emptyString;
    }

    public static String formatActualDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

}
