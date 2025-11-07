package com.groovy.rfs.authentication;

import static com.groovy.rfs.API.RetrofitUtils.retrofitBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.R;
import com.groovy.rfs.model.SerResBasic;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgetPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView email;
        Button goBtn;
        ImageButton cancelBtn;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.username_signin);
        goBtn = findViewById(R.id.go_btn);
        cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(v -> {
            finish();
        });
        goBtn.setOnClickListener(v -> {
            Toast.makeText(ForgetPassActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
            Retrofit retrofit = retrofitBuilder();
            UserApiService apiService = retrofit.create(UserApiService.class);
            Call<SerResBasic> call = apiService.requestPasswordReset(email.getText().toString());
        goBtn.setEnabled(false);

            call.enqueue(new Callback<SerResBasic>() {
                @Override
                public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                    if (response.isSuccessful() && response.body().getSuccess() == 1){
                        int success = response.body().getSuccess();
                        if (success==1){
                            Toast.makeText(ForgetPassActivity.this, "Đã gửi email", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgetPassActivity.this, ResetPassActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(ForgetPassActivity.this, "Lỗi gửi email", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }

                @Override
                public void onFailure(Call<SerResBasic> call, Throwable t) {
                    Toast.makeText(ForgetPassActivity.this, "Lỗi API", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}