package lam.project.foureventuserdroid.utils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import lam.project.foureventuserdroid.model.User;

/**
 * Created by Vale on 01/08/2016.
 */

public class UserRequest extends JsonRequest{

    public UserRequest(int method, String url, String payload, Response.Listener<User> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, payload, listener, errorListener);
    }

    @Override
    protected Response<User> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            final User user = User.fromJson(new JSONObject(jsonString));
            return Response.success(user, HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (JSONException ex) {
            return Response.error(new ParseError(ex));
        }
        catch (UnsupportedEncodingException enc) {
            return Response.error(new ParseError(enc));
        }
    }
}
