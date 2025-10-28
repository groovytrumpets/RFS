package com.groovy.rfs.User;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResMovies;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddListActivity extends AppCompatActivity {
    ImageButton btnCancel, btnSave, btnAddMovies;
    EditText etListName, etDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 1. Ánh xạ Views
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        btnAddMovies = findViewById(R.id.btn_add_movie);
        etListName = findViewById(R.id.editListName);
        etDescription = findViewById(R.id.editListDescription);

        // 2. Gán sự kiện

        // Nút Cancel
        btnCancel.setOnClickListener(v -> {
            finish(); // Đóng Activity
        });

        // Nút Save (Dấu tick V)
        btnSave.setOnClickListener(v -> {
            performSaveList();
        });
    }

    private void performSaveList() {
        String listName = etListName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (listName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên danh sách", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy token
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vô hiệu hóa nút để tránh bấm nhiều lần
        btnSave.setEnabled(false);
        Toast.makeText(this, "Đang lưu...", Toast.LENGTH_SHORT).show();

        // Gọi API
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResBasic> call = apiService.createList(token, listName, description);

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                // Bật lại nút
                btnSave.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    Toast.makeText(AddListActivity.this, "Tạo thành công!", Toast.LENGTH_SHORT).show();

                    // (Quan trọng) Báo cho ListUserActivity biết là dữ liệu đã đổi
                    setResult(RESULT_OK); // Đặt cờ kết quả

                    Intent intent = new Intent(AddListActivity.this, ListUserActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    String message = (response.body() != null) ? response.body().getMessage() : "API ERROR";
                    Toast.makeText(AddListActivity.this, "Create fail: " + message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                // Bật lại nút
                btnSave.setEnabled(true);
                Toast.makeText(AddListActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}