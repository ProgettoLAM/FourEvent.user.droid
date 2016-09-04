package lam.project.foureventuserdroid;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.complete_profile.StepManager;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.HandlerManager;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.gcm.GCMRegistrationIntentService;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

/**
 * Created by Vale on 30/07/2016.
 *
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

    private String token;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);

        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //inizializzo le view che mi interessano
        emailField = (EditText) findViewById(R.id.email_reg);
        passwordField = (EditText) findViewById(R.id.pass_reg);
        passwordField2 = (EditText) findViewById(R.id.pass2_reg);
        min_char_pass = (TextView) findViewById(R.id.min_char_pass);

        ic_close = (ImageView) findViewById(R.id.ic_close_reg);
        ic_check = (ImageView) findViewById(R.id.ic_check_reg);
        ic_warning_email = (ImageView) findViewById(R.id.ic_alert_email);
        ic_warning_password = (ImageView) findViewById(R.id.ic_alert_pass);

        //aggiungo i listener all'evento change text
        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);
        passwordField2.addTextChangedListener(watcher);

        //Registrazione token dell'utente per la ricezione delle notifiche
        handleMessaging();
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("EventDetailActivity", "onResume");

        if(mRegistrationBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
        }
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("EventDetailActivity", "onPause");

        if(mRegistrationBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        }
    }

    public void register(final View view){

        if(controlUser()) {

            //creo e mostro il progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);

            progressDialog.setMessage("Registrazione in corso...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();

            //creo l'utente con l'email
            final User user = User.Builder.create(email).build();

            try {

                //inizializzo l'oggetto JSON per completare la richiesta
                JSONObject userJson = new JSONObject("{'email':'"+user.email+"','password':'"+password+"'}");

                //creo l'url del backend
                String url = FourEventUri.Builder.create(FourEventUri.Keys.USER).getUri();

                CustomRequest request = new CustomRequest(Request.Method.PUT, url, userJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                progressDialog.dismiss();

                                next(user);

                        }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                progressDialog.dismiss();

                                String message = HandlerManager.handleError(error);

                                Snackbar snackbarError = Snackbar
                                        .make(view, message, Snackbar.LENGTH_LONG);

                                snackbarError.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightRed));
                                snackbarError.show();
                            }
                        });

                VolleyRequest.get(this).add(request);

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    //region nuova activity

    public void goToLogin(final View view) {

        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void next(User user){

        UserManager.get(this).save(user);
        StepManager.get(this).setStep(StepManager.INCOMPLETE);

        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(User.Keys.USER, user);

        startActivity(intent);
        finish();
    }

    //endregion

    //region controlli edittext

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

    public boolean controlUser() {

        email = emailField.getText().toString();
        password = passwordField.getText().toString();
        String password2 = passwordField2.getText().toString();

        if(email.equals("")) {
            ic_warning_email.setVisibility(View.VISIBLE);
            return false;
        }
        if(password.equals("")) {
            ic_warning_password.setVisibility(View.VISIBLE);
            return false;
        }
        return !(password2.equals("") || !password.equals(password2));
    }

    //Gestione del Google Cloud Messaging, per la ricezione di notifiche
    private void handleMessaging() {

        //Inizializzazione del nostro broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService
            @Override
            public void onReceive(Context context, Intent intent) {

                //Se il broadcast ha ricevuto con successo, il dispositivo è pronto per la registrazione
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Si ricava il token dal dispositivo dell'utente
                    token = intent.getStringExtra("token");

                //Se il broadcast non ha ricevuto con successo, viene mostrato un messaggio di errore
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){

                    Snackbar snackbarError = Snackbar.make(getWindow().getDecorView(), "Errore registrazione GCM!",
                            Snackbar.LENGTH_LONG);

                    snackbarError.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightRed));
                    snackbarError.show();

                } else {
                    Snackbar snackbarError = Snackbar.make(getWindow().getDecorView(), "Si è riscontrato un errore",
                            Snackbar.LENGTH_LONG);

                    snackbarError.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightRed));
                    snackbarError.show();
                }
            }
        };

        //Controlla se i servizi Google sono disponibili o no
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //Se i servizi non sono raggiungibili
        if(ConnectionResult.SUCCESS != resultCode) {

            //Se i servizi Google sono supportati, ma non installati, viene mostrato un messaggio di errore
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                Snackbar snackbarError = Snackbar.make(getWindow().getDecorView(), "Google Play Service non è installato/disponibile sul dispositivo",
                        Snackbar.LENGTH_LONG);

                snackbarError.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightRed));
                snackbarError.show();

                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

            //Se il dispositivo non supporta i servizi Google, viene mostrato un messaggio di errore
            } else {
                Snackbar snackbarError = Snackbar.make(getWindow().getDecorView(), "Questo dispositivo non supporta il Google Play Services!",
                        Snackbar.LENGTH_LONG);

                snackbarError.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.lightRed));
                snackbarError.show();
            }

        //Se i servizi sono disponibili si fa partire l'intent per registrare il dispositivo
        } else {
            //Starting intent to register device
            Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            startService(intent);
        }

    }


    //endregion
}
