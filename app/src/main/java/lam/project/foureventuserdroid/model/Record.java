package lam.project.foureventuserdroid.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import lam.project.foureventuserdroid.utils.DateConverter;

/**
 * Created by spino on 22/08/16.
 */

public class Record {


    public final String mDate;

    public final float mAmount;

    public final String mType;

    public final String mUser;

    public final String mEvent;

    public Record(final String date, final float amount, final String type, final String user,
        final String event) {

        this.mDate = date;
        this.mAmount = amount;
        this.mType = type;
        this.mUser = user;
        this.mEvent = event;
    }

    public static Record fromJson(JSONObject jsonObject) throws JSONException {

        String date = DateConverter.fromMillis(jsonObject.getLong(Keys.DATE));
        float amount = BigDecimal.valueOf(jsonObject.getDouble(Keys.AMOUNT)).floatValue();
        String type = jsonObject.getString(Keys.TYPE);
        String user = jsonObject.getString(Keys.USER);

        Builder builder = Builder.create(date,amount,type,user);

        if(jsonObject.has(Keys.EVENT)) {

            builder.withEvent(jsonObject.getString(Keys.EVENT));
        }

        return builder.build();
    }

    public JSONObject toJson () throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(Keys.DATE,mDate);
        jsonObject.put(Keys.AMOUNT,mAmount);
        jsonObject.put(Keys.TYPE,mType);
        jsonObject.put(Keys.USER,mUser);

        if(mEvent != null) {

            jsonObject.put(Keys.EVENT,mEvent);
        }

        return jsonObject;
    }
    public static class Builder {

        private String date;
        private float amount;
        private String type;
        private String user;
        private String event;

        private Builder(String date, float amount, String type, String user) {

            this.date = date;
            this.amount = amount;
            this.type = type;
            this.user = user;
        }

        public static Builder create(String date, float amount, String type, String user) {

            return new Builder(date,amount,type,user);
        }

        Builder withEvent(String event) {

            this.event = event;
            return this;
        }

        public Record build() {

            return new Record(date,amount,type,user,event);
        }
    }

    public static class Keys {

        static String DATE = "date";
        static String AMOUNT = "amount";
        static String TYPE = "type";
        static String USER = "user";
        static String EVENT = "event";
    }
}
