package com.groovy.rfs.API;

import com.groovy.rfs.model.SerResBasic;
import com.groovy.rfs.model.SerResMovieDetail;
import com.groovy.rfs.model.SerResMovies;
import com.groovy.rfs.model.SerResMyList;
import com.groovy.rfs.model.SerResPubLists;
import com.groovy.rfs.model.SerResPublicReviews;
import com.groovy.rfs.model.SerResReviews;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("get_movie.php") // Endpoint lấy danh sách phim
    Call<SerResMovies> getAllMovies(); // Trả về ServerResponseMovies

    @GET("get_movie_detail.php") // Endpoint chi tiết
    Call<SerResMovieDetail> getMovieDetails(@Query("idMovie") int movieId); // Truyền ID qua tham số URL

    @GET("get_my_lists.php") // Tên file PHP trên server
    Call<SerResMyList> getMyLists(
            @Header("Authorization") String authToken // Gửi token lên header
    );
    @GET("get_movies_in_list.php") // Tên file PHP mới
    Call<SerResMovies> getMoviesInList( // Tái sử dụng model ResponseMovies
                                          @Header("Authorization") String authToken,
                                          @Query("list_id") int listId // "Túi 1": Gửi ID của list lên URL
    );
    @GET("get_movies_in_public_list.php") // Tên file PHP mới
    Call<SerResMovies> getMoviesInListPublic( // Tái sử dụng model ResponseMovies
                                        @Query("list_id") int listId // "Túi 1": Gửi ID của list lên URL
    );
    @GET("search_movies.php")
    Call<SerResMovies> searchMovies( // Tái sử dụng model ResponseMovies
                                       @Query("query") String searchQuery // Gửi query lên URL (Túi 1)

    );
    @FormUrlEncoded // Báo cho Retrofit biết là gửi bằng Body (giống login)
    @POST("add_list.php") // Dùng POST để tạo mới
    Call<SerResBasic> createList(
            @Header("Authorization") String authToken,
            @Field("list_name") String listName,
            @Field("description") String description
    );
    @FormUrlEncoded
    @POST("add_movie_to_list.php")
    Call<SerResBasic> addMovieToList(
            @Header("Authorization") String token,
            @Field("list_id") int listIdToAdd,
            @Field("movie_id") int movieId);
    @FormUrlEncoded
    @POST("delete_movie_from_list.php") // Dùng POST (hoặc DELETE)
    Call<SerResBasic> deleteMovieFromList(
            @Header("Authorization") String authToken,
            @Field("list_id") int listId,
            @Field("movie_id") int movieId
    );
    @FormUrlEncoded
    @POST("delete_list.php") // Dùng POST (hoặc DELETE)
    Call<SerResBasic> deleteList(
            @Header("Authorization") String authToken,
            @Field("list_id") int listId
    );
    @FormUrlEncoded
    @POST("update_list_visibility.php")
    Call<SerResBasic> updateListVisibility(
            @Header("Authorization") String authToken,
            @Field("list_id") int listId,
            @Field("new_visibility") String newVisibility // "public" hoặc "private"
    );
    @GET("get_all_public_lists.php") // Tên file PHP
    Call<SerResPubLists> getPublicListsSimple();

    @GET("get_movie_reviews.php") // The name of your PHP file
    Call<SerResReviews> getMovieReviews(
            @Query("movie_id") int movieId, // Sends movie_id=... in the URL
            @Query("limit") Integer limit    // Sends limit=... in the URL (use Integer for optional)
    );
    @GET("get_all_public_reviews.php") Call<SerResPublicReviews> getAllPublicReviews();
    @GET("get_my_reviews.php")
    Call<SerResPublicReviews> getMyReviews(
            @Header("Authorization") String authToken
    );
    @FormUrlEncoded
    @POST("add_review.php")
    Call<SerResBasic> addReview(
            @Header("Authorization") String authToken,
            @Field("movie_id") int movieId,
            @Field("score") float score, // Đảm bảo tên là "score"
            @Field("comment") String comment
    );
    @FormUrlEncoded
    @POST("delete_review.php") // Dùng POST
    Call<SerResBasic> deleteReview(
            @Header("Authorization") String authToken,
            @Field("movie_id") int movieId
    );
    @FormUrlEncoded
    @POST("update_review.php")
    Call<SerResBasic> updateReview(
            @Header("Authorization") String authToken,
            @Field("movie_id") int movieId,
            @Field("score") float newScore,
            @Field("comment") String newComment
    );
    @GET("get_friends_reviews.php")
    Call<SerResPublicReviews> getFriendsReviews(
            @Header("Authorization") String authToken // Cần token
    );
    @GET("get_friends_lists.php")
    Call<SerResPubLists> getFriendsLists(
            @Header("Authorization") String authToken // Cần token
    );

}
