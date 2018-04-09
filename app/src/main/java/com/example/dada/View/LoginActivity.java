/*
 * LoginActivity
 *
 *
 * April 9, 2018
 *
 * Copyright (c) 2018 Team 12. CMPUT301, University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and condition of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise please contact me.
 */

package com.example.dada.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dada.Controller.UserController;
import com.example.dada.Model.OnAsyncTaskCompleted;
import com.example.dada.Model.User;
import com.example.dada.R;
import com.example.dada.Util.FileIOUtil;

/**
 * Login activity, first act
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameText;

    private RadioButton requesterRadio;
    private RadioButton providerRadio;

    private Button loginButton;
    private Button signUpButton;

    private String roleSel;

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
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.edit_text_login_username);

        requesterRadio = findViewById(R.id.radio_button_login_requester);
        providerRadio = findViewById(R.id.radio_button_login_provider);

        loginButton = findViewById(R.id.button_login);
        assert loginButton != null;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        signUpButton = findViewById(R.id.button_sign_in);
        assert signUpButton != null;
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSignup = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intentSignup);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.radio_button_login_provider:

                if (checked) {
                    providerRadio.setChecked(true);
                    requesterRadio.setChecked(false);
                    // will login as provider
                    roleSel = "P";
                    break;
                }

            case R.id.radio_button_login_requester:
                if (checked) {
                    requesterRadio.setChecked(true);
                    providerRadio.setChecked(false);
                    // will login as requester
                    roleSel = "R";
                    break;
                }
        }
    }

    public void login(){
        Log.i("Method start----->", "ResquesterMainActivity setListView");
        String username = usernameText.getText().toString();

        // TODO check user validity, replace following line with elastic search
        boolean validUsername = !(username.isEmpty() || username.trim().isEmpty());

        if ( !(validUsername) ){
            Toast.makeText(this, "Username entered is incorrect.", Toast.LENGTH_SHORT).show();
        }

        User user = userController.getUser(username);

        if (user == null) {
            Toast.makeText(this, "User does not exist, please signup", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (roleSel.equals("P")) {
                    // intent to ProviderMainActivity
                    Intent intentProviderMain = new Intent(getApplicationContext(), ProviderMainActivity.class);
                    startActivity(intentProviderMain);
                } else if (roleSel.equals("R")) {
                    // intent to RequesterMainActivity
                    Intent intentRequesterMain = new Intent(getApplicationContext(), RequesterMainActivity.class);
                    startActivity(intentRequesterMain);
                }
            } catch (Exception e) {
                openSelRoleDialog();
                e.printStackTrace();
            }

        }
    }

    private void openSelRoleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Please Specify Your Role (Requester/Provider).")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create & Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
