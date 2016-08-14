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
import lam.project.foureventuserdroid.model.Event;

/**
 * Created by Vale on 14/08/2016.
 */

public final class FavouriteManager {

    private static FavouriteManager sInstance;

    private final SharedPreferences mSharedPreferences;

    private List<Event> mEventCache;

    private boolean mDirty;

    private FavouriteManager(final Context context){

        String SHARED_PREFERENCES_NAME = context.getResources().getString(R.string.shared_preferences_name);

        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static FavouriteManager get(Context context){

        if(sInstance == null){

            sInstance = new FavouriteManager(context);
        }

        return sInstance;
    }

    public static FavouriteManager get(){

        if(sInstance == null){

            throw new IllegalStateException("Invoke get(context) before!");
        }

        return sInstance;
    }

    public List<Event> getFavouriteEvents(){

        if(!mDirty && mEventCache != null){

            return mEventCache;
        }

        final String eventsString = mSharedPreferences.getString(Event.Keys.EVENT,null);

        if (eventsString != null){

            try{

                List<Event> tmpEvents = new LinkedList<>();
                JSONArray eventsJson = new JSONArray(eventsString);

                for(int i=0; i<eventsJson.length(); i++){

                    JSONObject eventJson = new JSONObject(eventsJson.getJSONObject(i).toString());
                    Event event = Event.fromJson(eventJson);

                    tmpEvents.add(event);
                }

                mEventCache = tmpEvents;
            }
            catch (JSONException je){

                je.printStackTrace();
            }
        }else{

            mEventCache = new LinkedList<>();
        }

        mDirty = false;
        return mEventCache;
    }

    public boolean removeEvent(@NonNull final Event newEvent){

        List<Event> currentEvent = getFavouriteEvents();

        if(currentEvent == null){

            currentEvent = new LinkedList<>();
        }

        int duplicateIndex = -1;

        for(int i=0; i<currentEvent.size(); i++){

            final Event item = currentEvent.get(i);
            if(item.mTitle.equals(newEvent.mTitle)){

                duplicateIndex = i;
                break;
            }
        }

        if(duplicateIndex > -1){

            mEventCache.remove(duplicateIndex);
            mDirty = false;

        }
        return false;
    }

    public boolean save(@NonNull final Event event) {

        if(mEventCache == null) {
            mEventCache = new LinkedList<>();
        }

            try {
                final JSONArray array = new JSONArray();

                JSONObject item = event.toJson();
                array.put(item);

                mEventCache.add(event);

                final String arrayAsString = array.toString();
                return mSharedPreferences.edit().putString(Event.Keys.EVENT,
                        arrayAsString).commit();

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

    }
}
