package com.groovy.rfs.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.Adapter.NotificationsAdapter;
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.Notification;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResFriendStatus;
import com.groovy.rfs.model.SerResNotifications;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationsActivity extends AppCompatActivity implements NotificationsAdapter.OnNotificationActionClickListener {
    ImageView cancel_btn;
    List<Notification> notificationList = new ArrayList<>();
    NotificationsAdapter adapter;
    RecyclerView recyclerView;
    TextView noNotifications;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cancel_btn = findViewById(R.id.btn_cancel);
        cancel_btn.setOnClickListener(v -> {finish();});
        recyclerView = findViewById(R.id.rv_notifications);
        noNotifications = findViewById(R.id.tv_no_notifications);
        loading = findViewById(R.id.pb_loading_notifications);

        adapter = new NotificationsAdapter(this, this, notificationList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        fetchNotifications();
    }

    private void fetchNotifications() {
        loading.setVisibility(View.VISIBLE);
        noNotifications.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(NotificationsActivity.this, "Token loss", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResNotifications> call = apiService.getMyNotifications(token);
        call.enqueue(new Callback<SerResNotifications>() {
            @Override
            public void onResponse(Call<SerResNotifications> call, Response<SerResNotifications> response) {
                loading.setVisibility(View.GONE); // Tắt loading

                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    List<Notification> fetchedList = response.body().getNotifications();
                    if (fetchedList != null && !fetchedList.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        notificationList.clear();
                        notificationList.addAll(fetchedList);
                    //Log.e("API_TEST","Notifications: " + (notificationList != null && !notificationList.isEmpty()));
                        adapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    } else {
                        noNotifications.setVisibility(View.VISIBLE); // Hiện "Không có thông báo"
                    }
                } else {
                    Toast.makeText(NotificationsActivity.this, "Không thể tải thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResNotifications> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(NotificationsActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onAcceptClick(Notification notification, int position) {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(NotificationsActivity.this, "Token loss", Toast.LENGTH_SHORT).show();
            return;
        }
        int friendShipId = notification.getReference_id();

        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResFriendStatus> call = apiService.acceptFriendRequest(token,friendShipId);
        Log.d("API_TEST", "token: " + token + "friendShipId: "+friendShipId);
        call.enqueue(new Callback<SerResFriendStatus>() {
            @Override
            public void onResponse(Call<SerResFriendStatus> call, Response<SerResFriendStatus> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(NotificationsActivity.this, "Đã đồng ý kết bạn!", Toast.LENGTH_SHORT).show();
                    // Xóa thông báo này khỏi list
                    notificationList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, notificationList.size());
                } else {
                    Toast.makeText(NotificationsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResFriendStatus> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeclineClick(Notification notification, int position) {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(NotificationsActivity.this, "Token loss", Toast.LENGTH_SHORT).show();
            return;
        }
        int friendShipId = notification.getReference_id();
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResBasic> call = apiService.declineFriendRequest(token,friendShipId);
        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(NotificationsActivity.this, "Đã từ chối", Toast.LENGTH_SHORT).show();
                    // Xóa thông báo này khỏi list
                    notificationList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, notificationList.size());
                } else {
                    Toast.makeText(NotificationsActivity.this, "Thao tác thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(NotificationsActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onNotificationClick(Notification notification, int position) {
        switch (notification.getType()) {
            case "friend_request":
            case "friend_accept":
                // Mở trang hồ sơ của người GỬI
                Intent userIntent = new Intent(this, UserProfileActivity.class);
                userIntent.putExtra("USER_ID", notification.getUser_idSender());
                startActivity(userIntent);
                break;
            case "new_review":
                // Mở trang chi tiết phim
                Intent movieIntent = new Intent(this, MovieDetailActivity.class);
                movieIntent.putExtra("MOVIE_ID", notification.getReference_id());
                startActivity(movieIntent);
                break;
        }
    }
}