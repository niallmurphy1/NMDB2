package com.niall.nmdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {



    public DatabaseReference db;

    private EditText loginPwordEdit;
    private EditText loginEmailEdit;
    private FirebaseAuth mAuth;

    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        loginPwordEdit = findViewById(R.id.loginPasswordEditText);
        loginEmailEdit = findViewById(R.id.loginEmailEditText);
    }


    @NotNull
    private String getPasswordInput() {
        return loginPwordEdit.getText().toString();
    }

    @NotNull
    private String getEmailInput() {
        return loginEmailEdit.getText().toString();
    }




    public void onLoginClick(View view ){

        System.out.println("login clicked");

        final Intent nav = new Intent(this, Navigation.class);

        mAuth.signInWithEmailAndPassword(getEmailInput(), getPasswordInput())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            Toast.makeText(LoginActivity.this, "Login success.",
                                    Toast.LENGTH_SHORT).show();

                            db = FirebaseDatabase.getInstance().getReference().child("User");
                            String key = db.push().getKey();

                            Toast.makeText(LoginActivity.this, userId,
                                    Toast.LENGTH_LONG).show();


                            startActivity(nav);


                            // startActivity(dash);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }
                });
    }


}
