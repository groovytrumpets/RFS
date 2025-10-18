package com.groovy.rfs.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge; // Có thể không cần nếu dùng setOnApplyWindowInsetsListener
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.MainActivity;
import com.groovy.rfs.R;
// Bỏ import không dùng: import com.groovy.rfs.databinding.ActivityMainBinding;
import com.groovy.rfs.model.SevResUser;
import com.groovy.rfs.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
// Bỏ import không dùng: import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity {
    EditText identifierInput; // Đổi tên để rõ ràng hơn (nhận cả username/email)
    EditText passwordInput;
    Button goButton;
    // Button signByGG; // Nếu có nút đăng nhập Google
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this); // Không cần thiết nếu đã dùng setOnApplyWindowInsetsListener
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Ánh xạ
        identifierInput = findViewById(R.id.username_signin); // Đảm bảo ID đúng
        passwordInput = findViewById(R.id.password_signin);
        goButton = findViewById(R.id.go_btn);
        // signByGG = findViewById(R.id.signinWthGG_btn);

        goButton.setOnClickListener(v -> {
            // Lấy dữ liệu dạng String
            String identifier = identifierInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim(); // Sửa lại tên biến

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return; // Dừng lại nếu thiếu thông tin
            }

            // Gọi hàm đăng nhập với dữ liệu String
            loginUser(identifier, password);
        });

        /* // Nếu có nút đăng nhập Google
        signByGG.setOnClickListener(v -> {
            signInGoogle(); // Gọi hàm đăng nhập Google
        });
        */
    }


    private void loginUser(String identifier, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://khanhnnhe181337.id.vn/RFS/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SevResUser> call = apiService.loginUser(identifier, password);

        call.enqueue(new Callback<SevResUser>() {
            @Override
            public void onResponse(Call<SevResUser> call, Response<SevResUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SevResUser svrResponse = response.body();
                    Toast.makeText(context, svrResponse.getMessage(), Toast.LENGTH_LONG).show();


                    if (svrResponse.getIsSuccess() == 1) {
                        String token = svrResponse.getToken();
                        User user = svrResponse.getUser();

                        if (token != null) {
                            saveToken(token);
                            // Chuyển sang MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("AUTH", "Login successful but token is null!");
                            Toast.makeText(context, "Lỗi: Không nhận được token", Toast.LENGTH_SHORT).show();
                        }
                    } // Không cần else ở đây vì Toast báo lỗi đã hiển thị ở trên rồi
                } else {
                    // Log lỗi chi tiết hơn
                    Log.e("API_ERROR", "Login failed! Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(context, "Lỗi đăng nhập từ server (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SevResUser> call, Throwable t) {
                Log.e("API_ERROR", "Login onFailure: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm lưu token (đã sửa cho API 21+)
    private void saveToken(String token) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "my_secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("auth_token", token);
            editor.apply();
            Log.d("AUTH", "Token saved successfully!");

        } catch (GeneralSecurityException | IOException e) {
            Log.e("Security", "Error managing encrypted preferences", e);
            Toast.makeText(context, "Lỗi bảo mật khi lưu token", Toast.LENGTH_SHORT).show();
        }
    }

    // ... (Các hàm signInGoogle(), handleSignInResult() nếu có) ...
}