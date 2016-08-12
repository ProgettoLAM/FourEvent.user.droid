package lam.project.foureventuserdroid.complete_profile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.w3c.dom.Text;

import java.util.Calendar;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;

/**
 * Created by Vale on 11/08/2016.
 */

public class Step1Info extends AbstractStep{

    private int i = 1;
    private ImageView calendarDate;
    private static TextView dateInfo;
    private final static String CLICK = "click";

    private EditText nameField;
    private EditText surnameField;
    private EditText locationField;
    private RadioGroup radioGroup;
    private RadioButton sexField;

    private String name;
    private String surname;
    private String location;

    private String email = CompleteProfileActivity.email;
    private String password = CompleteProfileActivity.password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step1_info, container, false);

        calendarDate = (ImageView) v.findViewById(R.id.date_icon);
        dateInfo = (TextView) v.findViewById(R.id.date_info);

        nameField = (EditText) v.findViewById(R.id.name_info);
        surnameField = (EditText) v.findViewById(R.id.surname_info);
        locationField = (EditText) v.findViewById(R.id.location_info);
        radioGroup = (RadioGroup) v.findViewById(R.id.radio_info);
        locationField = (EditText) v.findViewById(R.id.location_info);


        /*ic_warning_name = (ImageView) v.findViewById(R.id.ic_alert_name);
        ic_warning_surname = (ImageView) v.findViewById(R.id.ic_alert_surname);
        ic_warning_location = (ImageView) v.findViewById(R.id.ic_alert_location);

        nameField.addTextChangedListener(watcher);
        surnameField.addTextChangedListener(watcher);
        locationField.addTextChangedListener(watcher);*/


        if (savedInstanceState != null)
            i = savedInstanceState.getInt(CLICK, 0);

        calendarDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        return v;
    }

    /*public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    break;
            case R.id.radio_female:
                if (checked)
                    break;
        }
    }*/

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


    public boolean nextIf(final View view) {

        int selectedId = radioGroup.getCheckedRadioButtonId();

        sexField = (RadioButton) view.findViewById(selectedId);

        Log.d("sex", sexField.getText().toString());
        return i > 1;
    }

    /*public int controlInfo() {
        name = nameField.getText().toString();
        surname = surnameField.getText().toString();
        location = locationField.getText().toString();

        if(name.equals("")) {
            ic_warning_name.setVisibility(View.VISIBLE);
            return 0;
        }
        if(surname.equals("")) {
            ic_warning_surname.setVisibility(View.VISIBLE);
            return 0;
        }
        if(location.equals("")) {
            ic_warning_location.setVisibility(View.VISIBLE);
            return 0;
        }
        else
            return 2;
    }*/


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

    /*private final TextWatcher watcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            if (nameField.getText().toString().length() != 0) {
                ic_warning_name.setVisibility(View.INVISIBLE);
            }
            if(surnameField.getText().toString().length() != 0){
                ic_warning_surname.setVisibility(View.INVISIBLE);
            }
            if(locationField.getText().toString().length() != 0){
                ic_warning_location.setVisibility(View.INVISIBLE);
            }
        }
    };*/


    //Classe relativa alla visualizzazione del dialog del calendario per la selezione della data di nascita
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm+1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            dateInfo.setText(month+"/"+day+"/"+year);
        }

    }

}

