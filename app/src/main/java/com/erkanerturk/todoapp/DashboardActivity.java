package com.erkanerturk.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView personalCard, sportCard, healthCard, softwareCard, otherCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        personalCard = (CardView) findViewById(R.id.personal_card);
        sportCard = (CardView) findViewById(R.id.sport_card);
        healthCard = (CardView) findViewById(R.id.health_card);
        softwareCard = (CardView) findViewById(R.id.software_card);
        otherCard = (CardView) findViewById(R.id.other_card);

        personalCard.setOnClickListener(this);
        sportCard.setOnClickListener(this);
        healthCard.setOnClickListener(this);
        softwareCard.setOnClickListener(this);
        otherCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);

        switch (v.getId()) {
            case R.id.personal_card:
                intent.putExtra("category", "Kişisel");
                break;
            case R.id.sport_card:
                intent.putExtra("category", "Spor");
                break;
            case R.id.health_card:
                intent.putExtra("category", "Sağlık");
                break;
            case R.id.software_card:
                intent.putExtra("category", "Yazılım");
                break;
            case R.id.other_card:
                intent.putExtra("category", "Diğer");
                break;
            default:
                break;
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }
}
