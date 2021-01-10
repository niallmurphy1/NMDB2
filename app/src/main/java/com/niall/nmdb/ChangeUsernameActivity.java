package com.niall.nmdb;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.nmdb.entities.User;

public class ChangeUsernameActivity extends AppCompatActivity {

    private EditText unameEdit;

    private TextView nameView;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String uId = fUser.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        unameEdit = findViewById(R.id.settingsEditTextName);
        nameView = findViewById(R.id.settingsNameTextView);


    }

    public void onChangeDetailsClick(View view){

        changeName();
    }

    public void changeName(){
        String newName = unameEdit.getText().toString();
        DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference("User").child(uId);
        fireDB.child("username").setValue(newName);

        nameView.setText("New username: " + newName);

        Toast.makeText(this, "Updated user name: " + newName, Toast.LENGTH_SHORT).show();
    }
}