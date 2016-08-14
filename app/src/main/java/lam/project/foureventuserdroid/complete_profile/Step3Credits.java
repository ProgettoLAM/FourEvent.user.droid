package lam.project.foureventuserdroid.complete_profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

/**
 * Created by Vale on 11/08/2016.
 */

public class Step3Credits extends AbstractStep {

    private int i = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step3_credits, container, false);

        return v;
    }


    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isOptional() {
        return true;
    }


    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {
        System.out.println("onNext");
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    @Override
    public String optional() {
        return null;
    }

    @Override
    public boolean nextIf() {

        //TODO gestire caso in cui non arriva niente dallo step 2
        User user = getStepDataFor(2).getParcelable(User.Keys.USER);

        try {

            String url = getResources().getString(R.string.backend_uri_put_user) + "/" + user.email;

            JSONObject obj = user.toJson();

            CustomRequest request = new CustomRequest(Request.Method.POST, url, obj,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Snackbar snackbar = Snackbar
                                .make(getView(), response.toString(), Snackbar.LENGTH_LONG);

                        snackbar.show();

                        StepManager.get(getContext()).setStep(StepManager.COMPLETE);

                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Snackbar snackbar = Snackbar
                                .make(getView(), error.toString(), Snackbar.LENGTH_LONG);

                        snackbar.show();

                    }
                });

            VolleyRequest.get(getContext()).add(request);

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return i > 3;
    }

    @Override
    public String error() {

        return null;
    }
}
