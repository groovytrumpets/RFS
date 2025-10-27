package com.groovy.rfs.User;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.authentication.LoginActivity;
import com.groovy.rfs.model.MovieList;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResMyList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListUserActivity extends AppCompatActivity {
    ListUserAdapter adapter;
    ListView listView;
    ImageButton btn_cancel, btn_add_list;

    List<MovieList> mvList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_add_list = findViewById(R.id.btn_add_list);

        btn_cancel.setOnClickListener(v -> {
            finish();
        });
        btn_add_list.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddListActivity.class);
            startActivity(intent);
        });
        listView = findViewById(R.id.movieList_listview);

        adapter = new ListUserAdapter(this, mvList); 
        listView.setAdapter(adapter);

        fetchDataFromServer();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            MovieList clickedList = mvList.get(position);
            Intent intent = new Intent(ListUserActivity.this, ListMovieActivity.class);
            intent.putExtra("LIST_ID", clickedList.getIdMovie_collections());
            startActivity(intent);
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy danh sách (list) mà người dùng nhấn giữ
                MovieList selectedList = mvList.get(position);

                // Hiển thị hộp thoại xác nhận xóa
                showDeleteConfirmationDialog(selectedList, position);

                return true; // true = báo là đã xử lý long click
            }
        });


    }
    private void showDeleteConfirmationDialog(MovieList listToDelete, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh sách '" + listToDelete.getList_name() + "' không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Nếu người dùng bấm "Xóa" -> gọi API
                    performDeleteList(listToDelete, position);
                })
                .setNegativeButton("Hủy", null) // "Hủy" thì không làm gì cả
                .show();
    }

    private void performDeleteList(MovieList listToDelete, int position) {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* ... (xử lý lỗi token) ... */ return; }

        int listId = listToDelete.getIdMovie_collections();

        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResBasic> call = apiService.deleteList(token, listId);

        // Hiển thị loading (nếu muốn)
        Toast.makeText(this, "Đang xóa...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(ListUserActivity.this, "Đã xóa danh sách", Toast.LENGTH_SHORT).show();

                    // (QUAN TRỌNG) Xóa list khỏi danh sách (List) và cập nhật UI
                    mvList.remove(position);
                    adapter.notifyDataSetChanged(); // Cập nhật lại ListView

                } else {
                    String message = (response.body() != null) ? response.body().getMessage() : "Xóa thất bại";
                    Toast.makeText(ListUserActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(ListUserActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDataFromServer() {
        String token = AuthUtils.getToken(this);
        Log.e("DEBUG_LIST_USER", "Token:"+token+", UName:"+AuthUtils.getUserName(this));
        if (token == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            Intent LoginIntent = new Intent(this, LoginActivity.class);
            startActivity(LoginIntent);
            return;
        }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);

        //call api
        Call<SerResMyList> call = apiService.getMyLists(token);

        //handle result
        call.enqueue(new Callback<SerResMyList>() {
            @Override
            public void onResponse(Call<SerResMyList> call, Response<SerResMyList> response) {
                if (response.isSuccessful()&&response.body()!=null&&response.body().getSuccess()==1){
                    List<MovieList> fetchedList = response.body().getLists();

                    if (fetchedList!=null && !fetchedList.isEmpty()){
                        mvList.clear();
                        mvList.addAll(fetchedList);
                        adapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(ListUserActivity.this, "Bạn chưa có danh sách nào", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(ListUserActivity.this, "Không thể tải danh sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResMyList> call, Throwable t) {
                Toast.makeText(ListUserActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }
}