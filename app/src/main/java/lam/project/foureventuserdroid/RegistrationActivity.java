package lam.project.foureventuserdroid;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
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

        sendAccount = (Button) findViewById(R.id.btn_account);

        sendAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordField.getText().toString();
                password2 = passwordField2.getText().toString();

                if(password.equals(password2)) {
                    ic_close.setVisibility(View.INVISIBLE);
                    ic_check.setVisibility(View.VISIBLE);
                }
                else if(!password.equals(password2)) {
                    ic_close.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}