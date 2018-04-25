package com.erkanerturk.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddToDo extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabaseReference, mFirebaseDatabaseReference2;
    private EditText mToDoTitleTextView, mToDoInfoTextView;
    private FirebaseUser mFirebaseUser;
    private String uid;
    private Button mDateTimePickerButton;
    private Spinner mCategorySpinner;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener mDateListener;
    private TimePickerDialog.OnTimeSetListener mTimeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        myCalendar = Calendar.getInstance();
        mToDoTitleTextView = (EditText) findViewById(R.id.addToDoTitleTextView);
        mToDoInfoTextView = (EditText) findViewById(R.id.addToDoInfoTextView);
        mDateTimePickerButton = (Button) findViewById(R.id.dateTimePickerButton);
        mCategorySpinner = (Spinner) findViewById(R.id.categorySpinner);

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

            String title = mToDoTitleTextView.getText().toString().trim();
            String info = mToDoInfoTextView.getText().toString().trim();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy - HH:mm");
            String timestampString = sdf.format(myCalendar.getTime());

            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("category").child(mCategorySpinner.getSelectedItem().toString());
            DatabaseReference newTask = mFirebaseDatabaseReference.push();
            newTask.child("title").setValue(title);
            newTask.child("info").setValue(info);
            newTask.child("status").setValue(false);
            newTask.child("timestamp").setValue(timestampString);


            Toast.makeText(getApplicationContext(), "Eklendi", Toast.LENGTH_LONG).show();

            mToDoTitleTextView.setText("");
            mToDoInfoTextView.setText("");
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
