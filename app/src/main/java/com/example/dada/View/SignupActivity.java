package com.example.dada.View;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dada.Controller.UserController;
import com.example.dada.Exception.UserException;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;

import org.osmdroid.ResourceProxy;

import java.io.File;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText emailText;
    private EditText phoneText;
    private ImageView imageHead;
    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap head;
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
        imageHead = findViewById(R.id.circleHead);
        if (head == null) {
            imageHead.setImageResource(R.drawable.temp_head);
        }
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

    // stackoverflow
    public void addImage(View view) {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            // https://blog.csdn.net/nupt123456789/article/details/7844076
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();

            File file = new File(path);
            if (file.length() > 65536) {
                Toast.makeText(this, "Image file is larger than 64 KB.", Toast.LENGTH_SHORT);
                return;
            }
            if (file.length() == 0) {
                Toast.makeText(this, "File cannot be access or not exist.", Toast.LENGTH_SHORT);
                return;
            }
            try {
                Log.i("debug--->","get in try");
                head = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageHead.setImageBitmap(head);
            } catch (Exception e) {
                Toast.makeText(this, "Set image fail.", Toast.LENGTH_SHORT);
            }
        }
    }
}
