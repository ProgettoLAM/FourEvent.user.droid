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
import lam.project.foureventuserdroid.model.User;

public class Step2Categories extends AbstractStep {

    private int i = 2;

    private static final String TAG = CompleteProfileActivity.class.getSimpleName();

    private static List<Category> categoryList = new ArrayList<>();

    private static int tag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step2_categories, container, false);

        return v;
    }

    public static void selectedButton(View view) {

        view.setSelected(!view.isSelected());

        String title = ((TextView) view).getText().toString();
        Object tagInfo = view.getTag();
        tag = Integer.parseInt(tagInfo.toString());

        if(view.isSelected()) {
            categoryList.add(new Category(tag, title));
        }
        else if(!view.isSelected()) {
            categoryList.remove(new Category(tag, title));
        }

        for(int i = 0; i< categoryList.size(); i++) {
            Log.d(TAG," tag: "+ categoryList.get(i).id +" title: "+categoryList.get(i).name);

        }


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
