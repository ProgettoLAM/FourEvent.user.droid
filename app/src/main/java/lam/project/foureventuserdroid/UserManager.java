package lam.project.foureventuserdroid;

import android.content.Context;
import android.content.SharedPreferences;

import lam.project.foureventuserdroid.model.User;

/**
 * Created by spino on 30/07/16.
 */
public final class UserManager {

    private static UserManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private UserManager(final Context context){

        mSharedPreferences = context.getSharedPreferences(User.Keys.USER,context.MODE_PRIVATE);
    }

    public static UserManager get(Context context){

        if(sInstance == null){

            sInstance = new UserManager(context);
        }

        return sInstance;
    }

    public static UserManager get(){

        if(sInstance == null){

            throw new IllegalStateException("Invoke get(context) before!");
        }

        return sInstance;
    }
}
