package lam.project.foureventuserdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe del modello delle categorie, con i relativi campi tag e nome
 */
public class Category implements Parcelable{

    public final int id;
    public final String name;

    public Category(final int id, final String name){

        this.id = id;
        this.name = name;
    }

    protected Category(Parcel in) {

        id = in.readInt();
        name = in.readString();
    }

    //Region metodi parcelable

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    //Endregion

    //Region lettura/scrittua Json

    public static Category fromJson(final JSONObject jsonObject) throws JSONException {

        final int id = jsonObject.getInt(Keys.ID);
        final String name = jsonObject.getString(Keys.NAME);

        return Builder.create(id,name).build();
    }

    public JSONObject toJson() throws JSONException{

        final JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.ID,id);
        jsonObject.put(Keys.NAME, name);

        return jsonObject;
    }

    //Endregion

    //Region Keys

    public static class Keys{

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CATEGORY = "categories";
    }

    //Endregion

    //Region Builder

    public static class Builder{

        public final int id;
        public final String name;

        private Builder(final int id,final String name){

            this.id = id;
            this.name = name;
        }

        public static Builder create(final int id, final String name){

            return new Builder(id,name);
        }

        public Category build(){

            return new Category(this.id,this.name);
        }
    }

    //Endregion
}
