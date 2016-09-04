package lam.project.foureventuserdroid.utils.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import lam.project.foureventuserdroid.R;
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
                mEventCache = new LinkedList<>();
            }
        }else{

            mEventCache = new LinkedList<>();
        }

        mDirty = false;
        return mEventCache;
    }

    public Event saveOrRemoveEvent(final Event event) {

        getFavouriteEvents();

        int itemId = -1;

        for(int i=0; i<mEventCache.size(); i++) {

            if(mEventCache.get(i).mId.equals(event.mId)) {

                itemId = i;
                break;
            }
        }

        if(itemId > -1) {

            event.mIsPreferred = false;
            mEventCache.remove(itemId);

        } else {

            event.mIsPreferred = true;
            mEventCache.add(event);
        }

        save();
        return event;
    }

    public boolean save() {

        if (mEventCache != null){

            try{
                final JSONArray array = new JSONArray();

                for(Event event : mEventCache){

                    JSONObject item = event.toJson();
                    array.put(item);
                }

                final String arrayAsString = array.toString();
                mDirty = true;
                return mSharedPreferences.edit().putString(Event.Keys.EVENT,
                        arrayAsString).commit();

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public boolean removeAll() {

        mEventCache = null;
        return mSharedPreferences.edit().remove(Event.Keys.EVENT).commit();
    }
}
