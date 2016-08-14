package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Category;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.shared_preferences.CategoryManager;

public class Step2Categories extends AbstractStep {

    private int i = 2;

    private static final String TAG = CompleteProfileActivity.class.getSimpleName();

    private static int tag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.step2_categories, container, false);
    }

    public static void selectedButton(View view) {

        view.setSelected(!view.isSelected());

        Button button = (Button) view;

        int tag = Integer.parseInt(button.getTag().toString());
        String title = ((TextView) view).getText().toString();

        CategoryManager.get().AddOrRemoveFavourite(Category.Builder.create(tag,title).build());

        /*
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
        */

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


        User user = getStepDataFor(1).getParcelable(User.Keys.USER);

        user.addCategories(CategoryManager.get().getFavouriteCategories());

        getStepDataFor(2).putParcelable(User.Keys.USER,user);

        return i > 2;
    }

    @Override
    public String error() {
        return null;
    }

}
