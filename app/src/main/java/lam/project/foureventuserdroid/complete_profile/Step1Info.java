package lam.project.foureventuserdroid.complete_profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.fcannizzaro.materialstepper.AbstractStep;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import lam.project.foureventuserdroid.R;
import lam.project.foureventuserdroid.model.User;
import lam.project.foureventuserdroid.utils.DateConverter;
import lam.project.foureventuserdroid.utils.ImageManager;
import lam.project.foureventuserdroid.utils.Utility;
import lam.project.foureventuserdroid.utils.connection.FourEventUri;
import lam.project.foureventuserdroid.utils.connection.HandlerManager;
import lam.project.foureventuserdroid.utils.connection.MultipartRequest;
import lam.project.foureventuserdroid.utils.connection.VolleyRequest;
import lam.project.foureventuserdroid.utils.shared_preferences.UserManager;

public class Step1Info extends AbstractStep{

    public static final int REQUEST_CODE = 1;

    private TextView dateInfo;

    private String mDate;
    private Fragment thisFragment;

    private CircleImageView imgUser;
    private EditText txtName;
    private EditText txtSurname;
    private EditText txtLocation;
    private RadioGroup radioGroup;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    private String mImageUri;
    private User mCurrentUser = UserManager.get().getUser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Focus non automatico su un campo da compilare
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public String name() {
        return "Completa profilo utente";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        thisFragment = this;

        return initView(inflater.inflate(R.layout.step1_info, container, false));
    }

    /**
     * Metodo per inizializzare i campi necessari per aggiungere le info dell'utente
     * @param rootView view dalla quale ricercare gli id degli items
     * @return view completa dei vari riferimenti
     * @see View nell'onCreateView
     */
    private View initView(final View rootView) {

        dateInfo = (TextView) rootView.findViewById(R.id.date_info);
        imgUser = (CircleImageView) rootView.findViewById(R.id.profile_image);
        txtName = (EditText) rootView.findViewById(R.id.name_info);
        txtSurname = (EditText) rootView.findViewById(R.id.surname_info);
        txtLocation = (EditText) rootView.findViewById(R.id.location_info);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_info);
        txtLocation = (EditText) rootView.findViewById(R.id.location_info);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        dateInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment newFragment = new SelectDateFragment();

