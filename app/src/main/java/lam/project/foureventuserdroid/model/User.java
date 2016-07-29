package lam.project.foureventuserdroid.model;

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
