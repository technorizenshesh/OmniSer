package com.technorizen.omniser.commonfragments;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.technorizen.omniser.R;
import com.technorizen.omniser.databinding.FragmentProviderMapBinding;

public class ProviderMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    FragmentProviderMapBinding binding;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_provider_map, container, false);
        
        init();
        
        // Inflate the layout for this fragment
        return binding.getRoot();
        
    }

    private void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


}