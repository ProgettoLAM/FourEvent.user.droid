package lam.project.foureventuserdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import lam.project.foureventuserdroid.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Controllo se mostrare la registrazione
        final User user = UserManager.get(this).getUser();
        if(user == null){

            Intent intent = new Intent(this,RegistrationActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private View.OnClickListener buttonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            int itemId = v.getId();

            switch (itemId){

                default: break;
            }

        }
    };

}
