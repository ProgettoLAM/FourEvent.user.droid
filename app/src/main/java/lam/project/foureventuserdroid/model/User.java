package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by spino on 29/07/16.
 */
public class User implements Parcelable{

    public final String email;

    public final String password;

    public String name;

    public String birthDate;

    public String location;

    public String gender;

    public Category category;

    private User(final String email, final String password, final String name,
                 final String birthDate, final String location, final String gender, final Category category){

        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.location = location;
        this.gender = gender;
        this.category = category;
    }

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
    public User addCategory(Category category) {
        this.category = category;
        return this;
    }

   protected User(Parcel in) {
        email = in.readString();
        password = in.readString();
        boolean present = in.readByte() == Keys.PRESENT;
        if(present) {
            name = in.readString();
        }
        else
            name = null;

        if(present) {
            birthDate = in.readString();
        }
        else
            birthDate = null;

        if(present) {
           location = in.readString();
        }
        else
            location = null;

        if(present) {
            gender = in.readString();
        }
        else
            gender = null;


    }


    public static User fromJson(final JSONObject jsonObject) throws JSONException{

        final String email = jsonObject.getString(Keys.EMAIL);
        final String password = jsonObject.getString(Keys.PASSWORD);

        Builder builder = Builder.create(email,password);

        if (jsonObject.has(Keys.NAME)) {
            builder.withName(jsonObject.getString(Keys.NAME));
        }

        if(jsonObject.has(Keys.BIRTH_DATE)){

            builder.withBirthDate(jsonObject.getString(Keys.BIRTH_DATE));
        }

        if(jsonObject.has(Keys.LOCATION)){

            builder.withLocation(jsonObject.getString(Keys.LOCATION));
        }

        if(jsonObject.has(Keys.GENDER)){

            builder.withGender(jsonObject.getString(Keys.GENDER));
        }

        return builder.build();
    }

    public JSONObject toJson() throws JSONException {

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.EMAIL, email);
        jsonObject.put(Keys.PASSWORD, password);

        if (name != null) {

            jsonObject.put(Keys.NAME, name);
        }

        if (birthDate != null) {

            jsonObject.put(Keys.BIRTH_DATE, birthDate);
        }

        if (location != null) {

            jsonObject.put(Keys.LOCATION, location);
        }

        if (gender != null) {

            jsonObject.put(Keys.GENDER, gender);
        }

        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(password);

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

    }

    public static class Keys{

        public static final String EMAIL = "email";

        public static final String PASSWORD = "password";

        public static final String NAME = "name";

        public static final String BIRTH_DATE = "birth_date";

        public static final String LOCATION = "location";

        public static final String GENDER = "gender";

        public static final String MALE = "M";

        public static final String FEMALE = "F";

        public static final String USER = "user";

        public static final Category CATEGORY = new Category(0,"category");

        public static final Byte PRESENT = 1;

        public static final Byte NOT_PRESENT = 0;
    }

    public static class Builder{

        private String mEmail;

        private String mPassword;

        private String mName;

        private String mBirthDate;

        private String mLocation;

        private String mGender;

        private Category mCategory;

        //TODO completare la classe, aggiungendo i parametri, completare i metodi e usare la classe
        //TODO parcelable, utilizzare il metodo opzionale anche per trasformazione JSON

        private Builder(final String email,final String password){

            this.mEmail = email;
            this.mPassword = password;
        }

        public static Builder create(final String email, final String password){

            return new Builder(email,password);
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

        public Builder withCategory(final Category category) {
            this.mCategory = category;
            return this;
        }

        public User build(){
            return new User(mEmail,mPassword,mName,mBirthDate,mLocation,mGender, mCategory);
        }
    }
}
