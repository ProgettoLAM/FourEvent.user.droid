package lam.project.foureventuserdroid.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spino on 10/08/16.
 */
public class Cateogory {

    public final String name;

    private Cateogory(final String name){

        this.name = name;
    }

    public static Cateogory fromJson(final JSONObject jsonObject) throws JSONException {

        final String name = jsonObject.getString(Keys.NAME);

        return Builder.create(name).build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.NAME, name);

        return jsonObject;
    }

    public static class Keys{

        public static final String NAME = "name";
        public static final String CATEGORY = "category";
    }

    public static class Builder{

        public final String name;

        private Builder(final String name){

            this.name = name;
        }

        public static Builder create(final String name){

            return new Builder(name);
        }

        public Cateogory build(){

            return new Cateogory(this.name);
        }
    }
}
