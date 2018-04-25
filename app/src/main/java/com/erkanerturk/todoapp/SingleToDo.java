package com.erkanerturk.todoapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private TextView mSingleToDoName;
    private TextView mSingleToDoDateTime;
    private String uid;
    Dialog myDialog;
    Button mSaveDialogButton, mCancelDialogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_to_do);

        mSingleToDoName = (TextView) findViewById(R.id.singleToDoTitleTextView);
        mSingleToDoDateTime = (TextView) findViewById(R.id.singleToDoTimeTextView);

        String toDoKey = getIntent().getExtras().getString("toDoKey", "");
        String selectedCategoryName = getIntent().getExtras().getString("categoryName", "");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            uid = mFirebaseUser.getUid();
        } else {
            Toast.makeText(getApplicationContext(), "Kullanıcı girişi doğrulanamadı", Toast.LENGTH_LONG).show();
            goLoginActivity();
        }

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("category").child(selectedCategoryName).child(toDoKey);

        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String toDoTitle = (String) dataSnapshot.child("title").getValue();
                String toDoDateTime = (String) dataSnapshot.child("timestamp").getValue();

                mSingleToDoName.setText(toDoTitle);
                mSingleToDoDateTime.setText(toDoDateTime);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSingleToDoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlertDialog();
            }
        });
    }

    public void goLoginActivity() {
        Intent intent = new Intent(SingleToDo.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void updateAlertDialog() {

        myDialog = new Dialog(SingleToDo.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_update_to_do);
        myDialog.setTitle("Güncelleme İşlemi");

        mSaveDialogButton = (Button) myDialog.findViewById(R.id.saveDialogButton);
        mCancelDialogButton = (Button) myDialog.findViewById(R.id.cancelDialogButton);

        mSaveDialogButton.setEnabled(true);
        mCancelDialogButton.setEnabled(true);

        mSaveDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Çalıştı", Toast.LENGTH_SHORT).show();
            }
        });

        mCancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.cancel();
            }
        });
        myDialog.show();
    }
}
