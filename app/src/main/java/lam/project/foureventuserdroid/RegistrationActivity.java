package lam.project.foureventuserdroid;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.CustomJsonRequest;
import lam.project.foureventuserdroid.utils.VolleyRequest;

/**
 * Created by Vale on 30/07/2016.
 */

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private EditText passwordField2;

    private ImageView ic_close;
    private ImageView ic_check;
    private ImageView ic_warning_email;
    private ImageView ic_warning_password;

    private TextView min_char_pass;

    private String email;
    private String password;
    private String password2;

    private final static String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);

        emailField = (EditText) findViewById(R.id.email_reg);

        passwordField = (EditText) findViewById(R.id.pass_reg);

        passwordField2 = (EditText) findViewById(R.id.pass2_reg);

        min_char_pass = (TextView) findViewById(R.id.min_char_pass);

        ic_close = (ImageView) findViewById(R.id.ic_close_reg);
        ic_check = (ImageView) findViewById(R.id.ic_check_reg);
        ic_warning_email = (ImageView) findViewById(R.id.ic_alert_email);
        ic_warning_password = (ImageView) findViewById(R.id.ic_alert_pass);

        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);
        passwordField2.addTextChangedListener(watcher);
    }

    public boolean controlUser() {
        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        password2 = passwordField2.getText().toString();

        if(email.equals("")) {
            ic_warning_email.setVisibility(View.VISIBLE);
            return false;
        }
        if(password.equals("")) {
            ic_warning_password.setVisibility(View.VISIBLE);
            return false;
        }
        if(password2.equals("") || !password.equals(password2)) {
            return false;
        }
        else
            return true;
    }



    public  void register(final View view){

        if(controlUser()) {

            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Registrazione in corso...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            try {

                final User user = User.Builder.create(email, password).build();
                String url = getResources().getString(R.string.backend_uri_put_user);


                CustomJsonRequest request = new CustomJsonRequest(Request.Method.PUT, url, user.toJson(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Snackbar snackbar = Snackbar
                                    .make(view, response.toString(), Snackbar.LENGTH_LONG);

                            snackbar.show();

                            next(user);

                            progressDialog.hide();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressDialog.hide();

                        }
                    });

                VolleyRequest.get(this).add(request);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void goToLogin(final View view) {

        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void next(User user){

        UserManager.get(this).save(user);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private final TextWatcher watcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(passwordField.getText().toString().equals(passwordField2.getText().toString()) && !passwordField.getText().toString().equals("")){
                ic_check.setVisibility(View.VISIBLE);
                ic_close.setVisibility(View.INVISIBLE);

            }
            else if(!passwordField.getText().toString().equals(passwordField2.getText().toString())){
                ic_close.setVisibility(View.VISIBLE);
                ic_check.setVisibility(View.INVISIBLE);

            }
        }

        public void afterTextChanged(Editable s) {
            if (emailField.getText().toString().length() != 0) {
                ic_warning_email.setVisibility(View.INVISIBLE);
            }
            if(passwordField.getText().toString().length() != 0){
                ic_warning_password.setVisibility(View.INVISIBLE);
            }
            if(passwordField.getText().toString().length() != 0 && passwordField.getText().toString().length() < 8){
                min_char_pass.setVisibility(View.VISIBLE);
            }
            if(passwordField.getText().toString().length() >= 8){
                min_char_pass.setVisibility(View.INVISIBLE);
            }
        }
    };

}
