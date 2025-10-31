package com.groovy.rfs.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.SevResUser;
import com.groovy.rfs.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView detail_wallpaper, avatar;
    private TextView detail_title, detail_year, tvusername;
    private Button follow_btn;
    private int userIdToLoad = -1;


    private ImageButton btn_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //ax
        detail_wallpaper = findViewById(R.id.detail_wallpaper);
        avatar = findViewById(R.id.avatar);
        detail_title = findViewById(R.id.detail_description);
        detail_year = findViewById(R.id.detail_year);
        tvusername = findViewById(R.id.username);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> {finish();});
        loadUserProfile();
        follow_btn = findViewById(R.id.detail_trailer_btn);


        //click on name
        userIdToLoad = getIntent().getIntExtra("USER_ID", -1);
        //Log.d("API_TEST", "userIdToLoad: " + userIdToLoad + "");
        if (userIdToLoad == -1) {
            loadUserProfile();
        } else {
            loadUserProfileFromApi(userIdToLoad);
        }
    }

    private void loadUserProfileFromApi(int userIdToLoad) {
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SevResUser> call = apiService.getUserProfile(userIdToLoad);

        call.enqueue(new Callback<SevResUser>() {
            @Override
            public void onResponse(Call<SevResUser> call, Response<SevResUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    Log.d("API_TEST", "user: " + user.getUsername() + "");

                    // Gán dữ liệu từ API
                    tvusername.setText(user.getUsername());
                    //
                    follow_btn.setVisibility(!AuthUtils.getUserName(UserProfileActivity.this)
                            .equals(user.getUsername())? View.VISIBLE : View.GONE);
                    // Tải Avatar
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(UserProfileActivity.this)
                                .load(user.getAvatar())
                                .circleCrop()
                                .into(avatar);
                    } else {
                        avatar.setImageResource(R.mipmap.ic_user_defaut);
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "Không thể tải hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SevResUser> call, Throwable t) {

            }
        });
    }

    private void loadUserProfile() {
        String avatarUrl = AuthUtils.getUserAvatarUrl(this);
        String username = AuthUtils.getUserName(this);

        tvusername.setText(username != null ? username : "N/A");
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.mipmap.ic_user_defaut)
                    .error(R.mipmap.ic_user_defaut)
                    .circleCrop() // Bo tròn
                    .into(avatar);
        } else {
            // Nếu không có avatar, hiện ảnh mặc định
            avatar.setImageResource(R.mipmap.ic_user_defaut);
        }
    }
}