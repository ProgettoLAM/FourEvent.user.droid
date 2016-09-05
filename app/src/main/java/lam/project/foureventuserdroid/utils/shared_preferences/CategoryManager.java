package lam.project.foureventuserdroid.utils.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Category;

/**
 * Manager delle categorie
 */
public class CategoryManager {

    private static CategoryManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private List<Category> mFavouriteCache;

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

    /**
     *
     * @return lista di categorie preferite
     */
    public List<Category> getFavouriteCategories(){

        //Se è presente una cache delle categorie, ritorna questa
        if(!mDirty && mFavouriteCache != null){

            return mFavouriteCache;
        }

        final String categoriesString = mSharedPreferences.getString(Category.Keys.CATEGORY,null);

        //Se l'utente ha scelto delle categorie, vengono inserite in una lista
        if (categoriesString != null){

            try{

                List<Category> tmpCategories = new LinkedList<>();
                JSONArray categoriesJson = new JSONArray(categoriesString);

                for(int i=0; i<categoriesJson.length(); i++){

                    JSONObject categoryJson = new JSONObject(categoriesJson.getJSONObject(i).toString());
                    Category category = Category.fromJson(categoryJson);

                    tmpCategories.add(category);
                }

                mFavouriteCache = tmpCategories;
            }
            catch (JSONException je){

                je.printStackTrace();
            }
        }
        //Altrimenti viene inizializzata la cache
        else{

            mFavouriteCache = new LinkedList<>();
        }

        mDirty = false;
        return mFavouriteCache;
    }

    /**
     * Aggiungere o rimuovere le categorie dalla cache
     * @param newCategory la categoria scelta dall'utente
     * @return un booleano, se è stato aggiunto o rimosso
     */
    public boolean AddOrRemoveFavouriteInCache(@NonNull final Category newCategory){

        getFavouriteCategories();

        if(mFavouriteCache == null){

            mFavouriteCache = new LinkedList<>();
        }

        int duplicateIndex = -1;

        for(int i=0; i<mFavouriteCache.size(); i++){

            final Category item = mFavouriteCache.get(i);

            if(item.id == newCategory.id){

                duplicateIndex = i;
                break;
            }
        }

        if(duplicateIndex > -1){

            mDirty = false;

            return mFavouriteCache.remove(duplicateIndex) != null;

        }
        else{

            return mFavouriteCache.add(newCategory);
        }
    }

    /**
     * Salvataggio della cache delle categorie
     * @return un booleano, se il salvataggio è avvenuto o no
     */
    public boolean save(){

        if (mFavouriteCache != null){

            try{
                final JSONArray array = new JSONArray();

                for(Category category : mFavouriteCache){

                    JSONObject item = category.toJson();
                    array.put(item);
                }

                final String arrayAsString = array.toString();
                return mSharedPreferences.edit().putString(Category.Keys.CATEGORY,
                        arrayAsString).commit();

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * Rimozione di tutta la cache
     * @return un booleano, se la rimozione è avvenuta o no
     */
    public boolean removeAll(){

        mFavouriteCache = null;
        return mSharedPreferences.edit().remove(Category.Keys.CATEGORY).commit();
    }
}
