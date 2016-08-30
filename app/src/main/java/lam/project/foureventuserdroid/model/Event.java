package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;

import lam.project.foureventuserdroid.utils.DateConverter;

public class Event implements Parcelable{

    public final String mId;

    public final String mTitle;

    public final String mDescription;

    public final String mAuthor;

    public final String mStartDate;

    public final String mEndDate;

    public final String mTag;

    public final String mAddress;

    public final float mLatitude;

    public final float mLongitude;

    public final String mPrice;

    public final int mMaxTicket;

    public int mParticipation;

    public boolean mIsPreferred;

    private final String mImage;

    private boolean mWillPartecipate;


    private Event(final String id, final String title, final String description, final String author,
                  final String startDate, final String endDate, final String tag, final String address,
                  final float latitude, final float longitude, final String price, final int participation,
                  final String image, final int maxTic, final boolean willPartecipate){

        this.mId = id;
        this.mTitle = title;
        this.mDescription = description;
        this.mAuthor = author;
        this.mStartDate = startDate;
        this.mEndDate = endDate;
        this.mTag = tag;
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mPrice = price;
        this.mParticipation = participation;
        this.mImage = image;
        this.mMaxTicket = maxTic;
        this.mWillPartecipate = willPartecipate;
    }

    public boolean isFree() {

        return mPrice.equals(Keys.FREE);
    }

    public void updateWillParticipate() {
        this.mWillPartecipate = !mWillPartecipate;
    }

    public boolean willPartecipate() {
        return this.mWillPartecipate;
    }

    //region lettura/scrittura JSON

    public static Event fromJson(final JSONObject jsonObject) throws JSONException{

        final String title = jsonObject.getString(Keys.TITLE);
        final String description = jsonObject.getString(Keys.DESCRIPTION);
        final String author = jsonObject.getString(Keys.AUTHOR);
        final String startDate = DateConverter.fromMillis(jsonObject.getLong(Keys.START_DATE));

        final String tag = jsonObject.getString(Keys.TAG);
        final String address = jsonObject.getString(Keys.ADDRESS);
        final String price = jsonObject.getString(Keys.PRICE);
        final String image = jsonObject.getString(Keys.IMAGE);
        final float latitude = BigDecimal.valueOf(jsonObject.getDouble(Keys.LATITUDE)).floatValue();
        final float longitude = BigDecimal.valueOf(jsonObject.getDouble(Keys.LONGITUDE)).floatValue();

        boolean willPartecipate = false;

        if(jsonObject.has(Keys.PARTICIPATE)) {
            willPartecipate = jsonObject.getBoolean(Keys.PARTICIPATE);
        }

        Builder builder = Builder.create(title, description, author, startDate).withTag(tag).withAddress(address)
                .withLocation(latitude,longitude).withPrice(price).withImage(image)
                .withWillPartecipate(willPartecipate);

        if(jsonObject.has(Keys.END_DATE)) {
            builder.withEndDate(DateConverter.fromMillis(jsonObject.getLong(Keys.END_DATE)));
        }

        if(jsonObject.has(Keys.ID)) {
            builder.withId(jsonObject.getString(Keys.ID));
        }


        if(jsonObject.has(Keys.PARTICIPATION)) {
            builder.withParticipation(jsonObject.getJSONArray(Keys.PARTICIPATION).length());
        }

        if(jsonObject.has(Keys.MAX_TICKETS)) {
            builder.withMaxTic(jsonObject.getInt(Keys.MAX_TICKETS));
        }

        return builder.build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        if(mId != null) {

            jsonObject.put(Keys.ID,mId);
        }

        jsonObject.put(Keys.TITLE, mTitle);
        jsonObject.put(Keys.DESCRIPTION, mDescription);
        jsonObject.put(Keys.AUTHOR, mAuthor);

        jsonObject.put(Keys.TAG, mTag);
        jsonObject.put(Keys.ADDRESS, mAddress);
        jsonObject.put(Keys.LATITUDE, mLatitude);
        jsonObject.put(Keys.LONGITUDE, mLongitude);
        jsonObject.put(Keys.PRICE, mPrice);
        jsonObject.put(Keys.IMAGE, mImage);

        try {

            jsonObject.put(Keys.START_DATE, DateConverter.toMillis(mStartDate));

            if(mEndDate != null) {
                jsonObject.put(Keys.END_DATE, DateConverter.toMillis(mEndDate));
            }

        } catch (ParseException e) {

            e.printStackTrace();
        }

        if(mMaxTicket != 0) {
            jsonObject.put(Keys.MAX_TICKETS, mMaxTicket);
        }

        return jsonObject;
    }

