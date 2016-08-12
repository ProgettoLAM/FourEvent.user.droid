package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by spino on 29/07/16.
 */
public class User{

    public final String email;

    public final String password;

    public final String name;

    public final String birthDate;

    public final String location;

    public final String gender;

    private User(final String email, final String password, final String name,
                 final String birthDate, final String location, final String gender){

        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.location = location;
        this.gender = gender;
    }

    public static User fromJson(final JSONObject jsonObject) throws JSONException{

        final String email = jsonObject.getString(Keys.EMAIL);
        final String password = jsonObject.getString(Keys.PASSWORD);

        Builder builder = Builder.create(email,password);

        String name = jsonObject.getString(Keys.NAME);
        String birthDate = jsonObject.getString(Keys.BIRTH_DATE);
        String location = jsonObject.getString(Keys.LOCATION);
        String gender = jsonObject.getString(Keys.GENDER);

        if(name != null){

            builder.withName(name);
        }

        if(birthDate != null){

            builder.withBirthDate(birthDate);
        }

        if(location != null){

            builder.withLocation(location);
        }

        if(gender != null){

            builder.withGender(gender);
        }

        return builder.build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.EMAIL, email);
        jsonObject.put(Keys.PASSWORD, password);

        if(name != null){

            jsonObject.put(Keys.NAME, name);
        }

        if(birthDate != null){

            jsonObject.put(Keys.BIRTH_DATE, birthDate);
        }

        if(location != null){

            jsonObject.put(Keys.LOCATION, location);
        }

        if(gender != null){

            jsonObject.put(Keys.GENDER, gender);
        }

        return jsonObject;
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
    }

    public static class Builder{

        private String mEmail;

        private String mPassword;

        private String mName;

        private String mBirthDate;

        private String mLocation;

        private String mGender;

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

        public User build(){

            return new User(mEmail,mPassword,mName,mBirthDate,mLocation,mGender);
        }
    }
}
