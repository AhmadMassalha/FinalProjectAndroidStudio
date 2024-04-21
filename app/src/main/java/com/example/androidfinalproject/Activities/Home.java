package com.example.androidfinalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.androidfinalproject.CustomViews.GeneralUtils;
import com.example.androidfinalproject.Fragments.EditWorkerFragment;
import com.example.androidfinalproject.R;
import com.example.androidfinalproject.Utilities.Database;
import com.example.androidfinalproject.Utilities.ImageUtils;
import com.example.androidfinalproject.Utilities.User;
import com.example.androidfinalproject.Utilities.UserSession;

public class Home extends AppCompatActivity implements EditWorkerFragment.OnWorkerInteractionListener {

    private AppCompatImageView app_IMG_background;
    private LinearLayout app_IMG_calendar;
    private LinearLayout app_IMG_editWorker;
    private LinearLayout app_IMG_editWorkTime;
    private AppCompatImageView app_IMG_logout;
    private AppCompatImageView app_IMG_user;
    private LinearLayout userDetails_IMG;
    private boolean userDataVisible = false;
    private TextView userdata_TEXT;
    private boolean workerFragActive = false;
    boolean userPermission = false;

    private String email;
    private String userDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getViews();

    }

    private void getViews(){
        ImageUtils.setStatusBarColor(this, R.color.statusBarColor);
        app_IMG_background = findViewById(R.id.app_IMG_background);
        ImageUtils.loadImage(app_IMG_background, ImageUtils.barberBackground);

        email = UserSession.getInstance().getEmail();

        app_IMG_calendar = findViewById(R.id.app_IMG_calendar);
        app_IMG_editWorker = findViewById(R.id.app_IMG_editWorker);
        app_IMG_editWorkTime = findViewById(R.id.app_IMG_editWorkTime);
        app_IMG_logout = findViewById(R.id.app_IMG_logout);
        app_IMG_user = findViewById(R.id.app_IMG_user);

        userDetails_IMG = findViewById(R.id.userDetails_IMG);
        userdata_TEXT = findViewById(R.id.userdata_TEXT);
        openUserDetails();
        setupListeners();
    }
    private void setupListeners() {
        app_IMG_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Calendar.class);
                startActivity(intent);
                finish();
            }
        });

        app_IMG_editWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Database().getUserByEmail(email, new Database.OnUserDataReceivedListener() {
                    @Override
                    public void onUserDataReceived(User user) {
                        if (user != null) {
                            if(user.getStatus().equals("manager"))
                                userPermission = true;
                        } else {
                            userDetails = "Email: "+"None" + "\n\nPermission status: None" ;
                        }
                    }
                    @Override
                    public void onError(Exception exception) {
                        // Handle the error
                    }
                });

                if(!workerFragActive && userPermission) {
                    workerFragActive = true;
                    FrameLayout frameLayout = findViewById(R.id.editWorker_frag_container);
                    frameLayout.setVisibility(View.VISIBLE);
                    app_IMG_calendar.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.editWorker_frag_container, new EditWorkerFragment())
                            .addToBackStack(null) // Optional: if you want the back button to return to the previous state
                            .commit();
                }else{
                    if(userPermission) {
                        app_IMG_calendar.setVisibility(View.VISIBLE);
                        workerFragActive = false;
                        removeEditWorkerFragment();
                    }else{
                        GeneralUtils.makeAToast(Home.this, "You don't have permission.");
                    }
                }
            }
        });

        app_IMG_editWorkTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.makeAToast(Home.this, "Feature only available to subscribers");
            }
        });

        app_IMG_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Logout Clicks
                logout();
            }
        });

        app_IMG_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userDataVisible) {
                    userDetails_IMG.setVisibility(View.INVISIBLE);
                    userDataVisible = false;
                }
                else{
                    userDataVisible = true;
                    openUserDetails();
                    userdata_TEXT.setText(userDetails);
                    userDetails_IMG.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void logout() {
        Intent intent = new Intent(Home.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void openUserDetails(){
        new Database().getUserByEmail(email, new Database.OnUserDataReceivedListener() {
            @Override
            public void onUserDataReceived(User user) {
                if (user != null) {
                    UserSession.getInstance().setUserPermission(true);
                    userDetails = "Email: "+user.getEmail() + "\nName: " + user.getName() + "\nPermission status: " + user.getStatus();
                } else {
                    userDetails = "Email: "+"None" + "\n\nPermission status: None" ;
                }
            }

            @Override
            public void onError(Exception exception) {
                // Handle the error
            }
        });
    }

    public void removeEditWorkerFragment() {
        FrameLayout frameLayout = findViewById(R.id.editWorker_frag_container);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            frameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWorkerSelected(User user) {
        //nothing
    }
}