package lam.project.foureventuserdroid.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spino on 29/07/16.
 */
public class User {

    public final String id;

    public final String email;

    public final String name;

    public final String password;

    private User(final String id, final String email, final String name, final String password){

        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public static User fromJson(final JSONObject jsonObject) throws JSONException{

        final String id = jsonObject.getString(Keys.ID);
        final String email = jsonObject.getString(Keys.EMAIL);
        final String name = jsonObject.getString(Keys.NAME);
        final String password = jsonObject.getString(Keys.PASSWORD);

        return Builder.create(id,email,name,password).build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.ID, id);
        jsonObject.put(Keys.EMAIL, email);
        jsonObject.put(Keys.NAME, name);
        jsonObject.put(Keys.PASSWORD, password);

        return jsonObject;
    }

    public static class Keys{

        public static final String ID = "id";

        public static final String EMAIL = "email";

        public static final String NAME = "name";

        public static final String PASSWORD = "password";
    }

    public static class Builder{

        private String mId;

        private String mEmail;

        private String mName;

        private String mPassword;

        private Builder(final String id, final String email, final String name,
                        final String password){

            this.mId = id;
            this.mEmail = email;
            this.mName = name;
            this.mPassword = password;
        }

        public static Builder create(final String id, final String email, final String name,
                                     final String password){

            return new Builder(id,email,name,password);
        }

        public User build(){

            return new User(mId,mEmail,mName,mPassword);
        }
    }
}
