package lam.project.foureventuserdroid.utils.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;

/**
 * Manager dell'utente
 */
public final class UserManager {

    private static UserManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private User mChacedUser;

    private UserManager(final Context context){

        String SHARED_PREFERENCES_NAME = context.getResources().getString(R.string.shared_preferences_name);

        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
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

    /**
     *
     * @return l'utente salvato, se presente nella cache
     */
    public User getUser(){

        if(mChacedUser != null){

            return mChacedUser;
        }

        final String userAsString = mSharedPreferences.getString(User.Keys.USER,null);

        if (userAsString != null){

            try{

                mChacedUser = User.fromJson(new JSONObject(userAsString));
            }
            catch (JSONException je){

                je.printStackTrace();
            }
        }

        return mChacedUser;
    }

    /**
     * Salvataggio delle utente tra le shared preferences
     * @param user utente passato
     * @return un booleano, se l'utente è stato salvato o no
     */
    public boolean save(@NonNull final User user){

        mChacedUser = user;

        try {

            JSONObject item = user.toJson();
            return mSharedPreferences.edit().putString(User.Keys.USER,item.toString()).commit();
        }
        catch (JSONException js){

            js.printStackTrace();
            return false;
        }

    }

    /**
     * Rimozione dell'utente, dopo il logout
     * @return un booleano, se l'utente è stato rimosso o no
     */
    public boolean remove() {

        mChacedUser = null;
        return mSharedPreferences.edit().remove(User.Keys.USER).commit();
    }
}
