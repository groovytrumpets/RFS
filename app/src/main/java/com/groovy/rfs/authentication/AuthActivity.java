package com.groovy.rfs.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.R;

public class AuthActivity extends AppCompatActivity {
    Button createAcc_btn;
    Button signIn_btn;
    Button tour_btn;
    ImageButton cancel_btn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createAcc_btn = findViewById(R.id.createAcc_btn);
        signIn_btn = findViewById(R.id.signIn_btn);
        tour_btn = findViewById(R.id.tour_btn);
        cancel_btn = findViewById(R.id.btn_cancel);
        cancel_btn.setOnClickListener(v -> {
            finish();
        });

        createAcc_btn.setOnClickListener(v -> {
            Intent createAccIntent = new Intent(AuthActivity.this, CreateAccountActivity.class);
            startActivity(createAccIntent);
        });
        signIn_btn.setOnClickListener(v -> {
            Intent signinAccIntent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(signinAccIntent);
        });
    }
}