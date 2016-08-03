package lam.project.foureventuserdroid.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by spino on 28/07/16.
 */
public class Event {

    public final String mTitle;

    public final String mDate;

    public final String mTag;

    public final String mAddress;

    public final float mLatitude;

    public final float mLongitude;

    public static Event fromJson(final JSONObject jsonObject) throws JSONException{

        final String title = jsonObject.getString(Keys.TITLE);
        final String date = jsonObject.getString(Keys.DATE);
        final String tag = jsonObject.getString(Keys.TAG);
        final String address = jsonObject.getString(Keys.ADDRESS);

        final float latitude = BigDecimal.valueOf(jsonObject.getDouble(Keys.LATITUDE)).floatValue();
        final float longitude = BigDecimal.valueOf(jsonObject.getDouble(Keys.LONGITUDE)).floatValue();

        return Builder.create(title,date).withTag(tag).withAddress(address)
                .withLocation(latitude,longitude).build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.TITLE, mTitle);
        jsonObject.put(Keys.DATE, mDate);
        jsonObject.put(Keys.TAG, mTag);
        jsonObject.put(Keys.ADDRESS, mAddress);
        jsonObject.put(Keys.LATITUDE, mLatitude);
        jsonObject.put(Keys.LONGITUDE, mLongitude);

        return jsonObject;
    }

    public static class Keys{

        public static final String TITLE = "title";
        public static final String DATE = "date";
        public static final String TAG = "tag";
        public static final String ADDRESS = "address";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";

    }

    private Event(final String title, final String mDate, final String tag, final String address,
                  final float latitude, final float longitude){

        this.mTitle = title;
        this.mDate = mDate;
        this.mTag = tag;
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public static class Builder{

        private String mTitle;
        private String mDate;
        private String mTag;
        private String mAddress;
        private float mLatitude;
        private float mLongitude;

        private Builder(final String title, final String date){

            this.mTitle = title;
            this.mDate = date;
        }

        public static Builder create(final String title, final String date){

            return new Builder(title,date);
        }

        public Builder withTag(final String tag){

            this.mTag = tag;
            return this;
        }

        public Builder withAddress(final String address){

            this.mAddress = address;
            return this;
        }

        public Builder withLocation(final float latitude, final float longitude){

            this.mLatitude = latitude;
            this.mLongitude = longitude;
            return this;
        }

        public Event build(){

            return new Event(mTitle, mDate, mTag, mAddress, mLatitude, mLongitude);
        }
    }
}
