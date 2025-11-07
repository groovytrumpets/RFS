package com.groovy.rfs.User;

import android.os.Bundle;
import android.widget.ImageButton;
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
import com.groovy.rfs.Adapter.ReportListAdapter;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.Reports;
import com.groovy.rfs.model.SerResReports;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReportListActivity extends AppCompatActivity {
    private RecyclerView rvReports;
    private ReportListAdapter adapter;
    private List<Reports> reportsData = new ArrayList<>();
    private ImageButton btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rvReports = findViewById(R.id.rv_public_reviews);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> finish());
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportListAdapter(this, reportsData);
        rvReports.setAdapter(adapter);
        fetchMyReports();
    }

    private void fetchMyReports() {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Login pls", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResReports> call = apiService.getMyReports(token);
        call.enqueue(new Callback<SerResReports>() {
            @Override
            public void onResponse(Call<SerResReports> call, Response<SerResReports> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    List<Reports> fetchedReports = response.body().getReports();
                    if (fetchedReports != null && !fetchedReports.isEmpty()) {
                        reportsData.clear();
                        reportsData.addAll(fetchedReports);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ReportListActivity.this, "Bạn chưa có báo cáo nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportListActivity.this, "Không thể tải báo cáo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResReports> call, Throwable t) {
                Toast.makeText(ReportListActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}