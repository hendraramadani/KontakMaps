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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class LihatKontakActivity extends AppCompatActivity  {
    protected Cursor cursor;
    DataHelper dbHelper;
    Button bKembali;
    TextView editNomor, editNama, editNRP, editJenisKelamin, editAlamat, editLatitude, editLongitude;


    private LocationManager locMan;
    private LocationListener locList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kontak);


        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locList = new lokasiListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getBaseContext(), "GAGAL REQUEST PERMISSION", Toast.LENGTH_LONG).show();
            return;
        }
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locList);

        dbHelper = new DataHelper(this);
        editNomor = (TextView) findViewById(R.id.editTextNomor);
        editNama = (TextView) findViewById(R.id.editTextNama);
        editNRP = (TextView) findViewById(R.id.editTextTextNRP);
        editJenisKelamin = (TextView) findViewById(R.id.editTextJenisKelamin);
        editAlamat = (TextView) findViewById(R.id.editTextTextAlamat);
        editLatitude = (TextView) findViewById(R.id.editTextLatitude);
        editLongitude = (TextView) findViewById(R.id.editTextLongitude);

        bKembali = (Button) findViewById(R.id.buttonKembali);

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
        bKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

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