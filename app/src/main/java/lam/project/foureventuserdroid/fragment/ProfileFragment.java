package lam.project.foureventuserdroid.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.complete_profile.StepManager;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    String oldPassword;
    String newPassword;
    Snackbar snackbar;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profilo");

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        final User user = UserManager.get().getUser();

        ImageView editPass = (ImageView) view.findViewById(R.id.change_pass);

        TextView passProfile = (TextView) view.findViewById(R.id.pass_profile);
        TextView emailProfile = (TextView) view.findViewById(R.id.email_profile);
        TextView nameProfile = (TextView) view.findViewById(R.id.name_profile);
        TextView birthDateProfile = (TextView) view.findViewById(R.id.birth_date_profile);
        TextView locationProfile = (TextView) view.findViewById(R.id.location_profile);
        TextView genderProfile = (TextView) view.findViewById(R.id.gender_profile);

        passProfile.setText(user.password);
        emailProfile.setText(user.email);
        nameProfile.setText(user.name);
        birthDateProfile.setText(user.birthDate);
        locationProfile.setText(user.location);
        if(user.gender.equals("F")) {
            genderProfile.setText("Femmina");
        }
        else if(user.gender.equals("M")) {
            genderProfile.setText("Maschio");
        }


        editPass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Cambia la password");

                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, (ViewGroup) getView(), false);

                final EditText oldPasswordField = (EditText) viewInflated.findViewById(R.id.old_password);
                final EditText newPasswordField = (EditText) viewInflated.findViewById(R.id.new_password);

                builder.setView(viewInflated);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        oldPassword = oldPasswordField.getText().toString();
                        newPassword = newPasswordField.getText().toString();

                        if(user.password.equals(oldPassword) && newPassword.length() >= 8) {

                            try {

                                String url = getResources().getString(R.string.backend_uri_change_pass) + "/" + user.email;

                                JSONObject obj = new JSONObject("{'oldPassword':'"+user.password+"', 'newPassword':'"+newPassword+"'}");

                                CustomRequest request = new CustomRequest(Request.Method.POST, url, obj,

                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {

                                                snackbar = Snackbar
                                                        .make(getView(), response.toString(), Snackbar.LENGTH_LONG);

                                                snackbar.show();

                                                user.updatePassword(newPassword);

                                                UserManager.get().save(user);

                                                dialog.cancel();

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        snackbar = Snackbar
                                                .make(getView(), error.toString(), Snackbar.LENGTH_LONG);

                                        snackbar.show();

                                    }
                                });

                                VolleyRequest.get(getContext()).add(request);

                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                        else if(newPassword.length() < 8) {
                            snackbar = Snackbar
                                    .make(getView(), "La password deve essere almeno di 8 caratteri", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }

                        else {
                            snackbar = Snackbar
                                    .make(getView(), "Password errata, riprova!", Snackbar.LENGTH_LONG);
                            snackbar.show();

                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        return view;

    }

}
