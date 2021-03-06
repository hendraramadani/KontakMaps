package com.example.nanta.kontakmap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BuatKontakActivity extends AppCompatActivity {

    protected Cursor cursor;
    DataHelper dbHelper;
    Button bSimpan, bKembali;
    EditText editNomor, editNama, editNRP, editJenisKelamin, editAlamat, editLatitude, editLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_kontak);

        dbHelper = new DataHelper(this);
        editNomor = (EditText) findViewById(R.id.editTextNomor);
        editNama = (EditText) findViewById(R.id.editTextNama);
        editNRP = (EditText) findViewById(R.id.editTextTextNRP);
//        editJenisKelamin = (EditText) findViewById(R.id.editTextJenisKelamin);
        editAlamat = (EditText) findViewById(R.id.editTextTextAlamat);
        editLatitude = (EditText) findViewById(R.id.editTextTextLatitude);
        editLongitude = (EditText) findViewById(R.id.editTextTextLongitude);
        final RadioGroup rbg=(RadioGroup) findViewById(R.id.radioGroup1);

        bSimpan = (Button) findViewById(R.id.buttonSimpan);
        bKembali = (Button) findViewById(R.id.buttonKembali);

        bSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected = rbg.getCheckedRadioButtonId();
                RadioButton gender=(RadioButton) findViewById(selected);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("insert into biodata(nomor, nama, nrp, jenis_kelamin, alamat,latitude,longitude) values('" +
                        editNomor.getText().toString() + "','" +
                        editNama.getText().toString() + "','" +
                        editNRP.getText().toString() + "','" +
                        gender.getText().toString() + "','" +
                        editAlamat.getText().toString() + "','" +
                        editLatitude.getText().toString() + "','" +
                        editLongitude.getText().toString() + "')");
                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                MainActivity.ma.RefreshList();
                finish();
            }
        });

        bKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}