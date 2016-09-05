package lam.project.foureventuserdroid.utils.connection;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Richiesta Volley dei files (immagini)
 */
public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();

    private static final String FILE_PART_NAME = "file";
    private static final String STRING_PART_NAME = "text";

    private final Response.Listener<String> mListener;
    private final File mFilePart;
    private final String mStringPart;

    public MultipartRequest(String url, Response.ErrorListener errorListener,
                            Response.Listener<String> listener, File file, String stringPart) {

        super(Method.PUT, url, errorListener);

        mListener = listener;
        mFilePart = file;
        mStringPart = stringPart;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {

        entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
        try
        {
            entity.addPart(STRING_PART_NAME, new StringBody(mStringPart));
        }
        catch (UnsupportedEncodingException e)
        {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType()
    {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            entity.writeTo(bos);
        }
        catch (IOException e)
        {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        String jsonString;

        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            JSONObject jsonObject = new JSONObject(jsonString);

            return Response.success(jsonObject.getString("filename"), HttpHeaderParser.parseCacheHeaders(response));

        }
        catch (UnsupportedEncodingException e) { return Response.error(new ParseError(e)); }

        catch (JSONException e) { return Response.error(new ParseError(e));}

    }

    @Override
    protected void deliverResponse(String response)
    {
        mListener.onResponse(response);
    }
}
