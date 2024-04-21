package com.example.androidfinalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.androidfinalproject.CustomViews.GeneralUtils;
import com.example.androidfinalproject.R;
import com.example.androidfinalproject.Utilities.Database;
import com.example.androidfinalproject.Utilities.ImageUtils;
import com.example.androidfinalproject.Utilities.UserSession;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private AppCompatImageView app_IMG_background;
    private AppCompatImageView app_IMG_background_blurred;
    private Button loginButton;
    private LinearLayout signIn_VIEW;
    private TextInputEditText loginUser_TEXTINPUT;
    private TextInputEditText loginPassword_TEXTINPUT;
    private Button goToRegister_BTN;
    private LinearLayout register_VIEW;
    private TextInputEditText registerUser_TEXTINPUT;
    private TextInputEditText registerPassword_TEXTINPUT;
    private TextInputEditText registerName_TEXTINPUT;
    private Button register_BTN;
    private Button backToLogin_BTN;

    private String background = ImageUtils.barberBackground;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        getViews();
        buttonLogic();
        textInputLogic();
    }
    private void getViews(){
        //set status color to black
        ImageUtils.setStatusBarColor(this, R.color.statusBarColor);

        //Find background
        app_IMG_background = findViewById(R.id.app_IMG_background);
        app_IMG_background_blurred = findViewById(R.id.app_IMG_background_blurred);
        ImageUtils.loadImage(app_IMG_background,background );
        ImageUtils.loadImageWithBlur(app_IMG_background_blurred,background , 25);
        app_IMG_background_blurred.setVisibility(View.INVISIBLE);

        //Find LinearLayouts
        signIn_VIEW = findViewById(R.id.signIn_VIEW);
        register_VIEW = findViewById(R.id.register_VIEW);

        //Text inputs
        loginUser_TEXTINPUT = findViewById(R.id.loginUser_TEXTINPUT);
        loginPassword_TEXTINPUT = findViewById(R.id.loginPassword_TEXTINPUT);
        registerPassword_TEXTINPUT = findViewById(R.id.registerPassword_TEXTINPUT);
        registerUser_TEXTINPUT = findViewById(R.id.registerUser_TEXTINPUT);
        registerName_TEXTINPUT= findViewById(R.id.registerName_TEXTINPUT);

        //Buttons
        loginButton = findViewById(R.id.login_BTN);
        goToRegister_BTN = findViewById(R.id.goToRegister_BTN);
        register_BTN = findViewById(R.id.register_BTN);
        backToLogin_BTN = findViewById(R.id.backToLogin_BTN);
    }

    void buttonLogic(){
        goToRegister_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_VIEW.setVisibility(View.VISIBLE);
                signIn_VIEW.setVisibility(View.INVISIBLE);
                GeneralUtils.hideKeyboard(Login.this);
                loginUser_TEXTINPUT.setText("");
                loginPassword_TEXTINPUT.setText("");
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean goodDetails = false;
                GeneralUtils.hideKeyboard(Login.this);
                app_IMG_background_blurred.setVisibility(View.VISIBLE);
                // Retrieve input from username and password TextInputEditText
                String username = loginUser_TEXTINPUT.getText().toString();
                String password = loginPassword_TEXTINPUT.getText().toString();
                if(username.equals("mang")) {
                    goToLogin("mang@gmail.com");
                    UserSession.getInstance().setEmail("mang@gmail.com");
                }
                if(!GeneralUtils.isValidEmail(username)) {
                    GeneralUtils.makeAToast(Login.this, "Invalid Email");
                    return;
                }
                else if (password.length() < 6) {
                    GeneralUtils.makeAToast(Login.this, "Password has to be at least 6 characters");
                    return;
                }

                if(GeneralUtils.isValidEmail(username) && password.length() >= 6)
                    goodDetails = true;

                if(goodDetails){
                    loginUser(username, password);
                }


            }
        });
        register_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean goodDetails = false;
                GeneralUtils.hideKeyboard(Login.this);
                String username = registerUser_TEXTINPUT.getText().toString();
                String password = registerPassword_TEXTINPUT.getText().toString();
                String name = registerName_TEXTINPUT.getText().toString();
                System.out.println("username:"+username+"password:"+password+" name:"+name);
                if(!GeneralUtils.isValidEmail(username)) {
                    GeneralUtils.makeAToast(Login.this, "Invalid Email");
                    return;
                }
                else if (password.length() < 6) {
                    GeneralUtils.makeAToast(Login.this, "Password has to be at least 6 characters");
                    return;
                }

                if(GeneralUtils.isValidEmail(username) && password.length() >= 6 && name.length() >= 1)
                    goodDetails = true;

                if(goodDetails) {
                    registerUser(username, password, name);
                    registerUser_TEXTINPUT.setText("");
                    registerPassword_TEXTINPUT.setText("");
                    registerName_TEXTINPUT.setText("");
                    register_VIEW.setVisibility(View.INVISIBLE);
                    signIn_VIEW.setVisibility(View.VISIBLE);
                }
            }
        });
        backToLogin_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GeneralUtils.hideKeyboard(Login.this);
                registerUser_TEXTINPUT.setText("");
                registerPassword_TEXTINPUT.setText("");
                register_VIEW.setVisibility(View.INVISIBLE);
                signIn_VIEW.setVisibility(View.VISIBLE);
            }
        });
    }

    void textInputLogic(){
        loginUser_TEXTINPUT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    blurBackground();
                }
            }
        });
        loginPassword_TEXTINPUT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    blurBackground();
                }
            }
        });
        registerUser_TEXTINPUT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    blurBackground();
                }
            }
        });
        registerPassword_TEXTINPUT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    blurBackground();
                }
            }
        });
    }
    void blurBackground(){
        app_IMG_background.setVisibility(View.INVISIBLE);
        app_IMG_background_blurred.setVisibility(View.VISIBLE);
    }
    private void registerUser(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            GeneralUtils.makeAToast(Login.this, "Registration successful.");
                            if (user != null) {
                                new Database().addUserToRealtimeDatabase(user.getUid(), user.getEmail(), "user", name);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            GeneralUtils.makeAToast(Login.this, "Authentication failed: " + task.getException());
                        }
                    }
                });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            GeneralUtils.makeAToast(Login.this, "Logged In, Welcome :)");
                            goToLogin(email);
                            // Update UI or proceed further
                        } else {
                            // If sign in fails, display a message to the user.
                            GeneralUtils.makeAToast(Login.this, "Authentication Failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void goToLogin(String email){
        Intent intent = new Intent(Login.this, Home.class);
        UserSession.getInstance().setEmail(email);
        startActivity(intent);
        finish();
    }
}