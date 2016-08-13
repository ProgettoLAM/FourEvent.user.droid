package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import java.util.ArrayList;
import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Category;

public class Step2Categories extends AbstractStep {

    private int i = 2;

    private static final String TAG = CompleteProfileActivity.class.getSimpleName();

    private static List<Category> categoryList = new ArrayList<Category>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step2_categories, container, false);

        return v;
    }

    public static void selectedButton(View view) {

        view.setSelected(!view.isSelected());

        String text = ((TextView) view).getText().toString();
        int tag = 1;

        if(view.isSelected()) {
            categoryList.add(new Category(tag, text));
        }

        if(!view.isSelected()) {
            categoryList.remove(new Category(tag, text));
        }

        //int tag = (int) view.getTag();

        Log.d(TAG," title : "+ categoryList.get(0));

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

        return i > 2;
    }

    @Override
    public String error() {
        return null;
    }

}
