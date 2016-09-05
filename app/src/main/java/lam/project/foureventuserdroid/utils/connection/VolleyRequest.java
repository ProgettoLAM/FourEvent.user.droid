package lam.project.foureventuserdroid.utils.connection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleyRequest {

    private static RequestQueue mRequestQueue;
    private VolleyRequest instance;
    private Context context;

    private VolleyRequest(Context context) {

        this.context = context;

        if(mRequestQueue == null) {

            mRequestQueue = initRequestQueue();
            mRequestQueue.start();
        }
    }

    private RequestQueue initRequestQueue() {
        return Volley.newRequestQueue(context);
    }

    //Singleton, si crea una sola istanza della classe
    public VolleyRequest get(Context context) {

        if(instance == null) {

            instance = new VolleyRequest(context);
        }

        return instance;
    }

    public VolleyRequest get(){

        if(instance == null){

            throw new IllegalStateException("Context not initialized");
        }

        return instance;
    }

    public void add(Request request) {
        mRequestQueue.add(request);
    }

}
