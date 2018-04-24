package com.erkanerturk.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTaskRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ToDo, TaskViewHolder> mFirebaseAdapter;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String uid;
    private String selectedCategoryName;
    private TextView mcategoryTextView, mBannerDay;

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
        mcategoryTextView = (TextView) findViewById(R.id.categoryTextView);
        mBannerDay = (TextView) findViewById(R.id.bannerDayTextView);
        mcategoryTextView.setText("Kişisel");
        selectedCategoryName = mcategoryTextView.getText().toString();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mFirebaseUser.getUid();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mFirebaseAuth.getCurrentUser() != null) {

                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id != (R.id.nav_info | R.id.nav_signout)) {
                    mcategoryTextView.setText(item.getTitle());
                    selectedCategoryName = item.getTitle().toString();
                    startFirebaseAdapter();
                }

                if (id == R.id.nav_info) {

                } else if (id == R.id.nav_signout) {
                    mFirebaseAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView mToDoTitle;
        TextView mToDoTime;
        ImageView mToDoStatus;

        public TaskViewHolder(View itemView) {
            super(itemView);

            mToDoTitle = (TextView) itemView.findViewById(R.id.toDoNameTextView);
            mToDoTime = (TextView) itemView.findViewById(R.id.toDoTimeTextView);
            mToDoStatus = (ImageView) itemView.findViewById(R.id.toDoStageImageView);
        }

        public void bindTask(ToDo toDo) {
/*            Picasso.with(mContext)
                    .load(restaurant.getImageUrl())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(restaurantImageView);
*/
            mToDoTitle.setText(toDo.getTitle());
            mToDoTime.setText(toDo.getTimestamp());

            if (toDo.isStatus() == true) {
                mToDoStatus.setVisibility(View.VISIBLE);
            } else {
                mToDoStatus.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE");
        String dayOfTheWeek = sdfDay.format(new Date());
        mBannerDay.setText(dayOfTheWeek);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAuth.addAuthStateListener(authStateListener);

        startFirebaseAdapter();
        startItemTouchHelper();
    }

    public void startFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ToDo, TaskViewHolder>(
                ToDo.class,
                R.layout.to_do_row,
                TaskViewHolder.class,
                mFirebaseDatabaseReference.child("users").child(uid).child("category").child(selectedCategoryName).orderByChild("timestamp")
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
    }

    public void startItemTouchHelper() {
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
                            Toast.makeText(getApplicationContext(), R.string.removed, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.failRemoved, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    HashMap<String, Object> newEditionTimeHM = new HashMap<String, Object>();
                    newEditionTimeHM.put("status", true);
                    mFirebaseAdapter.getRef(position).updateChildren(newEditionTimeHM)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), R.string.update, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), R.string.failUpdate, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                mFirebaseAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mTaskRecyclerView);
    }
}
