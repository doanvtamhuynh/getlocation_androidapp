package com.example.getlocation;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;


import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.getlocation.databinding.ActivityMainBinding;
import com.google.android.gms.location.Priority;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;

    TextView txtLocation;
    Button btnGetLocation, btnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtLocation = findViewById(R.id.txtLocation);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnCheck = findViewById(R.id.btnCheck);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkGPS();
            }
        });

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastKnownLocation();
            }
        });

    }

    private boolean checkGPS(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS chưa bật, hiển thị thông báo

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận") // Tiêu đề của hộp thoại
                    .setMessage("Chưa bật vị trí bạn có muốn bật không?") // Nội dung thông điệp
                    .setCancelable(false) // Không cho phép đóng hộp thoại bằng cách nhấn ra ngoài
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Hành động khi nhấn "Có"
                            dialog.dismiss(); // Đóng hộp thoại
                            enableGPS();
                        }
                    })
                    .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Hành động khi nhấn "Không"
                            dialog.dismiss(); // Đóng hộp thoại
                        }
                    });

            // Hiển thị hộp thoại
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return false;
        } else {
            // GPS đang bật, thực hiện các thao tác với vị trí
            Toast.makeText(this, "GPS is enabled!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void enableGPS(){
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isLocationEnabled) {
            Toast.makeText(MainActivity.this, "Vui lòng bật dịch vụ định vị!", Toast.LENGTH_SHORT).show();
            return; // Thoát khỏi phương thức
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(5000L)
                .setMinUpdateIntervalMillis(2000L)  // Cập nhật vị trí nhanh nhất mỗi 2 giây
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(MainActivity.this, "Không thể lấy vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    String countryName = "";
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            countryName = addresses.get(0).getCountryName();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    txtLocation.setText("Vị trí hiện tại: " + latitude + ", " + longitude + "\n" + "Country: " + countryName);
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
}