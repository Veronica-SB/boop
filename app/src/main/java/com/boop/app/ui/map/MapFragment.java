package com.boop.app.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.boop.app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.ChipGroup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapFragment extends Fragment {

    private MapView map;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint userLocation;
    private static final int REQUEST_LOCATION = 100;
    private final OkHttpClient httpClient = new OkHttpClient();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue("boop_app");
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(14.0);
        map.getController().setCenter(new GeoPoint(40.4168, -3.7038));

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            centerMapOnUser();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }

        // Filtros
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_filters);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (userLocation == null) return;
            if (checkedId == R.id.chip_vets) {
                loadPlaces("amenity=veterinary");
            } else if (checkedId == R.id.chip_grooming) {
                loadPlaces("shop=pet_grooming");
            } else if (checkedId == R.id.chip_parks) {
                loadPlaces("leisure=dog_park");
            }
        });
    }

    private void centerMapOnUser() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        map.getController().setCenter(userLocation);
                        // Cargar veterinarios por defecto al abrir el mapa
                        loadPlaces("amenity=veterinary");
                    }
                });
    }

    private void loadPlaces(String osmFilter) {
        if (userLocation == null) return;

        double lat = userLocation.getLatitude();
        double lon = userLocation.getLongitude();
        double radius = 3000; // 3 km

        String query = "[out:json];" +
                "node[" + osmFilter + "]" +
                "(around:" + radius + "," + lat + "," + lon + ");" +
                "out;";

        String url = "https://overpass-api.de/api/interpreter?data=" +
                query.replace(" ", "%20").replace("[", "%5B").replace("]", "%5D")
                        .replace("(", "%28").replace(")", "%29").replace(";", "%3B")
                        .replace("{", "%7B").replace("}", "%7D").replace("=", "%3D")
                        .replace(",", "%2C");

        Request request = new Request.Builder().url(url).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Error de red — no hacemos nada, el mapa sigue visible
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) return;
                String body = response.body().string();

                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    try {
                        map.getOverlays().clear();
                        JSONObject json = new JSONObject(body);
                        JSONArray elements = json.getJSONArray("elements");

                        for (int i = 0; i < elements.length(); i++) {
                            JSONObject element = elements.getJSONObject(i);
                            double eLat = element.getDouble("lat");
                            double eLon = element.getDouble("lon");
                            String name = "Sin nombre";
                            if (element.has("tags")) {
                                JSONObject tags = element.getJSONObject("tags");
                                if (tags.has("name")) name = tags.getString("name");
                            }

                            Marker marker = new Marker(map);
                            marker.setPosition(new GeoPoint(eLat, eLon));
                            marker.setTitle(name);
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            map.getOverlays().add(marker);
                        }
                        map.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            centerMapOnUser();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}