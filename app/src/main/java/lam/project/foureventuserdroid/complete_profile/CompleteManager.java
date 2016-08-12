package lam.project.foureventuserdroid.complete_profile;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Cateogory;

/**
 * Created by spino on 12/08/16.
 */
public class CompleteManager {

    private static CompleteManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private final String STEP = "step";


    private CompleteManager(final Context context){

        String SHARED_PREFERENCES_NAME = context.getResources().getString(R.string.shared_preferences_name);

        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static CompleteManager get(Context context){

        if(sInstance == null){

            sInstance = new CompleteManager(context);
        }

        return sInstance;
    }

    public static CompleteManager get(){

        if(sInstance == null){

            throw new IllegalStateException("Invoke get(context) before!");
        }

        return sInstance;
    }

    public int getStep(){

        return mSharedPreferences.getInt(STEP,0);
    }

    public boolean setStep(int step){

        return mSharedPreferences.edit().putInt(STEP,step).commit();
    }
}
