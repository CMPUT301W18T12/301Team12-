package com.example.dada.View;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dada.Controller.UserController;
import com.example.dada.Exception.UserException;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText emailText;
    private EditText phoneText;

    private Button signupButton;

    private UserController userController = new UserController(new OnAsyncTaskCompleted() {
        @Override
        public void onTaskCompleted(Object o) {
            User user = (User) o;
            FileIOUtil.saveUserInFile(user, getApplicationContext());
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameText = findViewById(R.id.edit_text_signup_username);
        emailText = findViewById(R.id.edit_text_signup_email);
        phoneText = findViewById(R.id.edit_text_signup_phone);

        signupButton = findViewById(R.id.button_signup);
        assert signupButton != null;

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void signup() {
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String mobile = phoneText.getText().toString();

        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());
        boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean validMobile = Patterns.PHONE.matcher(mobile).matches();

        if (!(validUsername && validEmail && validMobile)) {
            Toast.makeText(this, "Username/Email/Mobile is not valid.", Toast.LENGTH_SHORT).show();
        } else {
            if(username.length() > 8){
                Toast.makeText(this, "max username length is 8 characters", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    User user = new User(username, mobile, email);
                    userController.addUser(user);
                    finish();
                } catch (UserException e) {
                    // if the username has been taken
                    Toast.makeText(this, "Username has been taken.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
