package co.geeksters.cafe_ami.events.success;

/**
 * Created by geeksters on 19/10/15.
 */
public class UploadImageEvent {

    public String image;

    public UploadImageEvent(String image){
        this.image = image;
    }
}
