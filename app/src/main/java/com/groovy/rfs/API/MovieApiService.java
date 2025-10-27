package com.groovy.rfs.API;

import com.groovy.rfs.model.SerResMovieDetail;
import com.groovy.rfs.model.SerResMovies;
import com.groovy.rfs.model.SerResMyList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    @GET("search_movies.php")
    Call<SerResMovies> searchMovies( // Tái sử dụng model ResponseMovies
                                       @Query("query") String searchQuery // Gửi query lên URL (Túi 1)
    );
}
