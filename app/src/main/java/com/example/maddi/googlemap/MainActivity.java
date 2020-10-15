package com.example.maddi.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.renderscript.Element;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
    ArrayList<String> locationLabels = new ArrayList<String>();


    OkHttpClient client = new OkHttpClient();
    String url = "https://myscrap.com/api/msDiscoverPage";

    SupportMapFragment mapFragment;
    SearchView searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return fetchData(query);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return fetchData(newText);
            }
        });

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        fetchData("Ohad");
    }

    public void  readJson  (String json_string)
    {

            Gson gson = new Gson();
            Post post = gson.fromJson(json_string,Post.class);

            coordList = new ArrayList<LatLng>();
            locationLabels = new ArrayList<String>();

            for (LocationData data : post.locationData){
                coordList.add(new LatLng(data.latitude, data.longitude));
                locationLabels.add(data.name);
            }

    }

    public void  showMarkers() {
        map.clear();
        for (int i = 0; i < coordList.size(); i++) {
            map.addMarker(new MarkerOptions().position(coordList.get(i)).title(locationLabels.get(i)));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordList.get(i), 12.0f));
        }
    }

    public boolean fetchData(String query) {
        RequestBody formBody = new FormBody.Builder()
                .add("searchText", query)
                .add("apiKey", "501edc9e")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            readJson(myResponse);
                            showMarkers();
                        }
                    });
                }
            }
        });

        return false;
    }

}