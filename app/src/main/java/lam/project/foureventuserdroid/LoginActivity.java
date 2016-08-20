package lam.project.foureventuserdroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.complete_profile.StepManager;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

/**
 * Created by Vale on 31/07/2016.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;

    private String email;
    private String password;

    private ImageView ic_warning_email;
    private ImageView ic_warning_password;

    private final static String TAG = ".LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        emailField = (EditText) findViewById(R.id.email_log);
        passwordField = (EditText) findViewById(R.id.pass_log);

        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);

        ic_warning_email = (ImageView) findViewById(R.id.ic_alert_email);
        ic_warning_password = (ImageView) findViewById(R.id.ic_alert_pass);

    }

    public void goToRegistration(final View view) {

        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);

    }

    public boolean controlUser() {
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if(email.equals("")) {
            ic_warning_email.setVisibility(View.VISIBLE);
            return false;
        }
        if(password.equals("")) {
            ic_warning_password.setVisibility(View.VISIBLE);
            return false;
        }

        else
            return true;
    }

    public void login(final View view) {
        if(controlUser()) {

            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Login in corso...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

                email = emailField.getText().toString();
                password = passwordField.getText().toString();

            try {
                String url = getResources().getString(R.string.backend_uri_put_user);

                final JSONObject user = new JSONObject("{\"email\": \""+email+"\", \"password\" : \""+password+"\"}");

                CustomRequest request = new CustomRequest(Request.Method.POST, url, user,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Snackbar snackbar = Snackbar
                                        .make(view, response.toString(), Snackbar.LENGTH_LONG);

                                snackbar.show();

                                try {

                                    String email = response.getString("_id");

                                    response.remove("_id");
                                    response.put(User.Keys.EMAIL,email);

                                    next(User.fromJson(response));
                                }
                                catch (JSONException ex ) {}

                                progressDialog.dismiss();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Snackbar.make(view, "Email / password sbagliata",
                                Snackbar.LENGTH_LONG).show();

                        progressDialog.dismiss();
                    }
                });

                VolleyRequest.get(this).add(request);
            }
            catch (JSONException ex) {

            }
        }
    }

    private void next(User user){

        UserManager.get(this).save(user);

        //non visualizzo lo stepper
        StepManager.get(this).setStep(StepManager.COMPLETE);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private final TextWatcher watcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (emailField.getText().toString().length() != 0) {
                ic_warning_email.setVisibility(View.INVISIBLE);
            }
            if(passwordField.getText().toString().length() != 0){
                ic_warning_password.setVisibility(View.INVISIBLE);
            }
        }
    };

}
