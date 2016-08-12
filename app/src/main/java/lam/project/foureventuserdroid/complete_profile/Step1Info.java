package lam.project.foureventuserdroid.complete_profile;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.fcannizzaro.materialstepper.AbstractStep;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;

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
    private RadioButton genderField;

    public String name;
    public String location;
    public String gender;
    private String birthDate;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = bundle.getParcelable("user");
            Log.d("user", user.email);

        }

    }

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

    @Override
    public boolean nextIf() {

        name = nameField.getText().toString()+ " "+ surnameField.getText().toString();
        location = locationField.getText().toString();
        birthDate = dateInfo.getText().toString();

        int selectedId = radioGroup.getCheckedRadioButtonId();

        if(selectedId != -1) {
            genderField = (RadioButton) getActivity().findViewById(selectedId);
            gender = genderField.getText().toString();
            user.addName(name).addLocation(location).addGender(gender).addBirthDate(birthDate);
        }
        else
            user.addName(name).addLocation(location).addBirthDate(birthDate);

        try {

            String url = getResources().getString(R.string.backend_uri_put_user)+"/"+user.email;

            CustomRequest request = new CustomRequest(Request.Method.POST, url, user.toJson(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Snackbar snackbar = Snackbar
                                    .make(getView(), response.toString(), Snackbar.LENGTH_LONG);

                            snackbar.show();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    //progressDialog.hide();

                    Snackbar snackbar = Snackbar
                            .make(getView(), error.toString(), Snackbar.LENGTH_LONG);

                    snackbar.show();

                }
            });

            VolleyRequest.get(getContext()).add(request);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        return i > 1;
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

