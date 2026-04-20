package com.boop.app.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        // Init Google Map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Filter chips — maqueta (sin lógica de base de datos)
        TextView chipVet = view.findViewById(R.id.chip_vet);
        TextView chipGroomer = view.findViewById(R.id.chip_groomer);
        TextView chipPark = view.findViewById(R.id.chip_park);

        chipVet.setOnClickListener(v -> setActiveChip(view, chipVet, chipGroomer, chipPark));
        chipGroomer.setOnClickListener(v -> setActiveChip(view, chipGroomer, chipVet, chipPark));
        chipPark.setOnClickListener(v -> setActiveChip(view, chipPark, chipVet, chipGroomer));
    }

    private void setActiveChip(View root, TextView active, TextView... others) {
        active.setBackgroundResource(R.drawable.bg_chip_active);
        active.setTextColor(requireContext().getColor(android.R.color.white));
        for (TextView other : others) {
            other.setBackgroundResource(R.drawable.bg_chip_inactive);
            other.setTextColor(requireContext().getColor(R.color.text_secondary_light));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Centrar en Barcelona por defecto
        LatLng barcelona = new LatLng(41.3851, 2.1734);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 15f));
        // TODO: Cargar marcadores desde base de datos
    }
}
