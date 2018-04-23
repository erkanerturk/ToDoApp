package com.erkanerturk.todoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class AddToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mToDoNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);
    }

    public void addButtonClicked(View view) {
        mToDoNameTextView = (EditText) findViewById(R.id.addToDoNameTextView);

        String name = mToDoNameTextView.getText().toString();

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String dateString = sdf.format(date);
        String timeString = sdf2.format(date);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("ToDos");
        DatabaseReference newTask = mFirebaseDatabaseReference.push();
        newTask.child("name").setValue(name);
        newTask.child("date").setValue(dateString);
        newTask.child("time").setValue(timeString);
    }
}
