package com.erkanerturk.todoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private TextView mSingleToDoName;
    private TextView mSingleToDoTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_to_do);

        String toDoKey = getIntent().getExtras().getString("ToDoKey", "");
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("ToDos");

        mSingleToDoName = (TextView) findViewById(R.id.singleToDoNameTextView);
        mSingleToDoTime = (TextView) findViewById(R.id.singleToDoTimeTextView);

        mFirebaseDatabaseReference.child(toDoKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String toDoName = (String) dataSnapshot.child("name").getValue();
                String toDoTime = (String) dataSnapshot.child("time").getValue();

                mSingleToDoName.setText(toDoName);
                mSingleToDoTime.setText(toDoTime);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
