package lam.project.foureventuserdroid;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.complete_profile.StepManager;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

public class SplashActivity extends AppCompatActivity {

    private static final String IS_DONE_KEY = "IS_DONE_KEY";
    private static final String START_TIME_KEY = "START_TIME_KEY";

    private static final long MIN_WAIT_INTERVAL = 1500L;
    private static final long MAX_WAIT_INTERVAL = 3000L;
    private static final int GO_AHEAD_WHAT = 1;

    private long mStartTime = -1L;
    private boolean mIsDone;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case GO_AHEAD_WHAT:

                    long elapsedTime = SystemClock.uptimeMillis() - mStartTime;
                    if(elapsedTime >= MIN_WAIT_INTERVAL && !mIsDone){

                        mIsDone = true;
                        goAhead();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if(mStartTime == -1L) {

            mStartTime = SystemClock.uptimeMillis();
        }

        final Message goAheadMessage = mHandler.obtainMessage(GO_AHEAD_WHAT);
        mHandler.sendMessageAtTime(goAheadMessage,mStartTime+MAX_WAIT_INTERVAL);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putBoolean(IS_DONE_KEY,mIsDone);
        outState.putLong(START_TIME_KEY,mStartTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mIsDone = savedInstanceState.getBoolean(IS_DONE_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        if(savedInstanceState != null) {

            this.mStartTime = savedInstanceState.getLong(START_TIME_KEY);
        }

        final ImageView logoImageView = (ImageView) findViewById(R.id.splash_imageview);
        assert logoImageView != null;

        //TODO goahead senza il click dell'immagine
        logoImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                long elapsedTime = SystemClock.uptimeMillis() - mStartTime;
                if(elapsedTime >= MIN_WAIT_INTERVAL){

                    goAhead();
                }

                return false;
            }
        });
    }

    private void goAhead() {

        Intent intent;
        User user = UserManager.get(this).getUser();

        if(user == null ){

            StepManager.get(this).setStep(StepManager.INCOMPLETE);
            intent = new Intent(this,RegistrationActivity.class);

        } else {

            intent = new Intent(this,MainActivity.class);
        }

        startActivity(intent);
        finish();
    }


}
