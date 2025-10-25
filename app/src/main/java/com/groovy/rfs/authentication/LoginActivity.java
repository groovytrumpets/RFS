package com.groovy.rfs.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge; // Có thể không cần nếu dùng setOnApplyWindowInsetsListener
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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
    Button goButton,signByGG;
    ImageButton cancel_btn;
    Context context = this;
    GoogleSignInClient mGoogleSignInClient;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
            });

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
        cancel_btn = findViewById(R.id.btn_cancel);
        cancel_btn.setOnClickListener(v -> {
            finish();
        });
        identifierInput = findViewById(R.id.username_signin); // Đảm bảo ID đúng
        passwordInput = findViewById(R.id.password_signin);
        goButton = findViewById(R.id.go_btn);
        signByGG = findViewById(R.id.signinWthGG_btn);
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Yêu cầu lấy email của người dùng
                .requestIdToken("232237754159-a5vr1cokpsr0rma3u9pogvv070cftaif.apps.googleusercontent.com") // <-- DÁN WEB CLIENT ID VÀO ĐÂY
                .build();

        // 2. Khởi tạo mGoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signByGG.setOnClickListener(v -> {
            signIn();
        });

        /* // Nếu có nút đăng nhập Google
        signByGG.setOnClickListener(v -> {
            signInGoogle(); // Gọi hàm đăng nhập Google
        });
        */
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        // Dùng ActivityResultLauncher thay cho startActivityForResult đã cũ
        signInLauncher.launch(signInIntent);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Đăng nhập thành công! Lấy ID Token
            String idToken = account.getIdToken();
            Toast.makeText(this, "Lấy ID Token thành công!", Toast.LENGTH_SHORT).show();

            // ✅ GỬI idToken NÀY LÊN SERVER CỦA BẠN ĐỂ XÁC THỰC
            sendTokenToServer(idToken);
            // Chuyển sang MainActivity

        } catch (ApiException e) {
            // Đăng nhập thất bại
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendTokenToServer(String idToken) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://khanhnnhe181337.id.vn/RFS/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SevResUser> call = apiService.loginWithGoogle(idToken);

        call.enqueue(new Callback<SevResUser>() {
            @Override
            public void onResponse(Call<SevResUser> call, Response<SevResUser> response) {
                if (response.isSuccessful()) {
                    SevResUser svrResponse = response.body();
                    Toast.makeText(context, svrResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // ✅ Đăng nhập thành công, bạn có thể chuyển sang màn hình chính ở đây
                    // 💡 Case 1: Thành công HTTP, nhưng LỖI LOGIC SERVER
                    if (svrResponse.getIsSuccess() != 1) {
                        Toast.makeText(context, "Đăng nhập thất bại: " + svrResponse.getMessage(), Toast.LENGTH_LONG).show();
                        // KHÔNG CHUYỂN HƯỚNG
                        return;
                    }
                    if (svrResponse.getIsSuccess() == 1) {
                        String token = svrResponse.getToken();
                        User user = svrResponse.getUser();

                        if (token != null && user != null) {
                            // 1. Lưu Token bảo mật
                            saveToken(token);

                            // 2. Lưu Thông tin User (đã sửa logic ưu tiên username)
                            saveUserInfo(user);

                            // 3. Chuyển sang MainActivity sau khi lưu xong
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            setResult(Activity.RESULT_OK);
                            finish();

                        } else {
                            Log.e("AUTH_GG", "Login successful but Token or User is null!");
                            Toast.makeText(context, "Lỗi: Không nhận được đủ dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Lỗi từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SevResUser> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    Log.d("LOGIN_DEBUG", "Server response received. Success flag: " + svrResponse.getIsSuccess());
                    // Log 1: Check success value

                    if (svrResponse.getIsSuccess() == 1) {
                        String token = svrResponse.getToken();
                        User user = svrResponse.getUser();
                        Log.d("AUTH_SAVE", "User load from api:"+svrResponse.toString());
                        Log.d("LOGIN_DEBUG", "Success flag is 1. Token: " + (token != null ? "exists" : "NULL"));
                        // Log 2: Check if token exists

                        if (token != null) {
                            Log.d("LOGIN_DEBUG", "Token is valid. Saving token and navigating...");
                            // Log 3: Confirm before navigation
                            Log.d("LOGIN_DEBUG","USER_TOKEN:"+token);
                            saveToken(token);
                            saveUserInfo(user);
                            // Chuyển sang MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Log.e("LOGIN_DEBUG", "Success flag is NOT 1! Navigation skipped.");
                            // Log if success flag is wrong
                            Toast.makeText(context, "Lỗi: Không nhận được token", Toast.LENGTH_SHORT).show();
                        }
                    } // Không cần else ở đây vì Toast báo lỗi đã hiển thị ở trên rồi
                } else {
                    // Log lỗi chi tiết hơn
                    Log.e("LOGIN_DEBUG", "Response not successful or body is null. Code: " + response.code());
                    Log.e("API_ERROR", "Login failed! Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(context, "Lỗi đăng nhập từ server (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SevResUser> call, Throwable t) {
                Log.e("LOGIN_DEBUG", "API call failed: " + t.getMessage(), t);
                Log.e("API_ERROR", "Login onFailure: " + t.getMessage(), t);
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(User user) {
        if (user == null){
            Log.e("AUTH_SAVE", "ERROR: User object is NULL. Cannot save info.");
            return;
        }
        String username = user.getUsername();
        if (username == null || username.isEmpty()) {
            Log.w("AUTH_SAVE", "WARNING: FullName is NULL or EMPTY. Saving may fail.");
        }
        // Dùng SharedPreferences thông thường cho dữ liệu không nhạy cảm
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_fullname", user.getUsername()); // Ví dụ lưu tên
        editor.putString("user_email", user.getEmail());     // Ví dụ lưu email
        editor.putInt("user_id", user.getIdUser());           // Ví dụ lưu ID")
        // Thêm các trường khác nếu cần
        editor.apply();
        Log.d("AUTH_SAVE", "User info saved to SharedPreferences. FullName: " + username);
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