package clc65.hoangluu.duan.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import clc65.hoangluu.duan.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thiết lập RecyclerView để hiển thị kết quả
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        // binding.rvSearchResults.setAdapter(new SearchResultAdapter(new ArrayList<>()));

        // TODO: THÊM LOGIC TEXTWATCHER ĐỂ XỬ LÝ TÌM KIẾM
        // binding.etSearchQuery.addTextChangedListener(new TextWatcher() { ... });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}