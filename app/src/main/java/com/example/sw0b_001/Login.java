package com.example.sw0b_001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    EditText phonenumber, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void validateInput(View view) {
        phonenumber = findViewById(R.id.user_phonenumber);
        password = findViewById(R.id.user_password);

        System.out.println("[+] Phonenumber:" + phonenumber.getText().toString());
        System.out.println("[+] Password: " + password.getText().toString());

        Intent intent = new Intent(this, Platforms.class);
        startActivity(intent);
    }
}