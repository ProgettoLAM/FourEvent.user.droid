package lam.project.foureventuserdroid.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spino on 29/07/16.
 */
public class User {

    public final String email;

    public final String name;

    public final String password;

    private User(final String email, final String name, final String password){

        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static User fromJson(final JSONObject jsonObject) throws JSONException{

        final String email = jsonObject.getString(Keys.EMAIL);
        final String password = jsonObject.getString(Keys.PASSWORD);

        return Builder.create(email,password).build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.EMAIL, email);
        jsonObject.put(Keys.PASSWORD, password);

        return jsonObject;
    }

    public static class Keys{

        public static final String EMAIL = "email";

        public static final String NAME = "name";

        public static final String PASSWORD = "password";

        public static final String USER = "user";
    }

    public static class Builder{

        private String mEmail;

        private String mName;

        private String mPassword;

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

        public User build(){

            return new User(mEmail,mName,mPassword);
        }
    }
}
