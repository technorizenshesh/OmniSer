package com.technorizen.omniser.commonfragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.technorizen.omniser.R;
import com.technorizen.omniser.activities.CommonCartActivity;
import com.technorizen.omniser.databinding.FragmentProviderServiesBinding;

public class ProviderServiesFragment extends Fragment {

    Context mContext;
    FragmentProviderServiesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_provider_servies, container, false);

        init();

        // Inflate the layout for this fragment
        return binding.getRoot();

    }

    private void init() {

        binding.rlBook.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CommonCartActivity.class));
        });

    }


}