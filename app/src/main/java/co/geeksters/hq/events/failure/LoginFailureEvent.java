package co.geeksters.hq.events.failure;

/**
 * Created by soukaina on 22/12/14.
 */
public class LoginFailureEvent {

    public String errorMessage;

    public LoginFailureEvent(String message){

        this.errorMessage = message;
    }
}
