package com.groovy.rfs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.groovy.rfs.authentication.AuthActivity;
import com.groovy.rfs.authentication.CreateAccountActivity;
import com.groovy.rfs.authentication.LoginActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    Button auth_btn,logoutBtn;

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
        auth_btn.setOnClickListener(v -> {
            Intent authIntent = new Intent(getActivity(), AuthActivity.class);
            startActivity(authIntent);
        });
        logoutBtn.setOnClickListener(v -> {
            performLogout();
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEBUG_ACCOUNT", "1. AccountFragment onResume() called.");
        updateUIBasedOnLoginState();
    }

    private void updateUIBasedOnLoginState() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String fullName = prefs.getString("user_fullname", null); // Lấy tên, nếu không có thì là null

        Log.d("DEBUG_ACCOUNT", "2. Checking SharedPreferences. FullName found: " + fullName);
        if (fullName != null && !fullName.isEmpty()) {
            Log.d("DEBUG_ACCOUNT", "3. STATUS: LOGGED IN. Setting GONE/VISIBLE.");
            // Đã đăng nhập
            auth_btn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            username.setText("Xin chào, " + fullName + "!"); // <-- Sử dụng fullName ở đây
        } else {
            Log.d("DEBUG_ACCOUNT", "3. STATUS: LOGGED OUT.");
            // Chưa đăng nhập
            auth_btn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            username.setVisibility(View.GONE);
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
}