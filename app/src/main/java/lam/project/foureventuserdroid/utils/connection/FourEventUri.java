package lam.project.foureventuserdroid.utils.connection;

import android.net.Uri;

/**
 * Created by spino on 20/08/16.
 */

public class FourEventUri {

    private static final String BASE = "http://annina.cs.unibo.it:8080/api/";
    private static final String USER = "user";

    public static String getUserUri(String path) {

        return Uri.parse(BASE).buildUpon()
                .appendPath(USER)
                .appendPath(path).build().toString();
    }
}
