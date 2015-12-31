package co.geeksters.cafe_ami.models;

import java.io.Serializable;

/**
 * Created by soukaina on 10/12/14.
 */
public class Social implements Serializable {
    public int id;
    public String twitter = "";
    public String skype = "";
    public String facebook = "";
    public String linkedin = "";
    public String blog = "";
    public String website = "";
    public String other = "";

    public Social() { }

    public Social(int id){
        this.id = id;
    }
}
