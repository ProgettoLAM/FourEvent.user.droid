package lam.project.foureventuserdroid.complete_profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.CategoryManager;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

/**
 * Created by Vale on 11/08/2016.
 */

public class Step3Credits extends AbstractStep {

    User mCreatedUserWithCategories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.step3_credits, container, false);
    }


    @Override
    public String name() {
        return null;
    }

    @Override
    public void onNext() {

        final ProgressDialog progressDialog = ProgressDialog.show(getContext(),"Creazione dell'utente in corso",
                "Attendere prego...",true,false);

        progressDialog.show();

        mCreatedUserWithCategories = getStepDataFor(2).getParcelable(User.Keys.USER);

        String uri = FourEventUri.Builder.create(FourEventUri.Keys.USER)
                .appendPath(mCreatedUserWithCategories.email)
                .getUri();

        if(mCreatedUserWithCategories != null) {

            try {

                CustomRequest request = new CustomRequest(Request.Method.POST,
                        uri,
                    mCreatedUserWithCategories.toJson(),

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            progressDialog.dismiss();

                            StepManager.get(getContext()).setStep(StepManager.COMPLETE);
                            UserManager.get().save(mCreatedUserWithCategories);
                            CategoryManager.get().save();

                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressDialog.dismiss();
                            
                            System.out.println(error.toString());
                        }
                    }
                );

                VolleyRequest.get(getContext()).add(request);

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
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

       return true;
    }

    @Override
    public String error() {

        return "Completa tutti i campi obbligatori";
    }
}
