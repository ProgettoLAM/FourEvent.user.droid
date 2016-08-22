package lam.project.foureventuserdroid.utils.connection;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import lam.project.foureventuserdroid.model.Event;
import lam.project.foureventuserdroid.model.Record;

/**
 * Created by spino on 22/08/16.
 */

public class RecordListRequest extends JsonRequest<List<Record>> {


    public RecordListRequest(String url, String requestBody, Response.Listener<List<Record>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, requestBody, listener, errorListener);
    }

    @Override
    protected Response<List<Record>> parseNetworkResponse(NetworkResponse response) {

        List<Record> records = new LinkedList<>();

        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONArray jsonArray = new JSONArray(jsonString);

            for(int i = 0; i < jsonArray.length(); i++) {
                final JSONObject item = jsonArray.getJSONObject(i);
                final Record record = Record.fromJson(item);
                records.add(record);
            }

            return Response.success(records, HttpHeaderParser.parseCacheHeaders(response));
        }
        catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
        catch (JSONException je) {
            return Response.error(new ParseError(je));

        }
    }
}