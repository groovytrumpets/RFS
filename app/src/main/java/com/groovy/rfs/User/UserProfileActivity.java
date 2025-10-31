package com.groovy.rfs.User;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView detail_wallpaper, avatar;
    private TextView detail_title, detail_year, tvusername;



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