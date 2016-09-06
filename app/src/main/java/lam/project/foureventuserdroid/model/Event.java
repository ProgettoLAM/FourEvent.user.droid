package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;

import lam.project.foureventuserdroid.utils.DateConverter;

/**
 * Classe che rappresenta il modello di un evento, con i relativi campi
 */
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

    public Double mDistance;

    private final String mImage;

    private boolean mWillPartecipate;

    public String mStreetAddress;


    private Event(final String id, final String title, final String description, final String author,
                  final String startDate, final String endDate, final String tag, final String address,
                  final float latitude, final float longitude, final String price,
                  final int participation, final String image, final int maxTic,
                  final boolean willPartecipate, final Double distance, final String streetAddress){

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
        this.mDistance = distance;
        this.mStreetAddress = streetAddress;
    }

    public boolean isFree() {

        return mPrice.equals(Keys.FREE);
    }

    public boolean hasDistance() {

        return mDistance != null;
    }

    public void updateWillParticipate() {
        this.mWillPartecipate = !mWillPartecipate;
    }

    public boolean willPartecipate() {
        return this.mWillPartecipate;
    }

    //region lettura/scrittura Json

    public static Event fromJson(final JSONObject jsonObject) throws JSONException{

        final String title = jsonObject.getString(Keys.TITLE);
        final String description = jsonObject.getString(Keys.DESCRIPTION);
        final String author = jsonObject.getString(Keys.AUTHOR);
        final String startDate = DateConverter.dateFromMillis(jsonObject.getLong(Keys.START_DATE));

        final String tag = jsonObject.getString(Keys.TAG);
        final String address = jsonObject.getString(Keys.ADDRESS);

        final String price = jsonObject.getString(Keys.PRICE);
        final String image = jsonObject.getString(Keys.IMAGE);

        //Si prendono le coordinate
        JSONArray coordinates = jsonObject.getJSONObject(Keys.LOC).getJSONArray(Keys.COORDINATES);

        final float latitude = BigDecimal.valueOf(coordinates.getDouble(1)).floatValue();
        final float longitude = BigDecimal.valueOf(coordinates.getDouble(0)).floatValue();

        boolean willPartecipate = false;

        if(jsonObject.has(Keys.PARTICIPATE)) {
            willPartecipate = jsonObject.getBoolean(Keys.PARTICIPATE);
        }

        Builder builder = Builder.create(title, description, author, startDate).withTag(tag).withAddress(address)
                .withLocation(latitude,longitude).withPrice(price).withImage(image)
                .withWillPartecipate(willPartecipate);

        if(jsonObject.has(Keys.END_DATE)) {
            builder.withEndDate(DateConverter.dateFromMillis(jsonObject.getLong(Keys.END_DATE)));
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

        if(jsonObject.has(Keys.DISTANCE)) {

            builder.withDistance(jsonObject.getDouble(Keys.DISTANCE) / 1000);
        }

        if(jsonObject.has(Keys.STREET_ADDRESS)) {

            builder.withStreetAddress(jsonObject.getString(Keys.STREET_ADDRESS));
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

        JSONArray coordinates = new JSONArray();
        coordinates.put(mLongitude);
        coordinates.put(mLatitude);

        JSONObject loc = new JSONObject(Keys.TYPE);
        loc.put(Keys.COORDINATES,coordinates);

        jsonObject.put(Keys.LOC,loc);

        jsonObject.put(Keys.PRICE, mPrice);
        jsonObject.put(Keys.IMAGE, mImage);
        jsonObject.put(Keys.STREET_ADDRESS,mStreetAddress);

        try {

            jsonObject.put(Keys.START_DATE, DateConverter.dateTimeToMillis(mStartDate));

            if(mEndDate != null) {
                jsonObject.put(Keys.END_DATE, DateConverter.dateTimeToMillis(mEndDate));
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

    //region metodi parcelable

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

        dest.writeString(mStreetAddress);
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

        mStreetAddress = in.readString();
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
        static final String AUTHOR = "author";
        static final String PRICE = "price";
        static final String PARTICIPATION = "user_participations";
        static final String MAX_TICKETS = "tickets";
        static final String IMAGE = "image";
        static final String PARTICIPATE = "participate";
        static final String DISTANCE = "distance";
        static final String STREET_ADDRESS = "street_address";

        static final String TYPE = "{'type':'point'}";
        static final String COORDINATES = "coordinates";
        static final String LOC = "loc";

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
        private Double mDistance;
        private String mStreetAddress;

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

        Builder withDistance(final Double distance) {
            this.mDistance = distance;
            return this;
        }

        Builder withStreetAddress(final String streetAddress) {
            this.mStreetAddress = streetAddress;
            return this;
        }

        public Event build(){

            return new Event(mId,mTitle, mDescription, mAuthor, mStartDate, mEndDate, mTag, mAddress, mLatitude,
                            mLongitude, mPrice, mParticipation, mImage, mMaxTicket, mWillPartecipate,mDistance,mStreetAddress);
        }
    }

    //endregion
}
