package com.groovy.rfs.User;

import static com.groovy.rfs.API.RetrofitUtils.retrofitBuilder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.Adapter.PublicReviewsAdapter;
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.PublicReview;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResPublicReviews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReviewsUserActivity extends AppCompatActivity implements PublicReviewsAdapter.OnPublicReviewInteractionListener {
    ImageButton cancel_btn;
    private RecyclerView recyclerView;
    private PublicReviewsAdapter adapter;
    private List<PublicReview> reviewsData = new ArrayList<>();
    private int currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reviews_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cancel_btn = findViewById(R.id.btn_cancel);
        cancel_btn.setOnClickListener(v -> {
            finish();
        });

        currentUserId = AuthUtils.getKeyUserid(this);

        recyclerView = findViewById(R.id.rv_public_reviews); // Đảm bảo ID đúng
        adapter = new PublicReviewsAdapter(this,currentUserId, this , reviewsData);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Gọi API lấy tất cả reviews
        fetchAllPublicReviews();

    }

    private void fetchAllPublicReviews() {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResPublicReviews> call = apiService.getMyReviews(token);

        call.enqueue(new Callback<SerResPublicReviews>() {
            @Override
            public void onResponse(Call<SerResPublicReviews> call, Response<SerResPublicReviews> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    reviewsData.clear();
                    reviewsData.addAll(response.body().getReviews());
                    adapter.notifyDataSetChanged();
                }else {
                    String msg = "GET reviews public thất bại";
                    Toast.makeText(ReviewsUserActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResPublicReviews> call, Throwable t) {
                Toast.makeText(ReviewsUserActivity.this, "reviews public API error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(PublicReview review) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", review.getIdMovie());
        startActivity(intent);
    }

    @Override
    public void onEditClick(PublicReview review, int position) {
        showEditReviewDialog(review, position);
    }

    private void showEditReviewDialog(PublicReview review, int position) {
        // Inflate layout dialog tùy chỉnh (tạo file layout mới)
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_review, null); // Tạo dialog_edit_review.xml

        // Ánh xạ views trong dialog
        RatingBar editRatingBar = dialogView.findViewById(R.id.edit_review_rating);
        EditText editComment = dialogView.findViewById(R.id.edit_review_comment);

        // Điền dữ liệu cũ vào dialog
        editRatingBar.setRating(review.getScore());
        editComment.setText(review.getComment());

        // Tạo AlertDialog
        new AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                .setView(dialogView) // Đặt layout tùy chỉnh
                .setTitle("Edit review")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Lấy dữ liệu mới từ dialog
                    float newScore = editRatingBar.getRating();
                    String newComment = editComment.getText().toString().trim();

                    // Kiểm tra dữ liệu mới (tương tự hàm submit)
                    if (newScore == 0) {
                        Toast.makeText(ReviewsUserActivity.this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                        return; // Hoặc xử lý khác để giữ dialog mở
                    }

                    // Gọi hàm thực hiện update
                    performUpdateReview(review, position, newScore, newComment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performUpdateReview(PublicReview originalReview, int position, float newScore, String newComment) {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "TokenNull!", Toast.LENGTH_SHORT).show();
            return; }

        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        // movieId đã có sẵn trong Fragment
        Log.d("API_TEST","movieId: " + originalReview.getIdMovie() + " token: " + token);
        Call<SerResBasic> call = apiService.updateReview(token, originalReview.getIdMovie(), newScore, newComment);

        Toast.makeText(this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(ReviewsUserActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // (QUAN TRỌNG) Cập nhật dữ liệu trong list và báo cho Adapter
                    originalReview.setScore(newScore); // Giả sử model Review có setter
                    originalReview.setComment(newComment); // Giả sử model Review có setter
                    adapter.notifyItemChanged(position); // Chỉ cập nhật item đã thay đổi

                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "Cập nhật thất bại";
                    Toast.makeText(ReviewsUserActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(ReviewsUserActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(PublicReview review, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    performDeleteReview(review, position);
                })
                .setNegativeButton("Hủy", null)
                .show();

    }

    @Override
    public void onReportClick(PublicReview review, int position) {
        final String[] reasons = {"Spam", "Hate Speech", "Inappropriate", "Other"};
        final String[] reasonKeys = {"spam", "hate_speech", "inappropriate", "other"};

        new AlertDialog.Builder(this)
                .setTitle("Báo cáo đánh giá")
                .setItems(reasons, (dialog, which) -> {
                    // 'which' là vị trí (0, 1, 2, 3)
                    String selectedReason = reasonKeys[which];
                    // Gọi hàm thực hiện báo cáo
                    performReportReview(review.getIdRating(), selectedReason);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performReportReview(int idRating, String selectedReason) {
        String token = AuthUtils.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để báo cáo", Toast.LENGTH_SHORT).show();
            return;
        }
        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResBasic> call = apiService.reportReview(token, idRating, selectedReason);

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ReviewsUserActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReviewsUserActivity.this, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(ReviewsUserActivity.this, "API error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onUsernameClick(PublicReview review) {
        int userId = review.getUser_idUser();
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    private void performDeleteReview(PublicReview review, int position) {
        String token = AuthUtils.getToken(this);
        if (token == null) { /* ... xử lý lỗi token ... */ return; }

        // Gọi API deleteReview
        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        // movieId đã có sẵn trong Fragment
        Call<SerResBasic> call = apiService.deleteReview(token, review.getIdMovie());

        Toast.makeText(this, "Đang xóa...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(ReviewsUserActivity.this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();

                    // Xóa review khỏi danh sách và cập nhật UI
                    reviewsData.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, reviewsData.size());

                    // (Optional) Có thể hiện lại phần viết review nếu bị ẩn
                    // showWriteReviewSection();
                } else {
                    Toast.makeText(ReviewsUserActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(ReviewsUserActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}