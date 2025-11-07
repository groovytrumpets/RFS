package com.groovy.rfs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.API.UserApiService;
import com.groovy.rfs.Public.PROActivity;
import com.groovy.rfs.User.FriendsListActivity;
import com.groovy.rfs.User.ListUserActivity;
import com.groovy.rfs.User.ReportListActivity;
import com.groovy.rfs.User.ReviewsUserActivity;
import com.groovy.rfs.User.UserProfileActivity;
import com.groovy.rfs.authentication.AuthActivity;
import com.groovy.rfs.authentication.AuthUtils;
import com.groovy.rfs.model.Review;
import com.groovy.rfs.model.SerResAvatarUpdate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    Button auth_btn,logoutBtn, viewListBtn, myReviewsBtn,PRO_btn, friendListBtn, reportListBtn;
    ImageView avatar;
    TextView username;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Inflate the layout for this fragment
        auth_btn = view.findViewById(R.id.authenBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        username = view.findViewById(R.id.username);
        avatar = view.findViewById(R.id.avatar);
        myReviewsBtn = view.findViewById(R.id.myReviewsbtn);
        PRO_btn = view.findViewById(R.id.PRO);
        friendListBtn = view.findViewById(R.id.myFriends);
        reportListBtn = view.findViewById(R.id.myReports);
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });
        reportListBtn.setOnClickListener(v -> {
            Intent reviewOfUser = new Intent(getActivity(), ReportListActivity.class);
            startActivity(reviewOfUser);
        });
        friendListBtn.setOnClickListener(v -> {
            Intent reviewOfUser = new Intent(getActivity(), FriendsListActivity.class);
            startActivity(reviewOfUser);
        });
        myReviewsBtn.setOnClickListener(v -> {
            Intent reviewOfUser = new Intent(getActivity(), ReviewsUserActivity.class);
            startActivity(reviewOfUser);
        });
        username.setOnClickListener(v -> {
            Intent reviewOfUser = new Intent(getActivity(), UserProfileActivity.class);
            startActivity(reviewOfUser);
        });

        auth_btn.setOnClickListener(v -> {
            Intent authIntent = new Intent(getActivity(), AuthActivity.class);
            startActivity(authIntent);
        });
        logoutBtn.setOnClickListener(v -> {
            performLogout();
        });
        viewListBtn = view.findViewById(R.id.myListbtn);
        viewListBtn.setOnClickListener(v -> {
            Intent authIntent = new Intent(getActivity(), ListUserActivity.class);
            startActivity(authIntent);
        });
        PRO_btn.setOnClickListener(v -> {
            Intent authIntent = new Intent(getActivity(), PROActivity.class);
            startActivity(authIntent);
        });




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEBUG_ACCOUNT", "1. AccountFragment onResume() called.");
        updateUIBasedOnLoginState();

    }

    private void updateUIBasedOnStatus() {
        if (AuthUtils.isLoggedIn(getContext())) {
            // Đã đăng nhập

            // Lấy trạng thái ("active" hay "pro")
            String status = AuthUtils.getUserStatus(getContext());
            String uName = AuthUtils.getUserName(getContext());
            if ("pro".equalsIgnoreCase(status)) {
                // Nếu là PRO
                PRO_btn.setVisibility(View.GONE); // Ẩn nút
                username.setText("Xin chào PRO, " + uName + "! 👑");
            } else {
                // Nếu là user thường ("active")
                PRO_btn.setVisibility(View.VISIBLE); // Hiện nút
            }

        } else {
            // Chưa đăng nhập
            PRO_btn.setVisibility(View.GONE);
        }
    }

    private void updateUIBasedOnLoginState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String fullName = prefs.getString("user_fullname", null); // Lấy tên, nếu không có thì là null
        String status = prefs.getString("user_status", null);

        Log.d("DEBUG_ACCOUNT", "2. Checking SharedPreferences. FullName found: " + fullName);
        if (fullName != null && !fullName.isEmpty()) {
            Log.d("DEBUG_ACCOUNT", "3. STATUS: LOGGED IN. Setting GONE/VISIBLE.");
            Log.d("DEBUG_ACCOUNT", "4. STATUS: ACTIVE. Setting VISIBLE.: "+status);
            // Đã đăng nhập
            auth_btn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            username.setText("Xin chào, " + fullName + "!"); // <-- Sử dụng fullName ở đây
            if ("pro".equalsIgnoreCase(status)) {
                // Nếu là PRO
                PRO_btn.setVisibility(View.GONE); // Ẩn nút
                username.setText("Xin chào SIR, " + fullName + "! 👑");
            } else {
                // Nếu là user thường ("active")

                PRO_btn.setVisibility(View.VISIBLE); // Hiện nút
            }
            updateUIBasedOnStatus();
            String avatarUrl = AuthUtils.getUserAvatarUrl(getContext());
            if (avatarUrl != null && !avatarUrl.isEmpty()){
                Glide.with(this) // Dùng 'this' vì đang ở trong Fragment
                        .load(avatarUrl)
                        .placeholder(R.mipmap.ic_user_defaut) // Ảnh chờ
                        .error(R.mipmap.ic_user_defaut)       // Ảnh lỗi
                        .circleCrop() // Bo tròn nếu muốn
                        .into(avatar);
            }else {
                // Xử lý nếu không có avatar (hiện ảnh mặc định)
                avatar.setImageResource(R.mipmap.ic_user_defaut);
            }
        } else {
            Log.d("DEBUG_ACCOUNT", "3. STATUS: LOGGED OUT.");
            // Chưa đăng nhập
            auth_btn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            username.setVisibility(View.VISIBLE);
            PRO_btn.setVisibility(View.GONE);
            myReviewsBtn.setVisibility(View.GONE);
            viewListBtn.setVisibility(View.GONE);
            friendListBtn.setVisibility(View.GONE);
            reportListBtn.setVisibility(View.GONE);

            username.setOnClickListener(null);
            avatar.setOnClickListener(null);
            PRO_btn.setOnClickListener(null);
            myReviewsBtn.setOnClickListener(null);
            viewListBtn.setOnClickListener(null);
            friendListBtn.setOnClickListener(null);
            reportListBtn.setOnClickListener(null);


            avatar.setImageResource(R.mipmap.ic_user_defaut);
        }
    }
    private void performLogout() {
        // 1. Xóa Thông tin người dùng (SharedPreferences thông thường)
        // File: "user_prefs", Key: "user_fullname"
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user_fullname"); // Xóa tên
        editor.remove("user_email");   // Xóa email (nếu có)
        editor.apply();

        // 2. Xóa Token bảo mật (EncryptedSharedPreferences)
        // File: "my_secure_prefs", Key: "auth_token"
        try {
            MasterKey masterKey = new MasterKey.Builder(requireContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    requireContext(),
                    "my_secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor secureEditor = securePrefs.edit();
            secureEditor.remove("auth_token"); // 🚨 Xóa Token JWT
            secureEditor.apply();
            Log.d("AUTH", "Token cleared successfully!");

        } catch (GeneralSecurityException | IOException e) {
            Log.e("Security", "Error clearing secure prefs", e);
            // Có thể thông báo cho người dùng rằng có lỗi xảy ra
        }

        // 3. Cập nhật lại UI và Chuyển hướng

        // Gọi hàm cập nhật UI để chuyển về trạng thái "Chưa đăng nhập"
        updateUIBasedOnLoginState();

        Toast.makeText(requireContext(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

        // Tùy chọn: Chuyển hướng về màn hình chính hoặc màn hình đăng nhập
        // Intent intent = new Intent(getActivity(), MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Đóng mọi Activity cũ
        // startActivity(intent);
    }
    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        // Hiển thị ảnh mới (preview)
                        Glide.with(this).load(imageUri).circleCrop().into(avatar);

                        // Bắt đầu upload
                        uploadAvatarToServer(imageUri);
                    }
                }
            }
    );

    private void uploadAvatarToServer(Uri imageUri) {
        Toast.makeText(getContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // 3.1. Upload lên Cloudinary thành công -> Lấy URL
                        String secureUrl = (String) resultData.get("secure_url");
                        Log.d("Cloudinary", "Upload thành công: " + secureUrl);

                        // 3.2. Gửi URL này về server PHP của bạn
                        callUpdateAvatarApi(secureUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Upload ảnh thất bại: " + error.getDescription(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) { }
                })
                .dispatch(); // Bắt đầu upload
    }

    private void callUpdateAvatarApi(String imageUrl) {
        String token = AuthUtils.getToken(getContext());
        if (token == null) { /* ... (xử lý lỗi token) ... */ return; }
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        UserApiService apiService = retrofit.create(UserApiService.class);

        Call<SerResAvatarUpdate> call = apiService.updateAvatarUrl(token, imageUrl);

        call.enqueue(new Callback<SerResAvatarUpdate>() {
            @Override
            public void onResponse(Call<SerResAvatarUpdate> call, Response<SerResAvatarUpdate> response) {
                if (response.isSuccessful() && response.body().getSuccess() == 1) {
                    Toast.makeText(getContext(), "Cập nhật avatar thành công!", Toast.LENGTH_SHORT).show();

                    // 5. CẬP NHẬT SharedPreferences
                    String newUrl = response.body().getNew_avatar_url();
                    AuthUtils.updateUserAvatarUrl(getContext(), newUrl);

                    if (getContext() != null) {
                        Glide.with(AccountFragment.this) // Dùng 'this' hoặc 'getContext()'
                                .load(newUrl) // Tải URL mới
                                .circleCrop()
                                .placeholder(R.mipmap.ic_user_defaut)
                                .error(R.mipmap.ic_user_defaut)
                                .into(avatar); // Gán vào ImageView avatar
                    }


                } else {
                    Toast.makeText(getContext(), "Lỗi khi lưu vào CSDL", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<SerResAvatarUpdate> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng khi lưu CSDL", Toast.LENGTH_SHORT).show();
            }
        });
    }
}