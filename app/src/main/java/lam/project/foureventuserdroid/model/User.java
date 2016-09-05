package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import lam.project.foureventuserdroid.utils.DateConverter;

/**
 * Classe che rappresenta il modello dell'utente, con i relativi campi
 */
public class User implements Parcelable{

    public final String email;

    public String name;

    public String birthDate;

    public String location;

    public String gender;

    public float balance;

    public String image;

    public List<Category> categories;

    private User(final String email, final String name,
                 final String birthDate, final String location, final String gender,
                 final float balance, final String image){

        this.email = email;
        this.name = name;
        this.birthDate = birthDate;
        this.location = location;
        this.gender = gender;
        this.balance = balance;
        this.image = image;
    }

    public void updateBalance(float amount) {

        this.balance += amount;
    }

    public User addName(String name) {
        this.name = name;
        return this;
    }

    public User addLocation(String location) {
        this.location = location;
        return this;
    }

    public User addGender(String gender) {
        this.gender = gender;
        return this;
    }

    public User addBirthDate(String birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public User addCategories(List<Category> categories) {
        this.categories = categories;
        return this;
    }

    public User updateImage(String image) {
        this.image = image;
        return this;
    }

    //Region metodi parcelable

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    protected User(Parcel in) {

        email = in.readString();
        balance = in.readFloat();

        boolean present = in.readByte() == Keys.PRESENT;
        if(present) {
            name = in.readString();
        }
        else
            name = null;

        present = in.readByte() == Keys.PRESENT;
        if(present) {
            birthDate = in.readString();
        }
        else
            birthDate = null;

        present = in.readByte() == Keys.PRESENT;
        if(present) {
           location = in.readString();
        }
        else
            location = null;

        present = in.readByte() == Keys.PRESENT;
        if(present) {
            gender = in.readString();
        }
        else
            gender = null;

        present = in.readByte() == Keys.PRESENT;
        if(present) {
            in.readTypedList(categories,Category.CREATOR);
        }
        else
            categories = null;

        present = in.readByte() == Keys.PRESENT;
        if(present) {
            image = in.readString();
        }
        else
            image = null;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(email);
        dest.writeFloat(balance);

        if (name != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(name);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if (birthDate != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(birthDate);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if (location != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(location);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if (gender != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(gender);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if (categories != null) {

            dest.writeByte(Keys.PRESENT);
            dest.writeTypedList(categories);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);

        if (image != null) {
            dest.writeByte(Keys.PRESENT);
            dest.writeString(image);
        }
        else
            dest.writeByte(Keys.NOT_PRESENT);
    }

    //Endregion

    //Region lettura/scrittura Json

    public static User fromJson(final JSONObject jsonObject) throws JSONException{

        final String email = jsonObject.getString(Keys.EMAIL);

        Builder builder = Builder.create(email);

        if (jsonObject.has(Keys.NAME)) {
            builder.withName(jsonObject.getString(Keys.NAME));
        }

        if(jsonObject.has(Keys.BIRTH_DATE)){

            builder.withBirthDate(DateConverter.dateFromMillis(jsonObject.getLong(Keys.BIRTH_DATE)));
        }

        if(jsonObject.has(Keys.LOCATION)){

            builder.withLocation(jsonObject.getString(Keys.LOCATION));
        }

        if(jsonObject.has(Keys.GENDER)){

            builder.withGender(jsonObject.getString(Keys.GENDER));
        }

        final float balance = BigDecimal.valueOf(jsonObject.getDouble(Keys.BALANCE)).floatValue();

        if(balance > 0) {

            builder.withBalance(balance);
        }

        User user = builder.build();

        if(jsonObject.has(Keys.CATEGORIES)){

            List<Category> categories = new LinkedList<>();
            JSONArray jsonArray = new JSONArray(jsonObject.getString(Keys.CATEGORIES));

            //Per ogni categoria si aggiunge il suo modello nell'array dell'utente
            for(int i=0; i<jsonArray.length(); i++) {

                Category category = Category.fromJson(jsonArray.getJSONObject(i));

                categories.add(category);
            }

            user.addCategories(categories);
        }

        if(jsonObject.has(Keys.IMAGE)){

            builder.withImage(jsonObject.getString(Keys.IMAGE));
        }

        return user;
    }

    public JSONObject toJson() throws JSONException {

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.EMAIL, email);
        jsonObject.put(Keys.BALANCE,balance);

        if (name != null) {

            jsonObject.put(Keys.NAME, name);
        }

        if (birthDate != null) {

            try{

                jsonObject.put(Keys.BIRTH_DATE, DateConverter.dateToMillis(birthDate));

            } catch (ParseException e) {

                e.printStackTrace();
            }
        }

        if (location != null) {

            jsonObject.put(Keys.LOCATION, location);
        }

        if (gender != null) {

            jsonObject.put(Keys.GENDER, gender);
        }


        if(categories != null) {

            JSONArray array = new JSONArray();

            for(Category category : categories){

                array.put(category.toJson());
            }

            jsonObject.put(Keys.CATEGORIES,array);
        }

        if (image != null) {

            jsonObject.put(Keys.IMAGE, image);
        }

        return jsonObject;
    }

    //Endregion

    //Region Keys

    public static class Keys{

        public static final String EMAIL = "email";

        public static final String NAME = "name";

        public static final String TOKEN = "gcm_token";

        public static final String BIRTH_DATE = "birth_date";

        public static final String LOCATION = "location";

        public static final String GENDER = "gender";

        public static final String USER = "user";

        public static final String CATEGORIES = "categories";

        public static final String BALANCE = "balance";

        public static final String IMAGE = "image";

        public static final Byte PRESENT = 1;

        public static final Byte NOT_PRESENT = 0;
    }

    //Endregion

    //Region Builder

    public static class Builder{

        private String mEmail;

        private String mName;

        private String mBirthDate;

        private String mLocation;

        private String mGender;

        private float mBalance;

        private String mImage;

        private Builder(final String email){

            this.mEmail = email;
        }

        public static Builder create(final String email){

            return new Builder(email);
        }

        public Builder withName(final String name){

            this.mName = name;
            return this;
        }

        public Builder withBirthDate(final String birthDate){

            this.mBirthDate = birthDate;
            return this;
        }

        public Builder withLocation(final String location){

            this.mLocation = location;
            return this;
        }

        public Builder withGender(final String gender){

            this.mGender = gender;
            return this;
        }

        public Builder withBalance(final float balance) {

            this.mBalance = balance;
            return this;
        }

        public Builder withImage(final String image) {

            this.mImage = image;
            return this;
        }

        public User build(){
            return new User(mEmail,mName,mBirthDate,mLocation,mGender,mBalance,mImage);
        }
    }

    //Endregion
}
