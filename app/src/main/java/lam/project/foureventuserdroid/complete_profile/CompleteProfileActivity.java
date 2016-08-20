package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.view.View;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.DotStepper;
import lam.project.foureventuserdroid.utils.shared_preferences.CategoryManager;

public class CompleteProfileActivity extends DotStepper {

    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle("Completa il tuo profilo");

        CategoryManager.get(this);

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
