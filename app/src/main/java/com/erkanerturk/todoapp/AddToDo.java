package com.erkanerturk.todoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class AddToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference, mFirebaseDatabaseReference2;
    private EditText mToDoNameTextView;
    private FirebaseUser mFirebaseUser;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mFirebaseUser.getUid();
    }

    public void addButtonClicked(View view) {
        mToDoNameTextView = (EditText) findViewById(R.id.addToDoNameTextView);

        String name = mToDoNameTextView.getText().toString().trim();

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf3 = new SimpleDateFormat("dd MM yyy HH:mm");
        String dateString = sdf.format(date);
        String timeString = sdf2.format(date);
        String timestampString = sdf3.format(date);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("category").child("Yazılım");
        DatabaseReference newTask = mFirebaseDatabaseReference.push();
        newTask.child("name").setValue(name);
        newTask.child("date").setValue(dateString);
        newTask.child("time").setValue(timeString);
        newTask.child("timestamp").setValue(timestampString);


        // mFirebaseDatabaseReference.push().setValue(new ToDo(name,timeString));

        mToDoNameTextView.setText("");
    }
}
