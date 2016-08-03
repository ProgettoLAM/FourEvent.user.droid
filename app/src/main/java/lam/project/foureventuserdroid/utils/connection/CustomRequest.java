package lam.project.foureventuserdroid.utils.connection;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import lam.project.foureventuserdroid.model.User;

/**
 * Created by Vale on 01/08/2016.
 */

public class CustomRequest extends JsonObjectRequest{


    public CustomRequest(int method, String url, JSONObject jsonRequest,
                         Response.Listener<JSONObject> listener,
                         Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        try {
            String jsonString = new String(response.data,HttpHeaderParser.parseCharset(response.headers));

            return Response.success(new JSONObject(jsonString),HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {

            return Response.error(new ParseError(e));

        } catch (JSONException ex) {

            return Response.error(new ParseError(ex));
        }
    }
}
