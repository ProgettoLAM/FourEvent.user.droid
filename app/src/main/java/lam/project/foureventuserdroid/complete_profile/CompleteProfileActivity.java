package lam.project.foureventuserdroid.complete_profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.TabStepper;


import lam.project.foureventuserdroid.model.User;

public class CompleteProfileActivity extends TabStepper {

    private static final String TAG = CompleteProfileActivity.class.getSimpleName();

    private int i = 1;

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent srcIntent = getIntent();
        user = (User) srcIntent.getParcelableExtra(User.Keys.USER);

        setErrorTimeout(1500);
        setLinear(false);
        setTitle("");
        setAlternativeTab(true);
        setDisabledTouch();
        //setPreviousVisible();

        addStep(createFragment(new Step1Info()));
        addStep(createFragment(new Step2Categories()));
        addStep(createFragment(new Step3Credits()));

        super.onCreate(savedInstanceState);

    }

    private AbstractStep createFragment(AbstractStep fragment) {
        Bundle b = new Bundle();
        b.putInt("position", i++);
        b.putParcelable("user",user);
        fragment.setArguments(b);
        return fragment;
    }

    public void selectedButton(final View view) {

        view.setSelected(!view.isSelected());

        int tag = (int) view.getTag();
        String text = ((TextView) view).getText().toString();

        Log.d(TAG,"Tag : "+tag+" title : "+text);
    }
}
