package fr.dream.elisejoffre.androboum;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.io.InputStream;
import java.util.Arrays;

public class UserActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    TextView textView;
    // défini un numéro unique pour repérer plus tard ce code // dans la méthode onActivityResult(...)
    private static final int SELECT_PICTURE = 124;


    @Override
    protected void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_user);
        textView = (TextView) findViewById(R.id.email);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            Log.v("AndroBoum", "je suis déjà connecté sous l'email :" +auth.getCurrentUser().getEmail());

            textView.setText(auth.getCurrentUser().getEmail());
        } else {

            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(Arrays.asList(new
               AuthUI.IdpConfig.GoogleBuilder().build(), new AuthUI.IdpConfig.FacebookBuilder().build()
                                            )).build(), 123);
        }


        ImageView imageView = (ImageView) findViewById(R.id.imageProfil); imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent intent = new Intent(); intent.setType("image/*"); intent.setAction(Intent.ACTION_GET_CONTENT);

                Intent captureIntent = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.setAction(Intent.ACTION_PICK);
                Intent chooserIntent = Intent.createChooser(intent, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS , new Parcelable[] { captureIntent });
                startActivityForResult(chooserIntent, SELECT_PICTURE);
                return true;
            } });




    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// on vérifie que la réponse est bien liée au code de connexion choisi
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Authentification réussie
            if (resultCode == RESULT_OK) {
                Log.v("AndroBoum","je me suis connecté et mon email est :"+
                        response.getEmail());
                if(response.getEmail() != null){
                    textView.setText(response.getEmail());
                }

                return;

            } else {
                // echec de l'authentification
                if (response == null) {
                    finish();
                    return;
                }
                // pas de réseau
                if (response.getError().getErrorCode()== ErrorCodes.NO_NETWORK) { Log.v("AndroBoum","Erreur réseau");
                    finish();
                    return;
                }
                // une erreur quelconque
                if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) { Log.v("AndroBoum","Erreur inconnue");
                    finish();
                    return;
                }
            }
            Log.v("AndroBoum","Réponse inconnue");
        }

        if (requestCode == SELECT_PICTURE) { if (resultCode == RESULT_OK) {
            try {
                ImageView imageView = (ImageView) findViewById(R.id.imageProfil); boolean isCamera = (data.getData() == null);
                final Bitmap selectedImage;
                if (!isCamera) {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream); }
                else {
                    selectedImage = (Bitmap) data.getExtras().get("data");
                }
// on redimensionne le bitmap pour ne pas qu'il soit trop grand
                Bitmap finalbitmap = Bitmap.createScaledBitmap(selectedImage, 500, (selectedImage.getHeight() * 500) / selectedImage.getWidth(), false);
                imageView.setImageBitmap(finalbitmap); }
            catch (Exception e) { Log.v("AndroBoum",e.getMessage());
            }; }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            return true;
        case R.id.action_logout:

            AuthUI.getInstance().signOut(this);
            finish();

        return true;
        default:
            return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onDestroy() {
        AuthUI.getInstance().signOut(this);

        super.onDestroy();
    }
}