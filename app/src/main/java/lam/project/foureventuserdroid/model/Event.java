package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * Created by spino on 28/07/16.
 */
public class Event implements Parcelable{

    public final String mTitle;

    public final String mDescription;

    public final String mDate;

    public final String mTag;

    public final String mAddress;

    public final float mLatitude;

    public final float mLongitude;

    public final String mPrice;

    public final int mParticipation;

    public String mImage;

    private Event(final String title, final String description, final String date, final String tag, final String address,
                  final float latitude, final float longitude, final String price, final int participation,
                  final String image){

        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mTag = tag;
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mPrice = price;
        this.mParticipation = participation;
        this.mImage = image;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event addImage(String image) {
        this.mImage = image;
        return this;
    }


    protected Event(Parcel in) {

        boolean present = in.readByte() == Keys.PRESENT;

        mTitle = in.readString();
        mDescription = in.readString();
        mDate = in.readString();
        mTag = in.readString();
        mAddress = in.readString();
        mLatitude = in.readFloat();
        mLongitude = in.readFloat();
        mPrice = in.readString();
        mParticipation = in.readInt();

        if(present) {
            mImage = in.readString();

        }
    }


    public static Event fromJson(final JSONObject jsonObject) throws JSONException{

        final String title = jsonObject.getString(Keys.TITLE);
        final String description = jsonObject.getString(Keys.DESCRIPTION);
        final String date = jsonObject.getString(Keys.DATE);
        final String tag = jsonObject.getString(Keys.TAG);
        final String address = jsonObject.getString(Keys.ADDRESS);
        final String price = jsonObject.getString(Keys.PRICE);
        final int participation = jsonObject.getInt(Keys.PARTICIPATION);
        final float latitude = BigDecimal.valueOf(jsonObject.getDouble(Keys.LATITUDE)).floatValue();
        final float longitude = BigDecimal.valueOf(jsonObject.getDouble(Keys.LONGITUDE)).floatValue();

        Builder builder = Builder.create(title,description,date).withTag(tag).withAddress(address)
                .withLocation(latitude,longitude).withPrice(price).withParticipation(participation);

        if(jsonObject.has(Keys.IMAGE)) {
            builder.withImage(jsonObject.getString(Keys.IMAGE));

        }

        return builder.build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.TITLE, mTitle);
        jsonObject.put(Keys.DESCRIPTION, mDescription);
        jsonObject.put(Keys.DATE, mDate);
        jsonObject.put(Keys.TAG, mTag);
        jsonObject.put(Keys.ADDRESS, mAddress);
        jsonObject.put(Keys.LATITUDE, mLatitude);
        jsonObject.put(Keys.LONGITUDE, mLongitude);
        jsonObject.put(Keys.PRICE, mPrice);
        jsonObject.put(Keys.PARTICIPATION, mParticipation);

        if(mImage != null) {
            jsonObject.put(Keys.IMAGE, mImage);
        }

        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mDate);
        dest.writeString(mTag);
        dest.writeString(mAddress);
        dest.writeFloat(mLatitude);
        dest.writeFloat(mLongitude);
        dest.writeString(mPrice);
        dest.writeInt(mParticipation);

        if(mImage != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(mImage);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);
    }

    public static class Keys{

        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String DATE = "date";
        public static final String TAG = "tag";
        public static final String ADDRESS = "address";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String PRICE = "price";
        public static final String PARTICIPATION = "participation";
        public static final String IMAGE = "image";

        public static final Byte PRESENT = 1;
        public static final Byte NOT_PRESENT = 0;

        public static final String EVENT = "event";

    }


    public static class Builder{

        private String mTitle;
        private String mDescription;
        private String mDate;
        private String mTag;
        private String mAddress;
        private float mLatitude;
        private float mLongitude;
        private String mPrice;
        private int mParticipation;
        private String mImage;

        private Builder(final String title, final String description, final String date){

            this.mTitle = title;
            this.mDescription = description;
            this.mDate = date;
        }

        public static Builder create(final String title, final String description, final String date){

            return new Builder(title, description, date);
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

        public Builder withPrice(final String price) {
            this.mPrice = price;
            return this;
        }

        public Builder withParticipation(final int participation) {
            this.mParticipation = participation;
            return this;
        }

        public Builder withImage(final String image) {
            this.mImage = image;
            return this;
        }

        public Event build(){

            return new Event(mTitle, mDescription, mDate, mTag, mAddress, mLatitude, mLongitude, mPrice, mParticipation, mImage);
        }
    }
}
