package com.groovy.rfs.authentication;

import static com.groovy.rfs.API.RetrofitUtils.retrofitBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.R;
import com.groovy.rfs.model.SerResBasic;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ResetPassActivity extends AppCompatActivity {
    EditText email,otp,password;
    Button goBtn;
    ImageButton cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.email_create);
        otp = findViewById(R.id.username_create);
        password = findViewById(R.id.password_create);
        goBtn = findViewById(R.id.join_btn);
        cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(v -> {
            finish();
        });
        goBtn.setOnClickListener(v -> {
            Toast.makeText(ResetPassActivity.this, "Loading...", Toast.LENGTH_SHORT).show();

            String email = this.email.getText().toString();
            String otp = this.otp.getText().toString();
            String password = this.password.getText().toString();
            Retrofit retrofit = retrofitBuilder();
            UserApiService apiService = retrofit.create(UserApiService.class);
            Call<SerResBasic> call = apiService.resetPasswordWithOtp(email,otp,password);
            goBtn.setEnabled(false);

            call.enqueue(new Callback<SerResBasic>() {
                @Override
                public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                    if (response.isSuccessful() && response.body().getSuccess() == 1){

                        int success = response.body().getSuccess();
                        if (success==1){
                            Toast.makeText(ResetPassActivity.this, "Change success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(ResetPassActivity.this, "Change fail", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        String message = response.body().getMessage();
                        Toast.makeText(ResetPassActivity.this, "Change fail: "+message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SerResBasic> call, Throwable t) {
                        Toast.makeText(ResetPassActivity.this, "API error", Toast.LENGTH_SHORT).show();
                }
            });


        });


    }
}