package com.groovy.rfs.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.groovy.rfs.model.SevResUser;
import com.groovy.rfs.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateAccountActivity extends AppCompatActivity {
    EditText email, username, password;
    Button join,signByGG;
    Context context=this;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        //anh xa
        email = findViewById(R.id.email_create);
        username = findViewById(R.id.username_create);
        password = findViewById(R.id.password_create);
        join = findViewById(R.id.join_btn);
        signByGG = findViewById(R.id.signinWthGG_btn);
        join.setOnClickListener(view -> {
            insertUser();
        });

        signByGG.setOnClickListener(v -> {
            signIn();
        });

        // 1. Cấu hình GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // Yêu cầu lấy email của người dùng
                .requestIdToken("232237754159-a5vr1cokpsr0rma3u9pogvv070cftaif.apps.googleusercontent.com") // <-- DÁN WEB CLIENT ID VÀO ĐÂY
                .build();

        // 2. Tạo một GoogleSignInClient với các tùy chọn đã định nghĩa
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void insertUser() {
        //1. create entity storage
        User user = new User();
        //2. set data in to entity storage
        user.setEmail(email.getText().toString());
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        //3. create entity in Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://khanhnnhe181337.id.vn/RFS/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //4. call funct from interface
        //4.0 create entity from interface
        UserApiService userApiService = retrofit.create(UserApiService.class);
        //4.1 repare funct
        Call<SevResUser> call = userApiService.registerUser(
                user.getUsername(),
                user.getPassword(),
                user.getEmail());
        //4.2 execute funct
        call.enqueue(new Callback<SevResUser>() {
            @Override
            public void onResponse(Call<SevResUser> call, Response<SevResUser> response) {
                SevResUser sevResUser = response.body();
                Toast.makeText(context, sevResUser.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SevResUser> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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
            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
            startActivity(intent);
            setResult(Activity.RESULT_OK);
            finish();

        } catch (ApiException e) {
            // Đăng nhập thất bại
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm mới để gọi API
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
                    // Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    // startActivity(intent);
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
}