    //endregion

    //region parcelable methods

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if(mId != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(mId);

        } else {
            dest.writeByte(Keys.NOT_PRESENT);
        }
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mAuthor);
        dest.writeString(mStartDate);
        dest.writeString(mTag);
        dest.writeString(mAddress);
        dest.writeFloat(mLatitude);
        dest.writeFloat(mLongitude);
        dest.writeString(mPrice);
        dest.writeString(mImage);
        dest.writeByte((byte) (mWillPartecipate ? 1 : 0));

        if(mEndDate != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(mEndDate);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if(mParticipation != 0) {
            dest.writeByte(Keys.PRESENT);
            dest.writeInt(mParticipation);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if(mMaxTicket != 0) {
            dest.writeByte(Keys.PRESENT);
            dest.writeInt(mMaxTicket);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);
    }

    protected Event(Parcel in) {

        boolean present = in.readByte() == Keys.PRESENT;
        if(present) {

            mId = in.readString();
        }else{
            mId = null;
        }

        mTitle = in.readString();
        mDescription = in.readString();
        mAuthor = in.readString();
        mStartDate = in.readString();
        mTag = in.readString();
        mAddress = in.readString();
        mLatitude = in.readFloat();
        mLongitude = in.readFloat();
        mPrice = in.readString();
        mImage = in.readString();
        mWillPartecipate = in.readByte() != 0;

        present = in.readByte() == Keys.PRESENT;
        if(present) {

            mEndDate = in.readString();
        }else{
            mEndDate = null;
        }

        present = in.readByte() == Keys.PRESENT;
        if(present) {

            mParticipation = in.readInt();
        }else{
            mParticipation = 0;
        }

        present = in.readByte() == Keys.PRESENT;
        if(present) {

            mMaxTicket = in.readInt();
        }else{
            mMaxTicket = 0;
        }
    }

    //endregion

    //region Keys

    public static class Keys{

        static final String ID = "_id";
        static final String TITLE = "title";
        static final String DESCRIPTION = "description";
        static final String START_DATE = "start_date";
        static final String END_DATE = "end_date";
        static final String TAG = "tag";
        static final String ADDRESS = "address";
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String AUTHOR = "author";
        static final String PRICE = "price";
        static final String PARTICIPATION = "user_participations";
        static final String MAX_TICKETS = "tickets";
        static final String IMAGE = "image";
        static final String PARTICIPATE = "participate";

        static final String FREE = "FREE";

        static final Byte PRESENT = 1;
        static final Byte NOT_PRESENT = 0;

        public static final String EVENT = "event";

    }

    //endregion

    //region Builder

    public static class Builder{

        private String mId;
        private String mTitle;
        private String mDescription;
        private String mStartDate;
        private String mEndDate;
        private String mTag;
        private String mAddress;
        private float mLatitude;
        private float mLongitude;
        private String mAuthor;
        private String mPrice;
        private int mParticipation;
        private String mImage;
        private int mMaxTicket;
        private boolean mWillPartecipate;


        private Builder(final String title, final String description, final String author, final String startDate){

            this.mTitle = title;
            this.mDescription = description;
            this.mStartDate = startDate;
            this.mAuthor = author;
        }

        public static Builder create(final String title, final String description, final String author,
                                     final String startDate){

            return new Builder(title, description, author, startDate);
        }

        Builder withTag(final String tag){

            this.mTag = tag;
            return this;
        }

        Builder withAddress(final String address){

            this.mAddress = address;
            return this;
        }

        Builder withLocation(final float latitude, final float longitude){

            this.mLatitude = latitude;
            this.mLongitude = longitude;
            return this;
        }

        Builder withPrice(final String price) {
            this.mPrice = price;
            return this;
        }

        Builder withEndDate(final String endDate) {
            this.mEndDate = endDate;
            return this;
        }

        Builder withParticipation(final int participation) {
            this.mParticipation = participation;
            return this;
        }

        Builder withImage(final String image) {
            this.mImage = image;
            return this;
        }

        Builder withMaxTic(final int maxTicket) {
            this.mMaxTicket = maxTicket;
            return this;
        }

        Builder withId(final String id) {
            this.mId = id;
            return this;
        }

        Builder withWillPartecipate(final boolean willPartecipate) {
            this.mWillPartecipate = willPartecipate;
            return this;
        }

        public Event build(){

            return new Event(mId,mTitle, mDescription, mAuthor, mStartDate, mEndDate, mTag, mAddress, mLatitude,
                            mLongitude, mPrice, mParticipation, mImage, mMaxTicket, mWillPartecipate);
        }
    }

    //endregion
}
