package lam.project.foureventuserdroid.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spino on 28/07/16.
 */
public class Event {

    public final String title;

    public final String date;

    public final String tag;

    public final String address;

    public final float latitude;

    public final float longitude;

    private Event(final String title, final String date, final String tag, final String address,
                  final float latitude, final float longitude){

        this.title = title;
        this.date = date;
        this.tag = tag;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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

        public static Builder create(final String id, final String date){

            return new Builder(id,date);
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

    public static Event fromJson(JSONObject json) {
        Event event = null;
        try {
            event = Builder.create(json.getString("title"), json.getString("date"))
                    .withAddress(json.getString("address"))
                    .withTag(json.getString("tag"))
                    .build();

        }
        catch (JSONException ex) {

        }
        return event;

    }

}
