package co.geeksters.hq.events.success;

/**
 * Created by soukaina on 27/11/14.
 */
public class LoginEvent {

    public String access_token;

    public LoginEvent(String access_token) {
        this.access_token = access_token;
        // store this access token in a SharedPreferences
        // redirection (first login -> complete profile, else -> home page)
    }
}
