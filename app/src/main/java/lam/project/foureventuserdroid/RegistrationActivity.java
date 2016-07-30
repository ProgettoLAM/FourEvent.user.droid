package lam.project.foureventuserdroid;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Vale on 30/07/2016.
 */

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private EditText passwordField2;
    private Button sendAccount;
    private ImageView ic_close;
    private ImageView ic_check;
    private ImageView ic_warning_email;
    private ImageView ic_warning_password;

    private String email;
    private String password;
    private String password2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);

        emailField = (EditText) findViewById(R.id.email_reg);

        passwordField = (EditText) findViewById(R.id.pass_reg);

        passwordField2 = (EditText) findViewById(R.id.pass2_reg);

        ic_close = (ImageView) findViewById(R.id.ic_close_reg);
        ic_check = (ImageView) findViewById(R.id.ic_check_reg);
        ic_warning_email = (ImageView) findViewById(R.id.ic_alert_email);
        ic_warning_password = (ImageView) findViewById(R.id.ic_alert_pass);

        sendAccount = (Button) findViewById(R.id.btn_account);

        emailField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);
        passwordField2.addTextChangedListener(watcher);

        sendAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailField.getText().toString();
                password = passwordField.getText().toString();
                password2 = passwordField2.getText().toString();

                if(email.equals("")) {
                    ic_warning_email.setVisibility(View.VISIBLE);
                }
                if(password.equals("")) {
                    ic_warning_password.setVisibility(View.VISIBLE);
                }
                /*else {
                    ic_warning_email.setVisibility(View.INVISIBLE);
                    ic_warning_password.setVisibility(View.INVISIBLE);

                    if(password.equals(password2)) {
                        ic_close.setVisibility(View.INVISIBLE);
                        ic_check.setVisibility(View.VISIBLE);
                    }
                    else if(!password.equals(password2)) {
                        ic_close.setVisibility(View.VISIBLE);
                    }
                }*/

            }
        });
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
        }
    };
}
