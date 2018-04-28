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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTaskRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<ToDo, ToDoViewHolder> mFirebaseAdapter;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String uid, selectedCategoryName;
    private TextView mcategoryTextView, mBannerDay;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.activity_main);

        final FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTaskIntent = new Intent(MainActivity.this, AddToDo.class);
                startActivity(addTaskIntent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });

        mTaskRecyclerView = (RecyclerView) findViewById(R.id.toDoRecyclerView);
        mcategoryTextView = (TextView) findViewById(R.id.categoryTextView);
        mBannerDay = (TextView) findViewById(R.id.bannerDayTextView);

        String category = getIntent().getExtras().getString("category", "Kişisel");
        switch (category) {
            case "Kişisel":
                selectedCategoryName = "Kişisel";
                break;
            case "Spor":
                selectedCategoryName = "Spor";
                break;
            case "Sağlık":
                selectedCategoryName = "Sağlık";
                break;
            case "Yazılım":
                selectedCategoryName = "Yazılım";
                break;
            case "Diğer":
                selectedCategoryName = "Diğer";
                break;
            default:
                break;
        }
        mcategoryTextView.setText(selectedCategoryName);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            uid = mFirebaseUser.getUid();
        } else {
            Toast.makeText(getApplicationContext(), "Kullanıcı girişi doğrulanamadı", Toast.LENGTH_LONG).show();
            goLoginActivity();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mFirebaseAuth.getCurrentUser() != null) {

                } else {
                    goLoginActivity();
                }
            }
        };

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id != R.id.nav_signout) {
                    mcategoryTextView.setText(item.getTitle());
                    selectedCategoryName = item.getTitle().toString();
                    startFirebaseAdapter();

                } else if (id == R.id.nav_signout) {
                    mFirebaseAuth.signOut();
                    goLoginActivity();
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder {
        TextView mToDoTitle;
        TextView mToDoDateTime;
        ImageView mToDoStatus;

        public ToDoViewHolder(View itemView) {
            super(itemView);

            mToDoTitle = (TextView) itemView.findViewById(R.id.toDoNameTextView);
            mToDoDateTime = (TextView) itemView.findViewById(R.id.toDoDateTimeTextView);
            mToDoStatus = (ImageView) itemView.findViewById(R.id.toDoStageImageView);
        }

        public void bindToDo(ToDo toDo) {

            mToDoTitle.setText(toDo.getTitle());
            mToDoDateTime.setText(toDo.getTimestamp());

            if (toDo.isStatus()) {
                //mToDoStatus.setVisibility(View.VISIBLE);
                mToDoStatus.setImageResource(R.drawable.ic_checked);
            } else {
                //mToDoStatus.setVisibility(View.INVISIBLE);
                mToDoStatus.setImageResource(R.drawable.ic_no_checked);
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

        View hView = navigationView.getHeaderView(0);
        TextView mNavigationUserName = (TextView) hView.findViewById(R.id.nameTextView);
        TextView mNavigationUserEmail = (TextView) hView.findViewById(R.id.emailTextView);
        mNavigationUserName.setText(mFirebaseUser.getDisplayName().toString());
        mNavigationUserEmail.setText(mFirebaseUser.getEmail().toString());

        mFirebaseAuth.addAuthStateListener(authStateListener);

        startFirebaseAdapter();
        startItemTouchHelper();
    }

    public void goLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void startFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ToDo, ToDoViewHolder>(
                ToDo.class,
                R.layout.to_do_row,
                ToDoViewHolder.class,
                mFirebaseDatabaseReference.child("users").child(uid).child("category").child(selectedCategoryName).orderByChild("timestamp")
        ) {
            @Override
            protected void populateViewHolder(final ToDoViewHolder viewHolder, final ToDo model, final int position) {
                final String TO_DO_KEY = getRef(position).getKey();
                viewHolder.bindToDo(model);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!TO_DO_KEY.equals("") && TO_DO_KEY != null) {
                            Intent intent = new Intent(MainActivity.this, SingleToDo.class);
                            intent.putExtra("toDoKey", TO_DO_KEY);
                            intent.putExtra("categoryName", selectedCategoryName);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        }
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
                final int position = viewHolder.getAdapterPosition();

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
                    mFirebaseAdapter.getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean status = dataSnapshot.child("status").getValue(Boolean.class);
                            updateStatus(position, status);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                mFirebaseAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mTaskRecyclerView);
    }

    public void updateStatus(int position, boolean status) {
        HashMap<String, Object> newStatus = new HashMap<String, Object>();

        if (status) {
            newStatus.put("status", false);
        } else {
            newStatus.put("status", true);
        }

        mFirebaseAdapter.getRef(position).updateChildren(newStatus)
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
}
