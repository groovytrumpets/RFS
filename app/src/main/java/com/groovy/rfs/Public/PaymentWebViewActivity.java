package com.groovy.rfs.Public;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthActivity;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.SerResStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentWebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private static final String VNPAY_RETURN_SCHEME = "rfs://payment-return";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_web_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        webView = findViewById(R.id.payment_webview);
        progressBar = findViewById(R.id.payment_progress_bar);
        String payUrl = getIntent().getStringExtra("PAY_URL");

        if (payUrl == null || payUrl.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không có URL thanh toán", Toast.LENGTH_SHORT).show();
            finish(); // Đóng nếu không có URL
            return;
        }

        // Cài đặt WebView
        setupWebView(payUrl);
    }

    private void setupWebView(String url) {
        // Bật JavaScript (BẮT BUỘC cho VNPay)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Cài đặt Trình xử lý WebView (WebViewClient)
        webView.setWebViewClient(new WebViewClient() {

            // Bắt đầu tải trang
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE); // Hiện loading

                // --- BƯỚC QUAN TRỌNG NHẤT ---
                // Kiểm tra xem trang có phải là trang "Return" không
                if (url.startsWith(VNPAY_RETURN_SCHEME)) {
                    // Thanh toán đã xử lý xong (chưa biết thành công hay thất bại)
                    // Server VNPay đã gọi API 2 (IPN) của bạn rồi.

                    Toast.makeText(PaymentWebViewActivity.this, "Đang kiểm tra kết quả...", Toast.LENGTH_SHORT).show();
                    Log.d("API_TEST","url:" + url);
                    Uri uri = Uri.parse(url);
                    String responseCode = uri.getQueryParameter("vnp_ResponseCode");


                    if ("00".equals(responseCode)) {
                        // THÀNH CÔNG
                        Log.d("API_TEST","responseCode:" + responseCode);
                        setResult(Activity.RESULT_OK);
                        SharedPreferences prefs = getSharedPreferences("payment_prefs", Context.MODE_PRIVATE);
                        String orderCodeToCheck = prefs.getString("pending_order_code", null);
                        if (orderCodeToCheck != null) {
                            checkPaymentStatus(orderCodeToCheck); // Gọi API 3 (mới)
                        }else {
                            Toast.makeText(PaymentWebViewActivity.this, "Giao dịch đã bị hủy", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(PaymentWebViewActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                        // THẤT BẠI (Hủy, Lỗi)
                        setResult(Activity.RESULT_CANCELED);
                    }
                    finish(); // Đóng WebView
                }
                Log.d("WebViewDebug", "Đang cố gắng tải URL: " + url);
            }

            // Tải xong trang
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE); // Ẩn loading

                Log.d("WebViewDebug", "Đang cố gắng tải URL: " + url);
            }
            private void checkPaymentStatus(String currentOrderCode) {
                String token = AuthUtils.getToken(PaymentWebViewActivity.this);
                if (token == null) { /* ... */ return; }

                Toast.makeText(PaymentWebViewActivity.this, "Đang xác nhận thanh toán...", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(PaymentWebViewActivity.this, "Nâng cấp PRO thành công!", Toast.LENGTH_LONG).show();
                                AuthUtils.updateUserStatus(PaymentWebViewActivity.this, "pro");
                                finish();
                            } else if ("pending".equals(status)) {
                                // (API 2 chưa chạy xong)
                                Toast.makeText(PaymentWebViewActivity.this, "Đang xử lý, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                                // (Bạn có thể gọi lại hàm checkPaymentStatus sau 3 giây)
                            } else {
                                // "failed"
                                Toast.makeText(PaymentWebViewActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PaymentWebViewActivity.this, "Không thể kiểm tra trạng thái", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SerResStatus> call, Throwable t) {
                        Toast.makeText(PaymentWebViewActivity.this, "Lỗi mạng khi kiểm tra", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });



        // Tải URL
        Log.d("WebViewDebug", "Đang cố gắng tải URL: " + url);
        webView.loadUrl(url);
    }
}