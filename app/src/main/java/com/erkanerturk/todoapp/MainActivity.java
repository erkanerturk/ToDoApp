package com.erkanerturk.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTaskRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ToDo, TaskViewHolder> mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_main);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(MainActivity.this, AddToDo.class);
                startActivity(addTaskIntent);
            }
        });

        mTaskRecyclerView = (RecyclerView) findViewById(R.id.toDoRecyclerView);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("ToDos");

        setTime();
    }

    TextView mBannerDay;
    TextView mBannerDate;
    SimpleDateFormat sdfDay;
    SimpleDateFormat sdfTime;
    Handler handler;
    Runnable refresher;

    public void setTime() {
        mBannerDay = (TextView) findViewById(R.id.bannerDayTextView);
        mBannerDate = (TextView) findViewById(R.id.bannerDateTextView);

        sdfDay = new SimpleDateFormat("EEEE");
        sdfTime = new SimpleDateFormat("dd MM yyy HH:mm:ss");

        handler = new Handler();

        refresher = new Runnable() {
            @Override
            public void run() {

                Date d = new Date();
                String dayOfTheWeek = sdfDay.format(d);
                mBannerDay.setText(dayOfTheWeek);

                long date = System.currentTimeMillis();
                String dateString = sdfTime.format(date);
                mBannerDate.setText(dateString);

                handler.postDelayed(refresher, 100);
            }
        };
        handler.post(refresher);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void bindTask(ToDo toDo) {
            TextView mToDoName = (TextView) mView.findViewById(R.id.toDoNameTextView);
            TextView mToDoTime = (TextView) mView.findViewById(R.id.toDoTimeTextView);
/*
            Picasso.with(mContext)
                    .load(restaurant.getImageUrl())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(restaurantImageView);
*/
            mToDoName.setText(toDo.getName());
            mToDoTime.setText(toDo.getTime());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ToDo, TaskViewHolder>(
                ToDo.class,
                R.layout.to_do_row,
                TaskViewHolder.class,
                mFirebaseDatabaseReference.orderByChild("time")
        ) {
            @Override
            protected void populateViewHolder(final TaskViewHolder viewHolder, ToDo model, final int position) {

                final String TO_DO_KEY = getRef(position).getKey();
                viewHolder.bindTask(model);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent singleTaskActivity = new Intent(MainActivity.this, SingleToDo.class);
                        singleTaskActivity.putExtra("ToDoKey", TO_DO_KEY);
                        startActivity(singleTaskActivity);
                    }
                });
            }
        };
        mTaskRecyclerView.setHasFixedSize(true);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTaskRecyclerView.setAdapter(mFirebaseAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.RIGHT) {
                    mFirebaseAdapter.getRef(position).removeValue();
                } else {
                    System.out.println(mFirebaseAdapter.getRef(position).getKey());
                }

                mFirebaseAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mTaskRecyclerView);
    }
}
