package com.example.keuangan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keuangan.paketku.AdapterTransaksiku;
import com.example.keuangan.paketku.Transaksiku;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton tbhData;
    RecyclerView recyclerView;
    TextView tvSaldo;
    Button btnLaporan;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference myRef = database.getReference("Transaksiku").child(auth.getUid());
    List<Transaksiku> list = new ArrayList<>();
    AdapterTransaksiku adapterTransaksiku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbhData = findViewById(R.id.tbl_data);
        recyclerView = findViewById(R.id.resikel_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvSaldo = findViewById(R.id.uang_dompet);

        btnLaporan = findViewById(R.id.button_laporan);
        btnLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
            }
        });

        tbhData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTambahData();
            }
        });

        bacaData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bacaData() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaksiku value = snapshot.getValue(Transaksiku.class);
                    list.add(value);
//
                }
                adapterTransaksiku = new AdapterTransaksiku(MainActivity.this,list);
                recyclerView.setAdapter(adapterTransaksiku);
//                recyclerView.setAdapter(new AdapterTransaksiku(MainActivity.this,list));

                int totalMasuk = 0;
                int totalKeluar = 0;
                int saldo = 0;
                for (Transaksiku item : list) {
                    if (item.getTipe().equals("Pemasukan")) {
                        totalMasuk += item.getJumlah();
                    }
                    if (item.getTipe().equals("Pengeluaran")) {
                        totalKeluar += item.getJumlah();
                    }
                }
                saldo = totalMasuk - totalKeluar;

                tvSaldo.setText("Rp " + saldo);
                setClick();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

    }

    private void setClick() {
        adapterTransaksiku.setOnCallBack(new AdapterTransaksiku.OnCallBack() {
            @Override
            public void onTblHapus(Transaksiku transaksiku) {
                hapusTransaksi(transaksiku);
            }

            @Override
            public void onTblEdit(Transaksiku transaksiku) {
                showDialogEditData(transaksiku);
            }
        });
    }

    private void showDialogEditData(final Transaksiku transaksiku){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_tambah);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton tblKeluar = dialog.findViewById(R.id.tbl_keluar);
        tblKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Spinner spinnerTipe = dialog.findViewById(R.id.spinner_tipe);
        final String[] listTipe = {"Pengeluaran", "Pemasukan"};
        final ArrayAdapter<String> adapterTipe = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTipe);
        spinnerTipe.setAdapter(adapterTipe);

        spinnerTipe.setSelection(adapterTipe.getPosition(transaksiku.getTipe()));

        final EditText txtTambah = dialog.findViewById(R.id.txt_tambah);
        final EditText txtJumlah = dialog.findViewById(R.id.txt_jumlah);
        final Button tblTambah = dialog.findViewById(R.id.tbl_tambah);
        TextView tlTambah = dialog.findViewById(R.id.tl_tambah);

        txtTambah.setText(transaksiku.getIsi());
        txtJumlah.setText(String.valueOf(transaksiku.getJumlah()));
        tblTambah.setText("Update");
        tlTambah.setText("Edit Data");

        tblTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtTambah.getText())) {
                    tblTambah.setError("Silahkan Isi Saldo");
                } else {
                    transaksiku.setTipe(String.valueOf(spinnerTipe.getSelectedItem()));
                    transaksiku.setIsi(txtTambah.getText().toString());
                    transaksiku.setJumlah(Integer.parseInt(txtJumlah.getText().toString()));
                    transaksiku.setTanggal(getCurrentDate());

                    editTransaksi(transaksiku);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void editTransaksi(Transaksiku transaksiku){
        myRef.child(transaksiku.getKunci()).setValue(transaksiku).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext(),"Update Berhasil",Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void hapusTransaksi(final Transaksiku transaksiku){
        myRef.child(transaksiku.getKunci()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Toast.makeText(getApplication(), transaksiku.getIsi()+"telah dihapus",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogTambahData() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_tambah);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);

        ImageButton tblKeluar = dialog.findViewById(R.id.tbl_keluar);
        tblKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        final Spinner spinnerTipe = dialog.findViewById(R.id.spinner_tipe);
        final String[] listTipe = {"Pengeluaran", "Pemasukan"};
        final ArrayAdapter<String> adapterTipe = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTipe);
        spinnerTipe.setAdapter(adapterTipe);

        final EditText txtTambah = dialog.findViewById(R.id.txt_tambah);
        final EditText txtJumlah = dialog.findViewById(R.id.txt_jumlah);
        final Button tblTambah = dialog.findViewById(R.id.tbl_tambah);


        tblTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtTambah.getText())) {
                    tblTambah.setError("Silahkan Isi Saldo");
                } else {
                    simpanData(String.valueOf(spinnerTipe.getSelectedItem()), txtTambah.getText().toString(), Integer.parseInt(txtJumlah.getText().toString()));
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return dateFormat.format(calendar.getTime());
    }

    private void simpanData(String tipe, String isi, int jumlah) {

        String kunci = myRef.push().getKey();
        Transaksiku transaksiku = new Transaksiku(kunci, tipe, isi, jumlah, getCurrentDate());

        myRef.child(kunci).setValue(transaksiku).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext(),"Berhasil",Toast.LENGTH_SHORT).show();

            }
        });
    }
}