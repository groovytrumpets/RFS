package com.groovy.rfs.API;

import com.groovy.rfs.model.SerResAvatarUpdate;
import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResFriendStatus;
import com.groovy.rfs.model.SerResNotifications;
import com.groovy.rfs.model.SerResPayment;
import com.groovy.rfs.model.SerResStatus;
import com.groovy.rfs.model.SevResUser;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    //THÊM PHƯƠNG THỨC ĐĂNG NHẬP
    @FormUrlEncoded
    @POST("login.php") // Endpoint mới cho đăng nhập
    Call<SevResUser> loginUser( // Assuming SevResUser can hold token and user data
                                @Field("identifier") String identifier,
                                @Field("password") String password
    );
    @FormUrlEncoded
    @POST("update_avatar_url.php")
    Call<SerResAvatarUpdate> updateAvatarUrl(
            @Header("Authorization") String authToken,
            @Field("avatar_url") String newUrl // Chỉ gửi URL (text)
    );
    @GET("get_user_profile.php")
    Call<SevResUser> getUserProfile(@Query("user_id") int userId);
    @FormUrlEncoded
    @POST("send_friend_request.php")
    Call<SerResBasic> sendFriendRequest(
            @Header("Authorization") String authToken,
            @Field("receiver_id") int receiverId
    );
    @GET("check_friendship_status.php")
    Call<SerResFriendStatus> checkFriendshipStatus(
            @Header("Authorization") String authToken,
            @Query("profile_id") int profileId
    );
    @FormUrlEncoded
    @POST("accept_friend_request.php")
    Call<SerResFriendStatus> acceptFriendRequest(
            @Header("Authorization") String authToken,
            @Field("friendship_id") int friendshipId
    );
    @GET("get_my_notifications.php")
    Call<SerResNotifications> getMyNotifications(
            @Header("Authorization") String authToken
    );
    @FormUrlEncoded
    @POST("decline_friend_request.php")
    Call<SerResBasic> declineFriendRequest(
            @Header("Authorization") String authToken,
            @Field("friendship_id") int friendshipId
    );
    @FormUrlEncoded
    @POST("unfriend_user.php")
    Call<SerResBasic> unfriendUser(
            @Header("Authorization") String authToken,
            @Field("user_to_unfriend_id") int userToUnfriendId
    );
    @FormUrlEncoded
    @POST("create_payment_order.php")
    Call<SerResPayment> createPaymentOrder(
            @Header("Authorization") String authToken,
            @Field("amount") int amount,
            @Field("payment_method") String paymentMethod // "VNPAY"
    );
    @GET("check_payment_status.php")
    Call<SerResStatus> checkPaymentStatus(
            @Header("Authorization") String authToken,
            @Query("order_code") String orderCode
    );
}
