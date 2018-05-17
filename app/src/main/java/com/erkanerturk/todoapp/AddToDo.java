package com.erkanerturk.todoapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mToDoTitleTextView, mToDoInfoTextView;
    private FirebaseUser mFirebaseUser;
    private String uid;
    private Spinner mCategorySpinner;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener mDateListener;
    private TimePickerDialog.OnTimeSetListener mTimeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_to_do);

        myCalendar = Calendar.getInstance();
        mToDoTitleTextView = (EditText) findViewById(R.id.addToDoTitleTextView);
        mToDoInfoTextView = (EditText) findViewById(R.id.addToDoInfoTextView);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        mToDoInfoTextView.setMovementMethod(new ScrollingMovementMethod());

        String[] categorys = {"Kişisel", "Spor", "Sağlık", "Yazılım", "Diğer"};
        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, categorys);
        mCategorySpinner.setAdapter(spinnerAdapter);

        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                toastDateTimeInfo();
            }
        };

        mTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                toastDateTimeInfo();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            uid = mFirebaseUser.getUid();
        } else {
            Toast.makeText(getApplicationContext(), "Kullanıcı girişi doğrulanamadı", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void addButtonClicked(View view) {
        if (mFirebaseUser != null) {

            // TextView ler den veriler değişkene atandı
            String title = mToDoTitleTextView.getText().toString().trim();
            String info = mToDoInfoTextView.getText().toString().trim();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm");
            Date timeMillis = myCalendar.getTime();
            // Seçilen tarih long tipinde millisaniye olarak alındı
            String timestampString = sdf.format(myCalendar.getTime());

            if (title.length() > 3 && title.length() <= 18) {
                // Firebase de verinin nereye ekleneceği belirtirdi
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("category").child(mCategorySpinner.getSelectedItem().toString());
                // Push işlemi
                mFirebaseDatabaseReference.push().setValue(new ToDo(title, info, false, timestampString, timeMillis)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mToDoTitleTextView.setText("");
                        mToDoInfoTextView.setText("");
                        Toast.makeText(getApplicationContext(), "Eklendi", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("category", mCategorySpinner.getSelectedItem().toString());
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Ekleme Başarısız", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Uyarı");
                builder.setMessage("Başlık uzunluğu 3-18 karakter arasında olmalı ");
                builder.create().show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Kullanıcı girişi doğrulanamadı", Toast.LENGTH_LONG).show();
        }
    }

    public void showDateTimePickerDialog(View view) {

        new TimePickerDialog(AddToDo.this, mTimeListener, myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE), true).show();

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddToDo.this, mDateListener, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void toastDateTimeInfo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyy HH:mm");
        Toast.makeText(getApplicationContext(), "Tarih güncellendi: " + sdf.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
    }
}
