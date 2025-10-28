package com.groovy.rfs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.groovy.rfs.API.MovieApiService;
import com.groovy.rfs.API.RetrofitUtils;
import com.groovy.rfs.Adapter.PublicListAdapter;
import com.groovy.rfs.Public.ListMoviePublicActivity;
import com.groovy.rfs.model.PublicList;
import com.groovy.rfs.model.SerResPubLists;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListsFragment extends Fragment implements PublicListAdapter.OnPublicListClickListener{
    private RecyclerView recyclerView;
    private PublicListAdapter adapter; // Đổi adapter
    private List<PublicList> publicListsData = new ArrayList<>(); // Đổi kiểu dữ liệu

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListsFragment newInstance(String param1, String param2) {
        ListsFragment fragment = new ListsFragment();
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
        return inflater.inflate(R.layout.fragment_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.lits_public_rview);
        adapter = new PublicListAdapter(getContext(), this, publicListsData); // Dùng adapter mới
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        fetchPublicLists(); // Gọi hàm API mới
    }

    private void fetchPublicLists() {
        Retrofit retrofit = RetrofitUtils.retrofitBuilder();
        MovieApiService apiService = retrofit.create(MovieApiService.class);
        Call<SerResPubLists> call = apiService.getPublicListsSimple();
        call.enqueue(new Callback<SerResPubLists>() {
            @Override
            public void onResponse(Call<SerResPubLists> call, Response<SerResPubLists> response) {
                // (Optional: Ẩn ProgressBar loading)
                // progressBar.setVisibility(View.GONE);
                // recyclerView.setVisibility(View.VISIBLE);

                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    List<PublicList> fetchedLists = response.body().getLists();
                    if (fetchedLists != null && !fetchedLists.isEmpty()) {
                        // Cập nhật dữ liệu cho Adapter
                        publicListsData.clear();
                        publicListsData.addAll(fetchedLists);
                        adapter.notifyDataSetChanged(); // Báo RecyclerView vẽ lại
                    } else {
                        // Không có list nào
                        Toast.makeText(getContext(), "Không có danh sách công khai nào.", Toast.LENGTH_SHORT).show();
                        publicListsData.clear(); // Xóa dữ liệu cũ (nếu có)
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // Lỗi từ server (API trả về success = 0 hoặc lỗi HTTP khác)
                    Toast.makeText(getContext(), "Không thể tải danh sách.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SerResPubLists> call, Throwable t) {
                // (Optional: Ẩn ProgressBar loading)
                // progressBar.setVisibility(View.GONE);

                // Lỗi mạng hoặc lỗi parsing JSON
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onListClick(PublicList list) {
        Intent intent = new Intent(getActivity(), ListMoviePublicActivity.class);
        intent.putExtra("LIST_ID", list.getIdMovie_collections());
        intent.putExtra("LIST_NAME", list.getList_name());
        startActivity(intent);
    }
}