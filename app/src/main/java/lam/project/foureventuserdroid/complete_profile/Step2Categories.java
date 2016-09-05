package lam.project.foureventuserdroid.complete_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import java.util.List;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.Category;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.shared_preferences.CategoryManager;

public class Step2Categories extends AbstractStep {

    List<Category> mCategoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.step2_categories, container, false);
    }

    /**
     * Metodo onClick di ogni categoria
     * @param view view del bottone selezionato
     */
    public static void selectedButton(View view) {

        //Si seleziona graficamente il bottone
        view.setSelected(!view.isSelected());
        Button button = (Button) view;

        //Si prende il tag ed il titolo e si inseriscono nell'array presente in CategoryManager
        int tag = Integer.parseInt(button.getTag().toString());
        String title = ((TextView) view).getText().toString();

        CategoryManager.get().AddOrRemoveFavouriteInCache(Category.Builder.create(tag,title).build());
    }

    @Override
    public String name() {
        return "Seleziona categorie";
    }

    @Override
    public void onNext() {
        super.onNext();

        //Si riprende l'utente creato nello step 1
        User createdUser = getStepDataFor(1).getParcelable(User.Keys.USER);

        //Salvataggio della lista delle categorie in una variabile
        mCategoryList = CategoryManager.get().getFavouriteCategories();

        //Si aggiungono le categorie all'utente
        if (createdUser != null) {
            createdUser.addCategories(mCategoryList);
        }
        getStepDataFor(2).putParcelable(User.Keys.USER,createdUser);
    }

    @Override
    public boolean nextIf() {

        //Si prosegue nello step 3, se è stata selezionata almeno una categoria
        return mCategoryList.size() > 0;
    }

    @Override
    public String error() {
        return "Seleziona almeno una categoria";
    }

}
