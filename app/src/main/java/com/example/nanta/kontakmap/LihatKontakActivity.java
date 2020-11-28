package com.example.nanta.kontakmap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LihatKontakActivity extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    Button bKembali;
    TextView editNomor, editNama, editNRP, editJenisKelamin, editAlamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kontak);

        dbHelper = new DataHelper(this);
        editNomor = (TextView) findViewById(R.id.editTextNomor);
        editNama = (TextView) findViewById(R.id.editTextNama);
        editNRP = (TextView) findViewById(R.id.editTextTextNRP);
        editJenisKelamin = (TextView) findViewById(R.id.editTextJenisKelamin);
        editAlamat = (TextView) findViewById(R.id.editTextTextAlamat);

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
        }
        bKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}