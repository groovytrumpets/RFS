package com.groovy.rfs.API;

import com.groovy.rfs.model.SerResMovieDetail;
import com.groovy.rfs.model.SerResMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("get_movie.php") // Endpoint lấy danh sách phim
    Call<SerResMovies> getAllMovies(); // Trả về ServerResponseMovies

    @GET("get_movie_detail.php") // Endpoint chi tiết
    Call<SerResMovieDetail> getMovieDetails(@Query("idMovie") int movieId); // Truyền ID qua tham số URL
}
