package com.groovy.rfs.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {
    //private static final String BASED_URL ="https://khanhnnhe181337.id.vn/RFS/";
    private static final String BASED_URL ="http://192.168.4.102/RFS/";
 //private static final String BASED_URL ="http://10.33.68.59/RFS/";
    //10.33.68.59


    public static Retrofit retrofitBuilder(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASED_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
