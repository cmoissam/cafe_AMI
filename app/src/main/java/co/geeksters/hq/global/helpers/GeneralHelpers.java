package co.geeksters.hq.global.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
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
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        } else
            return true;
    }

    public static boolean isGPSEnabled(Context context){
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static boolean isPasswordConfirmed(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public static String formatActualDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public static String firstToUpper(String string){
        if(string == null || string.equals("")) return string;
        else return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String distanceToKilometer(float distance) {
        String distanceToKilometer = "";

        if (distance >= 1000) {
            distanceToKilometer = (float) distance/1000 +" Km";
        } else {
            distanceToKilometer = distance +" m";
        }

        return distanceToKilometer;


    }
}
