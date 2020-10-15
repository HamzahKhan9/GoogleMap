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
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

   /* LatLng sydney = new LatLng(-34,151);
    LatLng TamWorth = new LatLng(-31.083332,150.916672);
    LatLng NewCastle = new LatLng(-32.916668,151.750000);
    LatLng Brisbane = new LatLng(-27,153.021072);
    LatLng Dubbo = new LatLng(-32.256943,148.601105);
*/
    OkHttpClient client = new OkHttpClient();
    String url = "https://myscrap.com/api/msDiscoverPage";

    SupportMapFragment mapFragment;
    SearchView searchView;
   // FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /*arrayList.add(sydney);
        arrayList.add(TamWorth);
        arrayList.add(NewCastle);
        arrayList.add(Brisbane);
        arrayList.add(Dubbo);
*/

        searchView = findViewById(R.id.sv_location);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

               String location = searchView.getQuery().toString();

                List<Address> addressList = null;

                if (location != null || !location.equals(""))
                {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                             addressList = geocoder.getFromLocationName(location,1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (Address address : addressList) {
                        LatLng  latLng = new LatLng(address.getLatitude(),address.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title(location));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                       // map.addMarker(new MarkerOptions().position(latLng).title(location));
                    }

//                    Address address = addressList.get(0);
                    int listSize = addressList.size();
                    if (listSize > 0) {
                        Address lastAddress = addressList.get(listSize - 1);

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastAddress.getLatitude(),lastAddress.getLongitude()),10));

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // mTextViewResult = findViewById(R.id.text_view_result);

                RequestBody formBody = new FormBody.Builder()
                        .add("searchText", newText)
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
//                            arrayList.add(myResponse);
                                    //  mTextViewResult.setText(myResponse);
                                }
                            });
                        }
                    }
                });

                return false;
            }
        });

        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        for (int i = 0 ; i< arrayList.size(); i++)
        {
            map.addMarker(new MarkerOptions().position(arrayList.get(i)).title("Marker"));
            map.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
            map.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));

        }
    }
    public void readJson (String json_string)
    {
//        String json_string = null;

//            InputStream inputStream = getAssets().open("myResponse");
//            int size = inputStream.available();
//            byte[] buffer = new byte[size];
//            inputStream.read(buffer);
//            inputStream.close();
//
//            json_string = new String(buffer,"UTF-8");

//            Toast.makeText(getApplicationContext(),json_string, Toast.LENGTH_SHORT).show();

            Gson gson = new Gson();
            Post post = gson.fromJson(json_string,Post.class);

            for (LocationData data : post.locationData){
             arrayList.add(new LatLng(data.latitude,data.longitude));
            }


            //Toast.makeText(getApplicationContext(), post.father_details"", Toast.LENGTH_SHORT).show();

        }

    }