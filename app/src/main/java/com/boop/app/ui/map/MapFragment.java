package com.boop.app.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.google.android.material.chip.ChipGroup;
/* 
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
*/

public class MapFragment extends Fragment /* implements OnMapReadyCallback */ {

    // private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init Google Map (Comentado hasta configurar dependencias)
        /*
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        */

        // Filter chips — ChipGroup con selección única
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_filters);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_vets) {
                // TODO: Filtrar veterinarios
            } else if (checkedId == R.id.chip_grooming) {
                // TODO: Filtrar peluquerías
            } else if (checkedId == R.id.chip_parks) {
                // TODO: Filtrar parques
            }
        });
    }

    /*
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng barcelona = new LatLng(41.3851, 2.1734);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 15f));
    }
    */
}
