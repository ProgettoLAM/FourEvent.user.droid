package lam.project.foureventuserdroid.complete_profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;

/**
 * Created by Vale on 11/08/2016.
 */

public class Step3Credits extends AbstractStep {

    private int i = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step3_credits, container, false);

        return v;
    }


    @Override
    public String name() {
        return null;
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
        StepManager.get(getContext()).setStep(StepManager.COMPLETE);

        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();

        return i > 3;
    }

    @Override
    public String error() {

        return null;
    }
}
