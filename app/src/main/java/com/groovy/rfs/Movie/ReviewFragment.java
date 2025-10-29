package com.groovy.rfs.Movie;

import static com.groovy.rfs.API.RetrofitUtils.retrofitBuilder;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.Adapter.ReviewsAdapter;
import com.groovy.rfs.R;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.Review;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResReviews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewFragment extends Fragment implements ReviewsAdapter.OnReviewInteractionListener{
    private static final String ARG_MOVIE_ID = "movie_id";
    private int movieId;
    //views

    private RecyclerView rvReviews;
    private Button btnSeeAllReviews;
    private RatingBar rbMyRating;
    private EditText etMyComment;
    private Button btnSubmitReview;

    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewsData = new ArrayList<>();
    ImageButton btnDelete;

    public static ReviewFragment newInstance(int movieId) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewFragment newInstance(String param1, String param2) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Ánh xạ Views
        rvReviews = view.findViewById(R.id.rv_top_reviews); // Đảm bảo ID đúng
        btnSeeAllReviews = view.findViewById(R.id.btn_see_all_reviews);
        rbMyRating = view.findViewById(R.id.rb_my_rating);
        etMyComment = view.findViewById(R.id.et_my_comment);
        btnSubmitReview = view.findViewById(R.id.btn_submit_review);
        btnDelete = view.findViewById(R.id.btn_delete_review);

        // Setup RecyclerView
        int userId = AuthUtils.getKeyUserid(getContext());
        //Log.e("API_TEST","userId" + userId);
        reviewsAdapter = new ReviewsAdapter(getContext(), reviewsData,userId,this);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReviews.setAdapter(reviewsAdapter);
        rvReviews.setNestedScrollingEnabled(false); // Quan trọng khi trong ScrollView/ViewPager

        // Gọi API để lấy reviews
        fetchReviews(movieId);

        // Gán sự kiện
        btnSubmitReview.setOnClickListener(v -> submitMyReview());
    }

    private void submitMyReview() {
        float scoreValue = rbMyRating.getRating();
        String comment = etMyComment.getText().toString().trim();
        // Kiểm tra dữ liệu nhập
        if (scoreValue == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }
        // (Có thể bỏ qua kiểm tra comment rỗng nếu bạn cho phép)
        // if (comment.isEmpty()) { ... }

        // Kiểm tra đăng nhập và lấy token
        String token = AuthUtils.getToken(getContext());
        if (token == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để đánh giá", Toast.LENGTH_SHORT).show();
            // (Optional: Chuyển sang màn hình Login)
            return;
        }

        // Vô hiệu hóa nút gửi
        btnSubmitReview.setEnabled(false);
        Toast.makeText(getContext(), "Đang gửi...", Toast.LENGTH_SHORT).show();

        // Gọi API addReview
        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResBasic> call = apiService.addReview(token, movieId, scoreValue, comment);

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                btnSubmitReview.setEnabled(true); // Bật lại nút

                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    Toast.makeText(getContext(), "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    // Xóa input
                    rbMyRating.setRating(0);
                    etMyComment.setText("");
                    // Tải lại danh sách reviews để thấy đánh giá mới
                    fetchReviews(movieId);
                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "Gửi thất bại";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                btnSubmitReview.setEnabled(true);
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReviews(int movieId) {
        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService= retrofit.create(MovieApiService.class);
        Call<SerResReviews> call = apiService.getMovieReviews(movieId, 3);
        Log.e("API_TEST","movie Id" + movieId);
        call.enqueue(new Callback<SerResReviews>() {
            @Override
            public void onResponse(Call<SerResReviews> call, Response<SerResReviews> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    List<Review> fetchedReviews = response.body().getReviews();
                    //Log.e("API_TEST","fetchedReviews" + fetchedReviews.get(0).getComment());
                    if (fetchedReviews != null && !fetchedReviews.isEmpty()) {
                        reviewsData.clear();
                        reviewsData.addAll(fetchedReviews);
                        reviewsAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView

                        // Hiện nút "Xem tất cả"
                        btnSeeAllReviews.setVisibility(View.VISIBLE);
                    } else {
                        // Không có review nào
                        reviewsData.clear(); // Xóa review cũ (nếu có)
                        reviewsAdapter.notifyDataSetChanged();
                        btnSeeAllReviews.setVisibility(View.GONE); // Ẩn nút "Xem tất cả"
                        // (Optional: Hiển thị TextView "Chưa có đánh giá nào")
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResReviews> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng khi tải đánh giá", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteReviewClicked(Review review, int position) {
// Hiển thị dialog xác nhận
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    performDeleteReview(review, position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performDeleteReview(Review review, int position) {
        String token = AuthUtils.getToken(getContext());
        if (token == null) { /* ... xử lý lỗi token ... */ return; }

        // Gọi API deleteReview
        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        // movieId đã có sẵn trong Fragment
        Call<SerResBasic> call = apiService.deleteReview(token, movieId);

        Toast.makeText(getContext(), "Đang xóa...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(getContext(), "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();

                    // Xóa review khỏi danh sách và cập nhật UI
                    reviewsData.remove(position);
                    reviewsAdapter.notifyItemRemoved(position);
                    reviewsAdapter.notifyItemRangeChanged(position, reviewsData.size());

                    // (Optional) Có thể hiện lại phần viết review nếu bị ẩn
                    // showWriteReviewSection();
                } else {
                    Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}