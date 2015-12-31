package co.geeksters.cafe_ami.global;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by soukaina on 26/11/14.
 */
public class Config {
    public static  String FACEBOOK_API_KEY = "1586111191604185";
    public static  String FACEBOOK_API_SECRET = "3660b955863652908fd5a81f37a25155";

    static Map config = new HashMap();

    public static Map setCloudinaryConfiguration() {
        config.put("cloud_name", "dbrnidhop");
        config.put("api_key", "518561242124556");
        config.put("api_secret", "pOg1Mda-wMISCmLV2sSQmKIXsAY");

        return config;
    }

}
