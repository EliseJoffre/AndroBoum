package fr.dream.elisejoffre.androboum;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AndroBoumApp extends android.app.Application {


    static public void setIsConnected(boolean connectStatus) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser fuser = auth.getCurrentUser();
        if (auth != null) {
            final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mreference = mDatabase.getReference().child("Users").child(fuser.getUid());
            mreference.child("connected").setValue(connectStatus);
        }
    }
}
