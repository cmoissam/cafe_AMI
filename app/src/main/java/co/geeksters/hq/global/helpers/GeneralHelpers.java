package co.geeksters.hq.global.helpers;

import java.util.ArrayList;

/**
 * Created by soukaina on 11/12/14.
 */
public class GeneralHelpers {

    public static String generateEmailsStringFromList(ArrayList<String> emails){
        String emailsString = "";

        for(int i=0; i<emails.size(); i++){
            emailsString += "," + emails.get(i);
        }

        return emailsString;
    }

    public static String generateIdsStringFromList(ArrayList<Integer> ids){
        String idsString = "";

        for(int i=0; i<ids.size(); i++){
            idsString += "," + ids.get(i);
        }

        return idsString;
    }
}
