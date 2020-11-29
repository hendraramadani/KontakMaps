package com.example.nanta.kontakmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LihatKontakActivity extends FragmentActivity implements OnMapReadyCallback {
    protected Cursor cursor;
    DataHelper dbHelper;
    Button bKembali, bJarak;
    TextView editNomor, editNama, editNRP, editJenisKelamin, editAlamat, editLatitude, editLongitude;


    private LocationManager locMan;
    private LocationListener locList;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kontak);


        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locList = new lokasiListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
//            ActivityCompat.requestPermissions(LihatKontakActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},111);
//            ActivityCompat.requestPermissions(LihatKontakActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},112);
//            // here to request the missing permissions, and then overriding
//            public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults);
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getBaseContext(), "GAGAL REQUEST PERMISSION", Toast.LENGTH_LONG).show();
            return;
        }
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locList);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        dbHelper = new DataHelper(this);
        editNomor = (TextView) findViewById(R.id.editTextNomor);
        editNama = (TextView) findViewById(R.id.editTextNama);
        editNRP = (TextView) findViewById(R.id.editTextTextNRP);
        editJenisKelamin = (TextView) findViewById(R.id.editTextJenisKelamin);
        editAlamat = (TextView) findViewById(R.id.editTextTextAlamat);
        editLatitude = (TextView) findViewById(R.id.editTextLatitude);
        editLongitude = (TextView) findViewById(R.id.editTextLongitude);

        bKembali = (Button) findViewById(R.id.buttonKembali);
        bJarak = (Button) findViewById(R.id.btnJarak);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM biodata WHERE nama = '" + getIntent().getStringExtra("nama") + "'", null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            editNomor.setText(cursor.getString(0).toString());
            editNama.setText(cursor.getString(1).toString());
            editNRP.setText(cursor.getString(2).toString());
            editJenisKelamin.setText(cursor.getString(3).toString());
            editAlamat.setText(cursor.getString(4).toString());
            editLatitude.setText(cursor.getString(5).toString());
            editLongitude.setText(cursor.getString(6).toString());
        }


//        goToPeta(latitude, longitude, 20);

        bKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bJarak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude, longitude;
                latitude = editLatitude.getText().toString();
                longitude = editLongitude.getText().toString();
                hitungJarak(latitude, longitude);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Double latitude, longitude;
        latitude = Double.parseDouble(editLatitude.getText().toString());
        longitude = Double.parseDouble(editLongitude.getText().toString());

        mMap = googleMap;

        LatLng mapKontak = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(mapKontak).title("Marker in Contact Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapKontak, 15));
    }

    private void hitungJarak(String latitude, String longitude){
        TextView txtLat, txtLong;
        txtLat = (TextView) findViewById(R.id.txtLat);
        txtLong = (TextView) findViewById(R.id.txtLong);

        Double latAsal, latTujuan, longAsal, longTujuan;
        latAsal = Double.parseDouble(txtLat.getText().toString());
        longAsal = Double.parseDouble(txtLong.getText().toString());

        latTujuan = Double.parseDouble(latitude);
        longTujuan = Double.parseDouble(longitude);

        Location asal = new Location("asal");
        Location tujuan = new Location("tujuan");

        asal.setLatitude(latAsal);
        asal.setLongitude(longAsal);

        tujuan.setLatitude(latTujuan);
        tujuan.setLongitude(longTujuan);

//       Menghitung jarak
        Float jarak = (float) asal.distanceTo(tujuan)/1000;
        String jaraknya =String.valueOf(jarak);

        Toast.makeText(getBaseContext(),"Jarak Lokasi HP sekarang dengan Alamat Kontak adalah " + jaraknya + " km.", Toast.LENGTH_LONG).show();
    }

//    private void goToPeta(Double latitude, Double longitude, Integer zoom) {
//        LatLng lokasiBaru = new LatLng(latitude, longitude);
//        mMap.addMarker(new MarkerOptions().position(lokasiBaru).title("Marker in Latitude: " + latitude + " Longitude: " + longitude ));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiBaru, zoom));
//    }



    private class lokasiListener implements LocationListener {
        TextView txtLat, txtLong;

        @Override
        public void onLocationChanged(@NonNull Location location) {
            txtLat = (TextView) findViewById(R.id.txtLat);
            txtLong = (TextView) findViewById(R.id.txtLong);

            txtLat.setText(String.valueOf(location.getLatitude()));
            txtLong.setText(String.valueOf(location.getLongitude()));
            Log.d("LATITUDE", String.valueOf(txtLat));
            Log.d("LONGITUDE", String.valueOf(txtLong));
            Toast.makeText(getBaseContext(), "GPS Capture", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    }

}