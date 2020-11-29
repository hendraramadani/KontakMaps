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

public class UpdateKontakActivity extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    Button bSimpan, bKembali;
    EditText editNomor, editNama, editNRP, editJenisKelamin, editAlamat, editLatitude, editLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kontak);

        dbHelper = new DataHelper(this);
        editNomor = (EditText) findViewById(R.id.editTextNomor);
        editNama = (EditText) findViewById(R.id.editTextNama);
        editNRP = (EditText) findViewById(R.id.editTextTextNRP);
//        editJenisKelamin = (EditText) findViewById(R.id.editTextJenisKelamin);
        editAlamat = (EditText) findViewById(R.id.editTextTextAlamat);
        editLatitude = (EditText) findViewById(R.id.updateLatitude);
        editLongitude = (EditText) findViewById(R.id.updateLongitude);
        final RadioGroup rbg=(RadioGroup) findViewById(R.id.radioGroup1);


        bSimpan = (Button) findViewById(R.id.buttonSimpan);
        bKembali = (Button) findViewById(R.id.buttonKembali);



        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM biodata WHERE nama = '" + getIntent().getStringExtra("nama") + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            editNomor.setText(cursor.getString(0).toString());
            editNama.setText(cursor.getString(1).toString());
            editNRP.setText(cursor.getString(2).toString());
//            editJenisKelamin.setText(cursor.getString(3).toString());
//            Toast.makeText(getBaseContext(), cursor.getString(3).toString(), Toast.LENGTH_LONG).show();
            if (cursor.getString(3).toString().equals("Perempuan") ) {
                rbg.check(R.id.radioperempuan);
            } else  rbg.check(R.id.radiolaki);
            editAlamat.setText(cursor.getString(4).toString());
            editLatitude.setText(cursor.getString(5).toString());
            editLongitude.setText(cursor.getString(6).toString());
        }
        bSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected = rbg.getCheckedRadioButtonId();
                RadioButton gender=(RadioButton) findViewById(selected);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE biodata " +
                        "set nama='" + editNama.getText().toString() +
                        "', nrp='" + editNRP.getText().toString() +
                        "', jenis_kelamin='" + gender.getText().toString() +
                        "', latitude='" + editLatitude.getText().toString() +
                        "', longitude='" + editLongitude.getText().toString() +
                        "', alamat='" + editAlamat.getText().toString() +
                        "'WHERE nomor='" + editNomor.getText().toString() + "'");
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