package co.geeksters.hq.global.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.geeksters.hq.global.GlobalVariables;

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
        if(networkInfo == null) {
            return false;
        } else
            return true;
    }

    public static boolean isNetworkEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isGPSEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isEmailValid(String email) {

        Pattern pattern = Pattern.compile("[A-Z0-9a-z._%+-]{3,}+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");

        Matcher matcher = pattern.matcher(email);

        boolean matchFound = matcher.matches();

        return matchFound;
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

    public static String firstToUpper(String string) {
        if(string == null || string.equals("")) return string;
        else return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String distanceToKilometer(float distance) {
        String distanceToKilometer = "";

        if (distance >= 1000) {
            float newDistance = distance/1000;
            if(newDistance == (int) newDistance)
                distanceToKilometer += (int) newDistance + " Km";
            else
                distanceToKilometer += newDistance + " Km";
        } else {
            distanceToKilometer += (int) distance + " m";
        }

        return distanceToKilometer;
    }

    public static String intervalToKilometer(String interval) {
        String intervalToKilometer = "";

        String[] intervalValues = interval.split("-");

        if(!intervalValues[0].equals("") && !intervalValues[1].equals(""))
            intervalToKilometer += distanceToKilometer(Integer.valueOf(intervalValues[0]))
                                 + " - "
                                 + distanceToKilometer(Integer.valueOf(intervalValues[1]));

        return intervalToKilometer;
    }

    public static String distanceBetweenValues(int min, int max) {
        return min + "-" + max;
    }

    public static String distanceByInterval(float distance) {

        String interval = "";

        if(distance>=0 && distance<=2)
        {
            interval = "0-2km";
        }
        else if(distance>2 && distance<=4)
        {
            interval = "2-4km";
        }
        else if(distance>4 && distance<=6)
        {

            interval = "4-6km";
        }

//
//        String interval = "";
//        int i = GlobalVariables.MAX_SLICE_NUMBER;
////        setSliceNumber();
//
//        while(i > 0 && distance <= GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER * i) {
//            if (distance >= (i - 1) * GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER && distance <= i * GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER) {
//                interval = distanceBetweenValues((i - 1) * GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER, i * GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER);
//
//                break;
//            }
//
//            i -= 1;
//        }

        return interval;
    }

    public static int setSliceNumber() {
        GlobalVariables.MAX_SLICE_NUMBER = (int) ((GlobalVariables.RADIUS * 1000) / GlobalVariables.MAX_INTERVAL_DISTANCE_FINDER) + 1;
        return GlobalVariables.MAX_SLICE_NUMBER;
    }

    public static Bitmap decodeUri(Context context, Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 500 ,500, true);
        Bitmap resizedWithoutOritation = fixImageOrientation(selectedImage.getPath(),resized);

        return resizedWithoutOritation;
    }

    public static Bitmap fixImageOrientation(String path,Bitmap bitmap){
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            //fixOrientation(bitmap);

        }
        return bitmap;
    }


    // Makes HttpURLConnection and returns InputStream
    public static InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }
}