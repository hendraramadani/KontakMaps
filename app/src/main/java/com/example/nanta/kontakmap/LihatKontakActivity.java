package com.example.nanta.kontakmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LihatKontakActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int KODE_PERMISSION = 111;
    protected Cursor cursor;
    DataHelper dbHelper;
    Button bKembali, bJarak, bTampil;
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
            ActivityCompat.requestPermissions(LihatKontakActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, KODE_PERMISSION);
//            // here to request the missing permissions, and then overriding
//            public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults);
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Toast.makeText(getBaseContext(), "GAGAL REQUEST PERMISSION", Toast.LENGTH_LONG).show();
            return;
        }
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000000, 5, locList);
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
        bTampil = (Button) findViewById(R.id.btnTampil);

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
        bJarak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude, longitude;
                latitude = editLatitude.getText().toString();
                longitude = editLongitude.getText().toString();
                hitungJarak(latitude, longitude);
            }
        });

        bTampil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Double latitude, longitude;
                latitude = Double.parseDouble(editLatitude.getText().toString());
                longitude = Double.parseDouble(editLongitude.getText().toString());

                LatLng mapKontak = new LatLng(latitude, longitude);

                TextView txtLat, txtLong;
                txtLat = (TextView) findViewById(R.id.txtLat);
                txtLong = (TextView) findViewById(R.id.txtLong);

                // Jika TextView GPS sudah ada valuenya
                if(!txtLat.getText().toString().matches("") && !txtLong.getText().toString().matches("")){
                    Double latGPS, longGPS;
                    latGPS = Double.parseDouble(txtLat.getText().toString());
                    longGPS = Double.parseDouble(txtLong.getText().toString());

                    LatLng mapGPS = new LatLng(latGPS, longGPS);

                    MarkerOptions markerGPS = new MarkerOptions();

//            Create Marker1
                    markerGPS.position(mapGPS);

                    mMap.addMarker(new MarkerOptions().position(mapGPS).title("Marker in GPS Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapGPS, 15));

//                    INI NGEBUG, GARIS MASIH TIDAK SESUAI JALAN
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    Log.d("MAIN_ACTIVITY", String.valueOf(mapKontak));
//                    Log.d("MAIN_ACTIVITY", String.valueOf(mapGPS));
//                    polylineOptions.add(mapKontak);
//                    polylineOptions.add(mapGPS);
//                    polylineOptions.width(15);
//                    polylineOptions.color(Color.BLUE);
//                    polylineOptions.geodesic(true);
//
//                    mMap.addPolyline(polylineOptions);

//            Execute direction between 2 marker
                    String url = getRequestUrl(mapKontak, mapGPS);
                    TaskRequestDirections taskDirections = new TaskRequestDirections();
                    taskDirections.execute(url);
                }
                else {
                    Toast.makeText(getBaseContext(),"Latitude Longitude GPS belum ada", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case KODE_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
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

    private String getRequestUrl(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void hitungJarak(String latitude, String longitude){
        TextView txtLat, txtLong;
        txtLat = (TextView) findViewById(R.id.txtLat);
        txtLong = (TextView) findViewById(R.id.txtLong);

        if(!txtLat.getText().toString().matches("") && !txtLong.getText().toString().matches("")){
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
        else {
            Toast.makeText(getBaseContext(),"Latitude Longitude GPS belum ada", Toast.LENGTH_LONG).show();
        }
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