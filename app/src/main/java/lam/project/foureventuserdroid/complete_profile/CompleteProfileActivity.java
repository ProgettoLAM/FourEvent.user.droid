package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.TabStepper;

import lam.project.foureventuserdroid.R;

public class CompleteProfileActivity extends TabStepper {

    private static final String TAG = CompleteProfileActivity.class.getSimpleName();

    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //setErrorTimeout(1500);
        setTitle("Completa il tuo profilo");

        addStep(createFragment(new Step1Info()));
        addStep(createFragment(new Step2Categories()));
        addStep(createFragment(new Step3Credits()));

        super.onCreate(savedInstanceState);

    }

    private AbstractStep createFragment(AbstractStep fragment) {
        Bundle b = new Bundle();
        b.putInt("position", i++);
        fragment.setArguments(b);
        return fragment;
    }

    public void selectedButton(final View view) {

        Step2Categories.selectedButton(view);

    }
}
