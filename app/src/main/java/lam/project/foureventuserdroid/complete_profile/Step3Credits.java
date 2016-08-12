package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import lam.project.foureventuserdroid.R;

/**
 * Created by Vale on 11/08/2016.
 */

public class Step3Credits extends AbstractStep {

    private int i = 3;
    private Button button;
    private final static String CLICK = "click";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step1_info, container, false);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(CLICK, i);
    }

    @Override
    public String name() {

        for(int i = 0; i < getArguments().size(); i++) {
            switch (getArguments().getInt("position", i)) {
                case 1:
                    return "Dati personali";
                case 2:
                    return "Categorie";
                case 3:
                    return "Microcrediti";

            }
        }
        return null;
        //return "Tab " + getArguments().getInt("position", 0);
    }

    @Override
    public boolean isOptional() {
        return false;
    }


    @Override
    public void onStepVisible() {
    }

    @Override
    public void onNext() {
        System.out.println("onNext");
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    @Override
    public String optional() {
        return "You can skip";
    }

    @Override
    public boolean nextIf() {
        return i > 3;
    }

    @Override
    public String error() {
        for(int i = 0; i < getArguments().size(); i++) {
            switch (getArguments().getInt("position", i)) {
                case 1:
                    return "Compila i dati";
                case 2:
                    return "Scegli almeno una categoria";
                case 3:
                    return "Scopri cosa sono i microcrediti";

            }
        }
        return null;
    }
}