                newFragment.setTargetFragment(thisFragment,REQUEST_CODE);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        return rootView;
    }

    @Override
    public boolean nextIf() {

        boolean isNotEmptyName = !txtName.getText().toString().matches("") &&
                !txtSurname.getText().toString().matches("");

        //Controllo che il campo nome e cognome non siano vuoti ed aggiungo all'user i vari campi
        if(isNotEmptyName){

            mCurrentUser.addName(txtName.getText().toString()+ " "
                    + txtSurname.getText().toString());

            //Controllo che esista l'immagine del profilo
            if(mImageUri != null) {

                mCurrentUser.updateImage(mImageUri);
            }

            //Controllo che esista la location
            String location = txtLocation.getText().toString();

            if(!location.matches("")) {

                mCurrentUser.addLocation(location);
            }

            //Controllo che esista la data di nascita
            String birthDate = mDate;
            if(birthDate != null && !birthDate.matches("")) {

                mCurrentUser.addBirthDate(birthDate);
            }

            //Controllo che esista il sesso
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if(selectedId != -1) {

                RadioButton genderField = (RadioButton) getActivity().findViewById(selectedId);
                mCurrentUser.addGender(genderField.getText().toString());
            }

            //Inserisco nel modello dell'utente, i dati correnti
            getStepDataFor(1).putParcelable(User.Keys.USER, mCurrentUser);
        }

        return isNotEmptyName;
    }

    @Override
    public String error() { return "Inserisci nome, cognome.";}

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
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    //Endregion

    //Region fetch/scatta immagine + upload server

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (userChoosenTask.equals("Take Photo"))

                        cameraIntent();

                    else if (userChoosenTask.equals("Choose from Library"))

                        galleryIntent();

                }

                break;
        }
    }

    //Risultato della scelta dell'immagine in base al codice che ritorna:
    //- se ritorna "SELECT_FILE" si richiama il metodo per la scelta dalla galleria
    //- se ritorna "REQUEST_CAMERA" si richiama il metodo per la scelta dalla fotocamera
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE) onSelectFromGalleryResult(data);

            else if (requestCode == REQUEST_CAMERA) onCaptureImageResult(data);

        //Se ritorna un altro codice, si prende il risultato del datePicker e si setta la data
        } else if(requestCode == REQUEST_CODE){

            mDate = data.getStringExtra(SelectDateFragment.DATE_RESULT);
            dateInfo.setText(mDate);
        }
    }

    /**
     * Risultato dell'immagine scelta dalla galleria
     * @param data intent che deriva dal risultato della Activity
     */
    private void onSelectFromGalleryResult(Intent data) {

        try {

            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            File createdImage = ImageManager.get().writeImage(mCurrentUser.email,thumbnail);

            if(createdImage != null) {

                imgUser.setImageBitmap(thumbnail);
                uploadImage(createdImage);
            }
        }catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Risultato dell'immagine scattata dalla fotocamera
     * @param data intent che deriva dal risultato della Activity
     */
    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        File createdImage = ImageManager.get().writeImage(mCurrentUser.email,thumbnail);

        if(createdImage != null) {

            imgUser.setImageBitmap(thumbnail);
            uploadImage(createdImage);
        }
    }

    /**
     * Caricamento dell'immagine sul server
     * @param toUploadFile File preso dalla galleria del dispositivo o scattato dalla fotocamera
     */
    private void uploadImage(File toUploadFile) {

        String url = FourEventUri.Builder.create(FourEventUri.Keys.USER)
                .appendPath("img").appendEncodedPath(UserManager.get(getContext()).getUser().email)
                .getUri();

        final ProgressDialog loading = ProgressDialog
                .show(getContext(), "Immagine del profilo", "Caricamento in corso..", false, false);

        MultipartRequest mMultipartRequest = new MultipartRequest(url,
                new Response.ErrorListener() {
                @Override
                    public void onErrorResponse(VolleyError error) {

                    loading.dismiss();

                    Snackbar errorSnackbar = Snackbar.make(getView(), HandlerManager.handleError(error),
                            Snackbar.LENGTH_SHORT);

                    errorSnackbar.getView().setBackgroundColor(ContextCompat
                            .getColor(getContext(), R.color.lightRed));

                    errorSnackbar.show();

                    }
                },
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();

                        Snackbar successSnackbar = Snackbar.make(getView(), "Immagine caricata!",
                                Snackbar.LENGTH_SHORT);

                        successSnackbar.getView().setBackgroundColor(ContextCompat
                                .getColor(getContext(), R.color.lightGreen));

                        successSnackbar.show();

                        mImageUri = response;

                    }
                },toUploadFile,"file");

        VolleyRequest.get(getContext()).add(mMultipartRequest);
    }

    //Endregion

    //Classe relativa alla visualizzazione del calendario per la selezione della data di nascita
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public static String DATE_RESULT = "date";
        private String mDate;

        public SelectDateFragment() {}

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();

            //Si setta come età minima 16 anni
            calendar.add(Calendar.YEAR,-16);
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);

            pickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            pickerDialog.getDatePicker().setMinDate(0);

            pickerDialog.setTitle("");

            return pickerDialog;
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {

            populateSetDate(yy, mm, dd);
        }

        /**
         * Popolamento dei dati, convertiti attraverso la classe DateConverter
         * @param year
         * @param month
         * @param day
         */
        public void populateSetDate(int year, int month, int day) {

            final Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,day);
            mDate = DateConverter.dateFromCalendar(calendar);

            sendResult(REQUEST_CODE);
        }

        /**
         * Invio del risultato della data alla Activity
         * @param REQUEST_CODE
         */
        private void sendResult(int REQUEST_CODE) {

            Intent intent = new Intent();
            intent.putExtra(DATE_RESULT,mDate);
            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
        }
    }
}

