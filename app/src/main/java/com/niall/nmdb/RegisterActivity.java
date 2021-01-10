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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.niall.nmdb.entities.User;


public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth mainAuth;
    private EditText emailEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;

    private Intent loginIntent;

    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginIntent = new Intent(this, LoginActivity.class);

        mainAuth = FirebaseAuth.getInstance();

        emailEdit = findViewById(R.id.emailEditText);
        usernameEdit = findViewById(R.id.uNameEditText);
        passwordEdit = findViewById(R.id.passwordEditText);

    }

    @NotNull
    private String getPasswordInput() {
        return passwordEdit.getText().toString();
    }

    @NotNull
    private String getEmailInput() {
        return emailEdit.getText().toString();
    }


    public void onLoginClick(View view){

        startActivity(loginIntent);
    }

    public void onRegisterClick(View view){

       final Intent i = new Intent(this, Navigation.class);

        mainAuth.createUserWithEmailAndPassword(getEmailInput(), getPasswordInput())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Registration successful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mainAuth.getCurrentUser();
                            String userId = user.getUid();

                            User aUser = new User(emailEdit.getText().toString(),usernameEdit.getText().toString());

                            String name = user.getDisplayName();

                            System.out.println(name);

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            db.child("User").child(userId).setValue(aUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Write successful", Toast.LENGTH_LONG).show();

                                    startActivity(i);
                                }
                            });




                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Register failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        //...
                    }
                });
    }
}