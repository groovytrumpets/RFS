package com.groovy.rfs;

import static com.groovy.rfs.API.RetrofitUtils.retrofitBuilder;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.Adapter.PublicReviewsAdapter;
import com.groovy.rfs.Movie.MovieDetailActivity;
import com.groovy.rfs.Movie.ReviewFragment;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.PublicReview;
import com.groovy.rfs.model.Review;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResPublicReviews;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment implements PublicReviewsAdapter.OnPublicReviewInteractionListener {

    private RecyclerView recyclerView;
    private PublicReviewsAdapter adapter;
    private List<PublicReview> reviewsData = new ArrayList<>();
    private int currentUserId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Lấy User ID hiện tại
        currentUserId = AuthUtils.getKeyUserid(getContext());

        recyclerView = view.findViewById(R.id.rv_public_reviews); // Đảm bảo ID đúng
        adapter = new PublicReviewsAdapter(getContext(),currentUserId, this , reviewsData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Gọi API lấy tất cả reviews
        fetchAllPublicReviews();
    }

    private void fetchAllPublicReviews() {
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResPublicReviews> call = apiService.getAllPublicReviews();

        call.enqueue(new Callback<SerResPublicReviews>() {
            @Override
            public void onResponse(Call<SerResPublicReviews> call, Response<SerResPublicReviews> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    reviewsData.clear();
                    reviewsData.addAll(response.body().getReviews());
                    adapter.notifyDataSetChanged();
                }else {
                    String msg = "GET reviews public thất bại";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResPublicReviews> call, Throwable t) {
                Toast.makeText(getContext(), "reviews public API error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false);
    }

    @Override
    public void onItemClick(PublicReview review) {
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", review.getIdMovie());
        startActivity(intent);
    }

    @Override
    public void onEditClick(PublicReview review, int position) {
        showEditReviewDialog(review, position);
    }
    private void showEditReviewDialog(PublicReview reviewToEdit, int position) {
        // Inflate layout dialog tùy chỉnh (tạo file layout mới)
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_review, null); // Tạo dialog_edit_review.xml

        // Ánh xạ views trong dialog
        RatingBar editRatingBar = dialogView.findViewById(R.id.edit_review_rating);
        EditText editComment = dialogView.findViewById(R.id.edit_review_comment);

        // Điền dữ liệu cũ vào dialog
        editRatingBar.setRating(reviewToEdit.getScore());
        editComment.setText(reviewToEdit.getComment());

        // Tạo AlertDialog
        new AlertDialog.Builder(getContext(),R.style.MyAlertDialogStyle)
                .setView(dialogView) // Đặt layout tùy chỉnh
                .setTitle("Edit review")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Lấy dữ liệu mới từ dialog
                    float newScore = editRatingBar.getRating();
                    String newComment = editComment.getText().toString().trim();

                    // Kiểm tra dữ liệu mới (tương tự hàm submit)
                    if (newScore == 0) {
                        Toast.makeText(getContext(), "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                        return; // Hoặc xử lý khác để giữ dialog mở
                    }

                    // Gọi hàm thực hiện update
                    performUpdateReview(reviewToEdit, position, newScore, newComment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDeleteClick(PublicReview review, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    performDeleteReview(review, position);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void performDeleteReview(PublicReview review, int position) {
        String token = AuthUtils.getToken(getContext());
        if (token == null) { /* ... xử lý lỗi token ... */ return; }

        // Gọi API deleteReview
        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        // movieId đã có sẵn trong Fragment
        Call<SerResBasic> call = apiService.deleteReview(token, review.getIdMovie());

        Toast.makeText(getContext(), "Đang xóa...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(getContext(), "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();

                    // Xóa review khỏi danh sách và cập nhật UI
                    reviewsData.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, reviewsData.size());

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

    private void performUpdateReview(PublicReview originalReview, int position, float newScore, String newComment) {
        String token = AuthUtils.getToken(getContext());
        if (token == null) {
            Toast.makeText(getContext(), "TokenNull!", Toast.LENGTH_SHORT).show();
            return; }

        Retrofit retrofit = retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        // movieId đã có sẵn trong Fragment
        Log.d("API_TEST","movieId: " + originalReview.getIdMovie() + " token: " + token);
        Call<SerResBasic> call = apiService.updateReview(token, originalReview.getIdMovie(), newScore, newComment);

        Toast.makeText(getContext(), "Đang cập nhật...", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<SerResBasic>() {
            @Override
            public void onResponse(Call<SerResBasic> call, Response<SerResBasic> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // (QUAN TRỌNG) Cập nhật dữ liệu trong list và báo cho Adapter
                    originalReview.setScore(newScore); // Giả sử model Review có setter
                    originalReview.setComment(newComment); // Giả sử model Review có setter
                    adapter.notifyItemChanged(position); // Chỉ cập nhật item đã thay đổi

                } else {
                    String msg = (response.body() != null) ? response.body().getMessage() : "Cập nhật thất bại";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResBasic> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}