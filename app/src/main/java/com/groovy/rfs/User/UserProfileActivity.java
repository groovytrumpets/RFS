package com.groovy.rfs.User;

import android.app.AlertDialog;
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
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResFriendStatus;
import com.groovy.rfs.model.SevResUser;
import com.groovy.rfs.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView detail_wallpaper, avatar;
    private TextView friendCount, createYear, tvusername, reviewCount;
    private Button addfriend_btn;
    private int userIdToLoad = -1;
    private int myUserId = -1;
    private int friendship_id =-1;


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
        createYear = findViewById(R.id.detail_year);
        friendCount = findViewById(R.id.detail_director);
        reviewCount = findViewById(R.id.detail_rating_avg);
        //ax
        detail_wallpaper = findViewById(R.id.detail_wallpaper);
        avatar = findViewById(R.id.avatar);
        tvusername = findViewById(R.id.username);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> {finish();});
        loadUserProfile();

        myUserId = AuthUtils.getKeyUserid(this);
        //click on name
        userIdToLoad = getIntent().getIntExtra("USER_ID", -1);
//        Log.d("API_TEST", "myUserId: " + myUserId + "");
//        Log.d("API_TEST", "userIdToLoad: " + userIdToLoad + "");
        if (userIdToLoad == myUserId) {
            loadUserProfileFromApi(myUserId);
        } else {
            loadUserProfileFromApi(userIdToLoad);
            checkStatus(userIdToLoad);
        }
        addfriend_btn = findViewById(R.id.detail_trailer_btn);
        addfriend_btn.setOnClickListener(v -> {
            sendRequest(userIdToLoad);

        });

    }

    private void checkStatus(int userIdToLoad) {
        String token = AuthUtils.getToken(this);
        if (token == null) return;
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResFriendStatus> call = apiService.checkFriendshipStatus(token, userIdToLoad);
//        Log.d("API_TEST", "token: " + token + "userIdToLoad: "+userIdToLoad);

        call.enqueue(new Callback<SerResFriendStatus>() {
            @Override
            public void onResponse(Call<SerResFriendStatus> call, Response<SerResFriendStatus> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    String status = response.body().getStatus();
                    String initiator = response.body().getInitiator();
                    friendship_id = response.body().getFriendship_id();
                    Log.d("API_TEST", "friendShipId: "+friendship_id+" userIdToLoad: " + userIdToLoad+ "; token:"+token);
//                    Log.d("API_TEST", "status: " + status + "");
//                    Log.d("API_TEST", "initiator: " + initiator + "");

                    updateFriendButton(status, initiator);
                } else {
                    updateFriendButton("none", null);
                }
            }

            @Override
            public void onFailure(Call<SerResFriendStatus> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFriendButton(String status, String initiator) {
        switch (status) {
            case "pending":
                if ("me".equals(initiator)) {
                    addfriend_btn.setText("Request sent!");
                    addfriend_btn.setBackgroundColor(getResources().getColor(R.color.yellow));
                    addfriend_btn.setEnabled(true);
                } else {
                    addfriend_btn.setText("Accept request?");
                    addfriend_btn.setBackgroundColor(getResources().getColor(R.color.yellow));
                    addfriend_btn.setEnabled(true);
                    addfriend_btn.setOnClickListener(v -> {

                    onAcceptClick(userIdToLoad);
                    });
                }
                break;
            case "accepted":
                addfriend_btn.setText("Unfriend");
                addfriend_btn.setBackgroundColor(getResources().getColor(R.color.red));
                addfriend_btn.setTextColor(getResources().getColor(R.color.white));
                addfriend_btn.setEnabled(true);
                addfriend_btn.setOnClickListener(v -> {

                showUnfriendConfirmationDialog(userIdToLoad);
                });
                break;
            case "none":
            default:
                addfriend_btn.setText("Add friend");
                addfriend_btn.setEnabled(true);
                break;
        }
    }

    private void onAcceptClick(int userIdToLoad) {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(UserProfileActivity.this, "Token loss", Toast.LENGTH_SHORT).show();
            return;
        }
        int friendShipId = friendship_id;

        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResFriendStatus> call = apiService.acceptFriendRequest(token,friendShipId);
        Log.d("API_TEST", "token: " + token + "friendShipId: "+friendShipId);

        call.enqueue(new Callback<SerResFriendStatus>() {
            @Override
            public void onResponse(Call<SerResFriendStatus> call, Response<SerResFriendStatus> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(UserProfileActivity.this, "Đã đồng ý kết bạn!", Toast.LENGTH_SHORT).show();
                    // update ui
                    updateFriendButton("accepted", null);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResFriendStatus> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUnfriendConfirmationDialog(int userIdToLoad) {
        new AlertDialog.Builder(this)
                .setTitle("Hủy kết bạn")
                .setMessage("Bạn có chắc muốn hủy kết bạn với người này?")
                .setPositiveButton("Hủy kết bạn", (dialog, which) -> {
                    performUnfriend(userIdToLoad);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performUnfriend(int userIdToLoad) {
        String token = AuthUtils.getToken(this);
        if (token == null) { Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show(); return; }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResBasic> call = apiService.unfriendUser(token, userIdToLoad);
        addfriend_btn.setEnabled(false);
        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(UserProfileActivity.this, "Đã hủy kết bạn", Toast.LENGTH_SHORT).show();
                    // Cập nhật nút về trạng thái "Thêm bạn"
                    updateFriendButton("none", null);
                } else {
                    Toast.makeText(UserProfileActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                    addfriend_btn.setEnabled(true); // Cho phép thử lại
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                addfriend_btn.setEnabled(true); // Cho phép thử lại
                Toast.makeText(UserProfileActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest(int userIdToLoad) {
        String token = AuthUtils.getToken(this);
        if (token == null) { Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show(); return; }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResBasic> call = apiService.sendFriendRequest(token, userIdToLoad);

        addfriend_btn.setEnabled(false);
        Toast.makeText(this, "Đang gửi lời mời...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(UserProfileActivity.this, "Đã gửi lời mời", Toast.LENGTH_SHORT).show();
                    // Cập nhật giao diện nút
                    updateFriendButton("pending", "me");
                    // (Bạn không cần setEnabled(false) vì nó đã bị vô hiệu hóa)
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "Gửi thất bại";
                    Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                    addfriend_btn.setEnabled(true); // Cho phép thử lại
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                addfriend_btn.setEnabled(true); // Cho phép thử lại
                Toast.makeText(UserProfileActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
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
                    //Log.d("API_TEST", "user: " + user.getUsername() + "");

                    // Gán dữ liệu từ API
                    tvusername.setText(user.getUsername());
                    //
                    addfriend_btn.setVisibility(View.VISIBLE);
                    createYear.setText("Join since "+user.getJoin_year());
                    friendCount.setText(user.getFriend_count() + " Friends");
                    reviewCount.setText(user.getRating_count() + " Reviews");

                    //addfriend_btn.setVisibility(!AuthUtils.getUserName(UserProfileActivity.this)
                    //        .equals(user.getUsername())? View.VISIBLE : View.GONE);
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