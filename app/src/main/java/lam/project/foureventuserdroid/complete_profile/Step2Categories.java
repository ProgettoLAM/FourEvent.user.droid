package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import lam.project.foureventuserdroid.R;

public class Step2Categories extends AbstractStep {

    private int i = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step2_categories, container, false);

        return v;
    }

    public void selectedButton(View view) {

        view.setSelected(!view.isSelected());

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
        return true;
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
        return null;
    }

    @Override
    public boolean nextIf() {

        CompleteManager.get(getContext()).setStep(CompleteManager.SECOND_STEP);
        return i > 2;
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
