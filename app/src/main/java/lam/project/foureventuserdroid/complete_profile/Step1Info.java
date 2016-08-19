package lam.project.foureventuserdroid.complete_profile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;

import java.util.Calendar;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.shared_preferences.FavouriteManager;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

/**
 * Created by Vale on 11/08/2016.
 */

public class Step1Info extends AbstractStep{

    private int i = 1;
    private LinearLayout birth_date;
    private static TextView dateInfo;

    private EditText nameField;
    private EditText surnameField;
    private EditText locationField;
    private RadioGroup radioGroup;
    private RadioButton genderField;

    public String name;
    public String surname;
    public String completename;
    public String location;
    public String gender;
    private String birthDate;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.step1_info, container, false);

        birth_date = (LinearLayout) v.findViewById(R.id.birth_date);
        dateInfo = (TextView) v.findViewById(R.id.date_info);

        nameField = (EditText) v.findViewById(R.id.name_info);
        surnameField = (EditText) v.findViewById(R.id.surname_info);
        locationField = (EditText) v.findViewById(R.id.location_info);
        radioGroup = (RadioGroup) v.findViewById(R.id.radio_info);
        locationField = (EditText) v.findViewById(R.id.location_info);


        birth_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        return v;
    }



    @Override
    public boolean nextIf() {


        if(!nameField.getText().toString().matches("") &&
                !surnameField.getText().toString().matches("")){

            name = nameField.getText().toString();
            surname = surnameField.getText().toString();
            completename = nameField.getText().toString()+ " "+ surnameField.getText().toString();
            location = locationField.getText().toString();
            birthDate = dateInfo.getText().toString();

            user = UserManager.get().getUser();

            int selectedId = radioGroup.getCheckedRadioButtonId();

            if(name.trim().length() == 0 && surname.trim().length() == 0 && selectedId == -1 &&
                    location.trim().length() == 0 && birthDate.equals("Data di nascita")) {
                return true;
            }

            else if(selectedId != -1) {
                genderField = (RadioButton) getActivity().findViewById(selectedId);
                gender = genderField.getText().toString();
                user.addName(completename).addLocation(location).addGender(gender).addBirthDate(birthDate);
            }
            else
                user.addName(completename).addLocation(location).addBirthDate(birthDate);

            getStepDataFor(1).putParcelable(User.Keys.USER,user);

        } else {

            Snackbar.make(getView(),"Nome e Cognome obbligatori", Snackbar.LENGTH_SHORT).show();
        }

        return true;
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

