package com.groovy.rfs.User;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
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
import com.groovy.rfs.Adapter.FriendListAdapter;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResFriendsList;
import com.groovy.rfs.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendsListActivity extends AppCompatActivity implements FriendListAdapter.OnFriendClickListener {
    RecyclerView recycleView;
    ImageButton btn_cancel;
    TextView textView4;
    FriendListAdapter adapter;
    List<User> friendList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friends_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recycleView = findViewById(R.id.rv_public_reviews);
        btn_cancel = findViewById(R.id.btn_cancel);
        textView4 = findViewById(R.id.textView4);
        btn_cancel.setOnClickListener(v -> {
            finish();
        });
        adapter = new FriendListAdapter(this,friendList,this);
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(adapter);
        fetchMyFriends();
    }

    private void fetchMyFriends() {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* ... (xử lý lỗi token) ... */
            Toast.makeText(FriendsListActivity.this, "Login pls", Toast.LENGTH_SHORT).show();
            return; }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResFriendsList> call = apiService.getMyFriends(token);
        call.enqueue(new Callback<SerResFriendsList>() {
            @Override
            public void onResponse(Call<SerResFriendsList> call, Response<SerResFriendsList> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    
                    List<User> fetchedFriends = response.body().getFriends();
                    if (fetchedFriends != null && !fetchedFriends.isEmpty()) {
                        friendList.clear();
                        friendList.addAll(fetchedFriends);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FriendsListActivity.this, "Bạn chưa có bạn bè nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FriendsListActivity.this, "Không thể tải danh sách bạn bè", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResFriendsList> call, Throwable t) {
                Toast.makeText(FriendsListActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onFriendClick(User friend) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("USER_ID", friend.getIdUser()); // Gửi ID của người bạn đó
        startActivity(intent);
    }
}