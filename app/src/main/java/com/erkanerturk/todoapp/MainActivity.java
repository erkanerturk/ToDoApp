package com.erkanerturk.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTaskRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ToDo, TaskViewHolder> mFirebaseAdapter;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String uid;

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

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mFirebaseUser.getUid();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();

                if (mFirebaseUser != null) {

                } else {
                    mFirebaseAuth.removeAuthStateListener(authStateListener);
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


        setTime();
    }

    TextView mBannerDay, mBannerDate;
    SimpleDateFormat sdfDay, sdfTime;
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
        TextView mToDoName;
        TextView mToDoTime;

        public TaskViewHolder(View itemView) {
            super(itemView);

            mToDoName = (TextView) itemView.findViewById(R.id.toDoNameTextView);
            mToDoTime = (TextView) itemView.findViewById(R.id.toDoTimeTextView);
        }

        public void bindTask(ToDo toDo) {

/*            Picasso.with(mContext)
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
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ToDo, TaskViewHolder>(
                ToDo.class,
                R.layout.to_do_row,
                TaskViewHolder.class,
                //mFirebaseDatabaseReference.child("ToDos").orderByChild("time")
                mFirebaseDatabaseReference.child("users").child(uid).child("category").child("Yazılım").orderByChild("timestamp")
        ) {
            @Override
            protected void populateViewHolder(final TaskViewHolder viewHolder, ToDo model, final int position) {

                final String TO_DO_KEY = getRef(position).getKey();
                viewHolder.bindTask(model);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, SingleToDo.class);
                        intent.putExtra("ToDoKey", TO_DO_KEY);
                        startActivity(intent);
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
                    mFirebaseAdapter.getRef(position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    System.out.println(mFirebaseAdapter.getRef(position).getKey());
                }

                mFirebaseAdapter.notifyDataSetChanged();
            }
/*
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }*/
        }).attachToRecyclerView(mTaskRecyclerView);
    }
}
