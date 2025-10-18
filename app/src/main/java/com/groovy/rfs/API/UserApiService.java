package com.groovy.rfs.API;

import com.groovy.rfs.model.SevResUser;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApiService {
    @FormUrlEncoded
    @POST("users.php")
    Call<SevResUser> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email);

    // Dùng để đăng nhập hoặc đăng ký bằng Google
    @FormUrlEncoded
    @POST("google_auth.php") // Trỏ đến file API mới
    Call<SevResUser> loginWithGoogle(@Field("id_token") String idToken);
}
