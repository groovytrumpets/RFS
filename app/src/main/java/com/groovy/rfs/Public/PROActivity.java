package com.groovy.rfs.Public;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResPayment;
import com.groovy.rfs.model.SerResStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PROActivity extends AppCompatActivity {
    RadioGroup radioGroup;
    Button btnPay;

    private ActivityResultLauncher<Intent> paymentLauncher;
    private String currentOrderCode; // Biến lưu mã đơn hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_proactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        radioGroup = findViewById(R.id.rg_payment_methods);
        btnPay = findViewById(R.id.btn_proceed_to_payment);

        paymentLauncher = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Quay lại sau khi thanh toán, BẮT ĐẦU KIỂM TRA
                        Toast.makeText(PROActivity.this, "Sucess", Toast.LENGTH_SHORT).show();
                        // Lấy orderCode (có thể dùng biến currentOrderCode
                        // hoặc đọc lại từ SharedPreferences)
                        SharedPreferences prefs = getSharedPreferences("payment_prefs", Context.MODE_PRIVATE);
                        String orderCodeToCheck = prefs.getString("pending_order_code", null);

                        if (orderCodeToCheck != null) {
                            checkPaymentStatus(orderCodeToCheck); // Gọi API 3 (mới)
                        }
                    } else {
                        Toast.makeText(this, "Giao dịch đã bị hủy", Toast.LENGTH_SHORT).show();
                    }

                });

        btnPay.setOnClickListener(v -> {
            String selectedMethod;
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.rb_vnpay) {
                selectedMethod = "VNPAY";
            } else if (selectedId == R.id.rb_momo) {
                selectedMethod = "MOMO";
            } else {
                Toast.makeText(this, "Vui lòng chọn phương thức", Toast.LENGTH_SHORT).show();
                return;
            }
            callCreateOrderAPI(selectedMethod);
        });
    }

    private void checkPaymentStatus(String currentOrderCode) {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* ... */ return; }

        Toast.makeText(this, "Đang xác nhận thanh toán...", Toast.LENGTH_SHORT).show();
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResStatus> call = apiService.checkPaymentStatus(token, currentOrderCode);

        call.enqueue(new Callback<SerResStatus>() {
            @Override
            public void onResponse(Call<SerResStatus> call, Response<SerResStatus> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    String status = response.body().getStatus();

                    if ("completed".equals(status)) {
                        // THÀNH CÔNG (API 2 đã chạy)
                        Toast.makeText(PROActivity.this, "Nâng cấp PRO thành công!", Toast.LENGTH_LONG).show();
                        // (Optional: Cập nhật SharedPreferences)
                        finish();
                    } else if ("pending".equals(status)) {
                        // (API 2 chưa chạy xong)
                        Toast.makeText(PROActivity.this, "Đang xử lý, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                        // (Bạn có thể gọi lại hàm checkPaymentStatus sau 3 giây)
                    } else {
                        // "failed"
                        Toast.makeText(PROActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PROActivity.this, "Không thể kiểm tra trạng thái", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResStatus> call, Throwable t) {
                Toast.makeText(PROActivity.this, "Lỗi mạng khi kiểm tra", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callCreateOrderAPI(String selectedMethod) {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* Lỗi */ return; }
        int amount = 50000; // Số tiền

        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);
        Call<SerResPayment> call = apiService.createPaymentOrder(token, amount, selectedMethod);

        call.enqueue(new Callback<SerResPayment>() {
            @Override
            public void onResponse(Call<SerResPayment> call, Response<SerResPayment> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    String payUrl = response.body().getPayUrl();
                    String orderCode = response.body().getOrder_code();

                    SharedPreferences prefs = getSharedPreferences("payment_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("pending_order_code", orderCode);
                    editor.apply();

                    // Mở WebView
                    Intent intent = new Intent(PROActivity.this, PaymentWebViewActivity.class);
                    intent.putExtra("PAY_URL", payUrl);
                    startActivity(intent); // (Sẽ dùng ActivityResultLauncher để kiểm tra sau)
                    finish();
                } else {
                    Toast.makeText(PROActivity.this, "Không thể tạo đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResPayment> call, Throwable t) {
                Toast.makeText(PROActivity.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}