package com.erkanerturk.todoapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SingleToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private TextView mSingleToDoName, mSingleToDoDateTime, mSingleToDoInfo, mSingleToDoStatus;
    private String uid, toDoKey, selectedCategoryName;
    private Dialog myDialog;
    private Button mSaveDialogButton, mCancelDialogButton;
    private EditText mUpdateTitleEditText, mUpdateInfoEditText;
    private CardView mSingleToDoTitleCardView, mSingleToDoInfoCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_single_to_do);

        mSingleToDoName = (TextView) findViewById(R.id.singleToDoTitleTextView);
        mSingleToDoDateTime = (TextView) findViewById(R.id.singleToDoTimeTextView);
        mSingleToDoInfo = (TextView) findViewById(R.id.singleToDoInfoTextView);
        mSingleToDoStatus = (TextView) findViewById(R.id.singleToDoStatusTextView);
        mSingleToDoTitleCardView = (CardView) findViewById(R.id.singleToDoTitleCardView);
        mSingleToDoInfoCardView = (CardView) findViewById(R.id.singleToDoInfoCardView);

        toDoKey = getIntent().getExtras().getString("toDoKey", "");
        selectedCategoryName = getIntent().getExtras().getString("categoryName", "");

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
                String toDoInfo = (String) dataSnapshot.child("info").getValue();
                Boolean status = (Boolean) dataSnapshot.child("status").getValue();

                mSingleToDoName.setText(toDoTitle);
                mSingleToDoDateTime.setText(toDoDateTime);
                mSingleToDoInfo.setText(toDoInfo);

                if (status != null) {
                    if (status) mSingleToDoStatus.setText("Yapıldı");
                    if (!status) mSingleToDoStatus.setText("Yapılmadı");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSingleToDoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateTitleInfoDialog();
            }
        });

        mSingleToDoInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateTitleInfoDialog();
            }
        });

        mSingleToDoTitleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateTitleInfoDialog();
            }
        });

        mSingleToDoInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateTitleInfoDialog();
            }
        });
    }

    public void goLoginActivity() {
        Intent intent = new Intent(SingleToDo.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showUpdateTitleInfoDialog() {
        myDialog = new Dialog(SingleToDo.this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.dialog_update_to_do);
        myDialog.setTitle("Güncelleme İşlemi");

        mSaveDialogButton = (Button) myDialog.findViewById(R.id.saveDialogButton);
        mCancelDialogButton = (Button) myDialog.findViewById(R.id.cancelDialogButton);
        mUpdateTitleEditText = (EditText) myDialog.findViewById(R.id.updateTitleEditText);
        mUpdateInfoEditText = (EditText) myDialog.findViewById(R.id.updateInfoEditText);

        mUpdateInfoEditText.setMovementMethod(new ScrollingMovementMethod());

        mSaveDialogButton.setEnabled(true);
        mCancelDialogButton.setEnabled(true);

        mUpdateTitleEditText.setText(mSingleToDoName.getText().toString());
        mUpdateTitleEditText.setSelection(mSingleToDoName.getText().length());
        mUpdateInfoEditText.setText(mSingleToDoInfo.getText().toString());

        mSaveDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpdateTitleEditText.getText().toString().length() > 3 && mUpdateTitleEditText.getText().toString().length() <= 18) {
                    updateFirebase(mUpdateTitleEditText.getText().toString(), mUpdateInfoEditText.getText().toString());
                } else {
                    mUpdateTitleEditText.setError("Başlık uzunluğu 3-18 karakter arasında olmalı ");
                }
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

    public void updateFirebase(String title, String info) {
        HashMap<String, Object> newData = new HashMap<String, Object>();
        newData.put("title", title);
        newData.put("info", info);

        mFirebaseDatabaseReference.updateChildren(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myDialog.cancel();
                        Toast.makeText(getApplicationContext(), R.string.update, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), R.string.failUpdate, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
