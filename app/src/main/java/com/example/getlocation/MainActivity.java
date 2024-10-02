package com.example.getlocation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.LocationServices;
import android.provider.Settings;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.getlocation.databinding.ActivityMainBinding;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
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
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Toast.makeText(MainActivity.this, "Vị trí hiện tại: " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}