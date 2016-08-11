package lam.project.foureventuserdroid.utils.complete_profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Cateogory;

/**
 * Created by spino on 10/08/16.
 */
public class CategoryManager{

    private static CategoryManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private List<Cateogory> mFavouriteCache;

    private boolean mDirty;


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

    public List<Cateogory> getFavouriteCategories(){

        if(!mDirty && mFavouriteCache != null){

            return mFavouriteCache;
        }

        final String categoriesString = mSharedPreferences.getString(Cateogory.Keys.CATEGORY,null);

        if (categoriesString != null){

            try{

                List<Cateogory> tmpCategories = new LinkedList<>();
                JSONArray categoriesJson = new JSONArray(categoriesString);

                for(int i=0; i<categoriesJson.length(); i++){

                    JSONObject categoryJson = new JSONObject(categoriesJson.getJSONObject(i).toString());
                    Cateogory cateogory = Cateogory.fromJson(categoryJson);

                    tmpCategories.add(cateogory);
                }

                mFavouriteCache = tmpCategories;
            }
            catch (JSONException je){

                je.printStackTrace();
            }
        }else{

            mFavouriteCache = new LinkedList<>();
        }

        mDirty = false;
        return mFavouriteCache;
    }

    public boolean AddFavourite(@NonNull final Cateogory newCategory){

        List<Cateogory> currentFavourite = getFavouriteCategories();

        if(currentFavourite == null){

            currentFavourite = new LinkedList<>();
        }

        int duplicateIndex = -1;

        for(int i=0; i<currentFavourite.size(); i++){

            final Cateogory item = currentFavourite.get(i);
            if(item.id == newCategory.id){

                duplicateIndex = i;
                break;
            }
        }

        if(duplicateIndex > 0){

            mFavouriteCache.remove(duplicateIndex);
            mDirty = false;

            return save();
        }else{
            currentFavourite.add(newCategory);

            return save();
        }
    }

    public boolean save(){

        if (mFavouriteCache != null){

            try{
                final JSONArray array = new JSONArray();

                for(Cateogory cateogory : mFavouriteCache){

                    JSONObject item = cateogory.toJson();
                    array.put(item);
                }

                final String arrayAsString = array.toString();
                return mSharedPreferences.edit().putString(Cateogory.Keys.CATEGORY,
                        arrayAsString).commit();

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }
}
