package com.example.dada.View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class UserEditProfileActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText emailText;
    private EditText mobileText;

    private Button saveButton;
    private User user;

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
        setContentView(R.layout.activity_user_edit_profile);

        user = FileIOUtil.loadUserFromFile(getApplicationContext());

        usernameText = findViewById(R.id.editText_userName_EditUserProfileActivity);
        emailText = findViewById(R.id.editText_email_EditUserProfileActivity);
        mobileText = findViewById(R.id.editText_mobile_EditUserProfileActivity);

        // get the user, use the user to setText
        usernameText.setText(user.getUserName());   // setText(user.getName)
        emailText.setText(user.getEmail());      // setText(user.getEmail)
        mobileText.setText(user.getPhone());     // setText(user.getMobile)

        saveButton = findViewById(R.id.button_save_EditUserProfileActivity);
        assert saveButton != null;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * save and upload the new profile to the ES server
     */
    public void editProfile(){
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String mobile = mobileText.getText().toString();

        String oldUserName = user.getUserName();

        user.setUserName(username);
        user.setEmail(email);
        user.setPhone(mobile);

        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());
        boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        boolean validMobile = Patterns.PHONE.matcher(mobile).matches();

        if ( !(validUsername && validEmail && validMobile) ){
            Toast.makeText(this, "Username/Email/Mobile is not valid.", Toast.LENGTH_SHORT).show();
        }
        else if(username.length() > 8){
            Toast.makeText(this, "max username length is 8 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                Log.i("Debug", user.getID());
                userController.updateUser(user, oldUserName);
                finish();
            } catch (UserException e) {
                // if the username has been taken
                Toast.makeText(this, "Username has been taken.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
