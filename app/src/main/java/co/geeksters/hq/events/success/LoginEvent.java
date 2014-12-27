package co.geeksters.hq.events.success;

/**
 * Created by soukaina on 27/11/14.
 */
public class LoginEvent {

    public String accessToken;

    public LoginEvent(String accessToken) {
        this.accessToken = accessToken;
        // store this access token in a SharedPreferences
        // redirection (first login -> complete profile, else -> home page)
    }
}
