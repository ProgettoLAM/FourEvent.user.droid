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
    }
}
