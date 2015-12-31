package co.geeksters.cafe_ami.events.success;

import java.util.ArrayList;

import co.geeksters.cafe_ami.models.EmailResonse;

/**
 * Created by soukaina on 26/12/14.
 */
public class PasswordResetEvent {
    public ArrayList<EmailResonse> emailsResponse;

    public PasswordResetEvent(ArrayList<EmailResonse> emailsResponse){
        this.emailsResponse = emailsResponse;
    }
}
