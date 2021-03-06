package lam.project.foureventuserdroid.utils.shared_preferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Manager delle immagini da caricare
 */
public class ImageManager {

    private static final String TAG = "ImageManager";
    private static final String EXT = ".png";

    private static ImageManager instance;

    public static ImageManager get() {

        if(instance == null)
            instance = new ImageManager();

        return instance;
    }

    /**
     * Scrittura dell'immagine nello storage dell'app
     * @param name nome dell'immagine
     * @param bitmap immagine
     * @return file
     */
    public File writeImage(String name, Bitmap bitmap) {

        File image = getOutputMediaFile(name);

        if (image == null) {
            Log.d(TAG,
                    "Errore nella creazione del file, controlla i permessi di storage: ");
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

            return image;
        } catch (FileNotFoundException e) {

            Log.d(TAG, "File non trovato: " + e.getMessage());
            return null;
        } catch (IOException e) {

            Log.d(TAG, "Errore nell'accesso al file: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lettura dell'immagine
     * @param name nome dell'immagine
     * @return immagine
     */
    public Bitmap readImage(String name) {

        File image = getOutputMediaFile(name);

        if(image == null) {
            Log.d(TAG,
                    "Errore nella creazione del file, controlla i permessi di storage: ");
            return null;
        }

        return BitmapFactory.decodeFile(image.getPath());

    }

    private File getOutputMediaFile(String name){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/FourEvent");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        return new File(mediaStorageDir.getPath() + File.separator + name + EXT);
    }
}
