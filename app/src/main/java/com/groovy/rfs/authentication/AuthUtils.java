package com.groovy.rfs.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AuthUtils {
    private static final String SECURE_PREF_NAME = "my_secure_prefs";
    private static final String REGULAR_PREF_NAME = "user_prefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_NAME = "user_fullname";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_USER_AVATAR = "user_avatar";

    /**
     * DÙNG TRONG: ApiClient (Interceptor)
     * Lấy token bảo mật để xác thực API
     */
    public static String getToken(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            return prefs.getString(KEY_AUTH_TOKEN, null);
        } catch (Exception e) {
            Log.e("AuthUtils", "Error getting token", e);
            return null;
        }
    }
    /**
     * Hàm này được gọi sau khi upload thành công để lưu URL mới
     */
    public static void updateUserAvatarUrl(Context context, String newUrl) {
        SharedPreferences prefs = context.getSharedPreferences(REGULAR_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_AVATAR, newUrl);
        editor.apply();
    }

    /**
     * DÙNG TRONG: ProfileFragment
     * Lấy tên người dùng để hiển thị "Xin chào..."
     */
    public static String getUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(REGULAR_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_NAME, null); // null là mặc định
    }

    /**
     * DÙNG TRONG: SplashActivity, ProfileFragment
     * Kiểm tra xem đã đăng nhập chưa
     */
    public static boolean isLoggedIn(Context context) {
        // Chỉ cần kiểm tra có token là biết đã đăng nhập
        return getToken(context) != null;
    }

    /**
     * DÙNG TRONG: ProfileFragment (Nút Đăng xuất)
     * Xóa sạch token và thông tin user
     */
    public static void logout(Context context) {
        // 1. Xóa token bảo mật
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(KEY_AUTH_TOKEN);
            editor.apply();
        } catch (Exception e) {
            Log.e("AuthUtils", "Error clearing secure prefs", e);
        }

        // 2. Xóa thông tin user
        SharedPreferences userPrefs = context.getSharedPreferences(REGULAR_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPrefs.edit();
        userEditor.clear();
        userEditor.apply();

        // 3. Quay về màn hình Login
        Intent intent = new Intent(context, LoginActivity.class);
        // Cờ này xóa hết các Activity cũ, đảm bảo user không "back" lại được
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    // -----------------------------------------------------------------
    // HÀM HỖ TRỢ (Copy từ LoginActivity)
    // -----------------------------------------------------------------

    private static SharedPreferences getEncryptedPrefs(Context context)
            throws GeneralSecurityException, IOException {

        MasterKey masterKey = new MasterKey.Builder(context.getApplicationContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context.getApplicationContext(),
                SECURE_PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }
    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(REGULAR_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_EMAIL, "Không có email"); // "Không có email" là mặc định
    }
    public static int getKeyUserid(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(REGULAR_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_USERID, -1);
    }
    public static String getUserAvatarUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(REGULAR_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER_AVATAR, null); // Trả về null nếu không có
    }
}
