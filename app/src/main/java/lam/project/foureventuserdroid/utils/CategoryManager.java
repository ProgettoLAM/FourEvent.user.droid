package lam.project.foureventuserdroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Cateogory;
import lam.project.foureventuserdroid.model.User;

/**
 * Created by spino on 10/08/16.
 */
public class CategoryManager{

    private static CategoryManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private List<Cateogory> mCacheCategories;

    private CategoryManager(final Context context){

        String SHARED_PREFERENCES_NAME = context.getResources().getString(R.string.shared_preferences_name);

        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static CategoryManager get(Context context){

        if(sInstance == null){

            sInstance = new CategoryManager(context);
        }

        return sInstance;
    }

    public static CategoryManager get(){

        if(sInstance == null){

            throw new IllegalStateException("Invoke get(context) before!");
        }

        return sInstance;
    }

    //TODO verificare il corretto funzionamento del metodo getCategories

    public List<Cateogory> getCategories(){

        if(mCacheCategories != null){

            return mCacheCategories;
        }

        final String categoriesString = mSharedPreferences.getString(Cateogory.Keys.CATEGORY,null);

        if (categoriesString != null){

            try{

                List<Cateogory> tmpCategories = new ArrayList<>();
                JSONArray categoriesJson = new JSONArray(categoriesString);

                for(int i=0; i<categoriesJson.length(); i++){

                    JSONObject categoryJson = new JSONObject(categoriesJson.getJSONObject(i).toString());
                    Cateogory cateogory = Cateogory.fromJson(categoryJson);

                    tmpCategories.add(cateogory);
                }

                mCacheCategories = tmpCategories;
            }
            catch (JSONException je){

                je.printStackTrace();
            }
        }

        return mCacheCategories;
    }

    //TODO completare il metodo save

    public boolean save(@NonNull final List<Cateogory> cateogories){

        mCacheCategories = cateogories;

        return false;
    }
}
