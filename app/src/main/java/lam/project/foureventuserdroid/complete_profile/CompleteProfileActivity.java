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

        //Aggiunta dei 3 step per completare il profilo
        addStep(createFragment(new Step1Info()));
        addStep(createFragment(new Step2Categories()));
        addStep(createFragment(new Step3Credits()));

        super.onCreate(savedInstanceState);

    }

    /**
     * Creazione dei 3 fragments, ai quali si assegna ad ognuno una posizione
     * @param fragment fragment di uno step
     * @return fragment con una posizione
     */
    private AbstractStep createFragment(AbstractStep fragment) {
        Bundle b = new Bundle();
        b.putInt("position", i++);
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * Richiamo del metodo di onClick nel fragment delle categorie (secondo step)
     * @param view view del bottone selezionato nel secondo step
     */
    public void selectedButton(final View view) {

        Step2Categories.selectedButton(view);
    }
}
