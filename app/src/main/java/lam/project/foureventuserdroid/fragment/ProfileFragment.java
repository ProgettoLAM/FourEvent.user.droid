package lam.project.foureventuserdroid.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.hdodenhof.circleimageview.CircleImageView;
import lam.project.foureventuserdroid.MainActivity;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.shared_preferences.ImageManager;
import lam.project.foureventuserdroid.utils.Utility;
import lam.project.foureventuserdroid.utils.connection.CustomRequest;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.MultipartRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

public class ProfileFragment extends Fragment {

    private final String NAME = "Profilo";

    private User user;

    private String oldPassword;
    private String newPassword;
    private Snackbar snackbar;

    private String mImageUri;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    private CircleImageView imgProfile;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setTitle();

        return initView(inflater.inflate(R.layout.fragment_profile, container, false));

    }

    /**
     * Metodo per inizializzare i campi del profilo
     * @param view la view del profilo
     * @return view completa di tutti i campi definiti
     */
    private View initView(View view) {

        //Si prende l'utente salvato
        user = UserManager.get().getUser();

        ImageView editPass = (ImageView) view.findViewById(R.id.change_pass);
        TextView emailProfile = (TextView) view.findViewById(R.id.email_profile);
        TextView nameProfile = (TextView) view.findViewById(R.id.name_profile);
        TextView birthDateProfile = (TextView) view.findViewById(R.id.birth_date_profile);
        TextView locationProfile = (TextView) view.findViewById(R.id.location_profile);
        TextView genderProfile = (TextView) view.findViewById(R.id.gender_profile);
        imgProfile = (CircleImageView) view.findViewById(R.id.profile_image);

        setImage();

        //Al click dell'immagine del profilo si può scegliere quale caricare
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //Ad ogni campo del profilo si assegnano i dati dell'utente (se presenti)
        emailProfile.setText(user.email);
        nameProfile.setText(user.name);

        //Se la data di nascita non è definita
        if(user.birthDate == null || user.birthDate.equals("Data di nascita")) {

            birthDateProfile.setText("--/--/--");
        }
        else {

            birthDateProfile.setText(user.birthDate.split(" - ")[0]);
        }

        //Se la città non è definita
        if(user.location == null) {

            locationProfile.setText(R.string.city);
        }
        else {

            locationProfile.setText(user.location);
        }

        //Se il sesso è definito, in base al genere si setta il testo
        if(user.gender != null) {

            if(user.gender.equals("F")) {

                genderProfile.setText(R.string.female);
            }
            else if(user.gender.equals("M")) {

                genderProfile.setText(R.string.male);
            }
        }

        //Al click dell'icona del cambio della password si apre un dialog
        editPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        return view;
    }

    /**
     * Si setta il titolo del fragment
     */
    private void setTitle() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(NAME);
    }

    //Region intent salvataggio dell'immagine

    private void selectImage() {

        final CharSequence[] items = { "Scatta una foto", "Scegli dalla galleria", "Annulla" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aggiungi un'immagine");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getContext());
                if (items[item].equals("Scatta una foto")) {
                    userChoosenTask = "Scatta una foto";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Scegli dalla galleria")) {
                    userChoosenTask = "Scegli dalla galleria";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Annulla")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getActivity().startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    //Endregion

    //Region fetch/scatta immagine + upload del server

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (userChoosenTask.equals("Take Photo")) cameraIntent();

                    else if (userChoosenTask.equals("Choose from Library")) galleryIntent();
                }

                break;
        }
    }

    //Risultato della scelta dell'immagine in base al codice che ritorna:
    //se ritorna "SELECT_FILE" si richiama il metodo per la scelta dalla galleria
    //se ritorna "REQUEST_CAMERA" si richiama il metodo per la scelta dalla fotocamera
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE) onSelectFromGalleryResult(data);

            else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(data);
        }
    }

    /**
     * Risultato dell'immagine scelta dalla galleria
     * @param data intent che deriva dal risultato della Activity
     */
    private void onSelectFromGalleryResult(Intent data) {

        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            File createdImage = ImageManager.get().writeImage(MainActivity.mCurrentUser.email,thumbnail);

            if(createdImage != null) {

                uploadImage(createdImage);
                imgProfile.setImageBitmap(thumbnail);
                refreshNavbarImage(thumbnail);
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Risultato dell'immagine scattata dalla fotocamera
     * @param data intent che deriva dal risultato della Activity
     */
    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        File createdImage = ImageManager.get().writeImage(MainActivity.mCurrentUser.email,thumbnail);

        if(createdImage != null) {

            uploadImage(createdImage);
            imgProfile.setImageBitmap(thumbnail);
            refreshNavbarImage(thumbnail);
        }
    }

    /**
     * Caricamento dell'immagine sul server
     * @param toUploadFile File preso dalla galleria del dispositivo o scattato dalla fotocamera
     */
    private void uploadImage(File toUploadFile) {

        String url = FourEventUri.Builder.create(FourEventUri.Keys.USER)
                .appendPath("img").appendEncodedPath(user.email).getUri();

        final ProgressDialog loading = ProgressDialog
                .show(getContext(), "Immagine dell'evento", "Caricamento in corso..", false, false);

        MultipartRequest mMultipartRequest = new MultipartRequest(url,
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar.make(getView(), "Errore nel caricamento dell'immagine", Snackbar.LENGTH_SHORT)
                        .show();

                loading.dismiss();
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Snackbar.make(getView(), "Immagine caricata!", Snackbar.LENGTH_SHORT)
                        .show();

                mImageUri = response;

                user.updateImage(mImageUri);

                loading.dismiss();

            }
        },toUploadFile,"filename");

        VolleyRequest.get(getContext()).add(mMultipartRequest);
    }

    /**
     * Si setta l'immagine anche nella navigation view
     * @param bm immagine del profilo
     */
    private void refreshNavbarImage(Bitmap bm) {

        ((CircleImageView) MainActivity.headerView.findViewById(R.id.profile_image))
                .setImageBitmap(bm);
    }

    /**
     * L'immagine viete settata nell'item corrispondente
     */
    private void setImage() {

        Bitmap contentImage = ImageManager.get().readImage(MainActivity.mCurrentUser.email);

        //Se l'utente non ha settato l'immagine, viene inserita una di default
        if(contentImage == null) {

            if(user.gender != null) {
                if(user.gender.equals("F")) {
                    imgProfile.setImageResource(R.drawable.img_female);
                }
            }
        }
        else {

            imgProfile.setImageBitmap(contentImage);
        }
    }

    //Endregion

    /**
     * Update della password, al click dell'icona corrispondente
     */
    private void updatePassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cambia la password");

        //Creazione di un layout per l'inserimento della password attuale e quella nuova
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, (ViewGroup) getView(), false);

        final EditText oldPasswordField = (EditText) viewInflated.findViewById(R.id.old_password);
        final EditText newPasswordField = (EditText) viewInflated.findViewById(R.id.new_password);

        builder.setView(viewInflated);

        //Al click del bottone positivo si controlla sul server se è possibile modificare la password
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                oldPassword = oldPasswordField.getText().toString();
                newPassword = newPasswordField.getText().toString();

                //Se la nuova password inserita ha una lunghezza maggiore/uguale a 8 caratteri
                if(newPassword.length() >= 8) {

                    try {
                        String url = FourEventUri.Builder.create(FourEventUri.Keys.USER)
                                .appendPath("changepassword").appendEncodedPath(user.email)
                                .getUri();

                        //Si passa al server la password attuale e nuova per il confronto
                        JSONObject obj = new JSONObject("{'oldPassword':'"+oldPassword+"'," +
                                " 'newPassword':'"+newPassword+"'}");

                        CustomRequest request = new CustomRequest(Request.Method.POST, url, obj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try{

                                            snackbar = Snackbar
                                                    .make(getView(), response.getString("message"),
                                                            Snackbar.LENGTH_LONG);

                                            snackbar.getView()
                                                    .setBackgroundColor(ContextCompat
                                                            .getColor(getContext(), R.color.lightGreen));

                                            snackbar.show();
                                            dialog.dismiss();

                                        } catch (JSONException e) { e.printStackTrace();}
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        try {

                                            String responseBody = new String( error.networkResponse.data, "utf-8" );
                                            JSONObject jsonObject = new JSONObject( responseBody );

                                            String errorText = (String) jsonObject.get("message");

                                            snackbar = Snackbar
                                                    .make(getView(), errorText, Snackbar.LENGTH_LONG);

                                            snackbar.getView().setBackgroundColor(ContextCompat
                                                    .getColor(getContext(), R.color.lightRed));

                                            snackbar.show();

                                        } catch (NullPointerException | UnsupportedEncodingException | JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        VolleyRequest.get(getContext()).add(request);

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }

                //Se la lunghezza della password è minore di 8 caratteri, si visualizza un messaggio
                else if(newPassword.length() < 8) {
                    snackbar = Snackbar
                            .make(getView(), "La password deve essere almeno di 8 caratteri", Snackbar.LENGTH_LONG);
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
}
