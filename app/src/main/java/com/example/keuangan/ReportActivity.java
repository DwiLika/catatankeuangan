package com.example.keuangan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.keuangan.paketku.AdapterLaporan;
import com.example.keuangan.paketku.AdapterTransaksiku;
import com.example.keuangan.paketku.Transaksiku;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    ChipGroup groupPilihan;
    Spinner spinnerBulan;
    RecyclerView rvLaporan;
    TextView tvTotal;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference myRef = database.getReference("Transaksiku").child(auth.getUid());
    AdapterLaporan adapterLaporan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        groupPilihan = findViewById(R.id.group_pilihan);

        spinnerBulan = findViewById(R.id.spinner_bulan);
        final String[] bulan = {"Semua", "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bulan);
        spinnerBulan.setAdapter(adapter);

        rvLaporan = findViewById(R.id.rv_laporan);
        rvLaporan.setLayoutManager(new LinearLayoutManager(this));

        adapterLaporan = new AdapterLaporan();
        rvLaporan.setAdapter(adapterLaporan);

        tvTotal = findViewById(R.id.tv_total);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Laporan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bacaData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void bacaData() {
        final ArrayList<Transaksiku> allList = new ArrayList<>();
        final ArrayList<Transaksiku> categorizedList = new ArrayList<>();
        final ArrayList<Transaksiku> filteredList = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                allList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaksiku transaksiku = snapshot.getValue(Transaksiku.class);
                    allList.add(transaksiku);
                }

                groupPilihan.check(R.id.chip_masuk);
                spinnerBulan.setSelection(spinnerBulan.getSelectedItemPosition());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        groupPilihan.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                categorizedList.clear();
                int total = 0;

                for (Transaksiku transaksiku : allList) {
                    if (i == R.id.chip_masuk) {
                        if (transaksiku.getTipe().equals("Pemasukan")) {
                            categorizedList.add(transaksiku);
                            total += transaksiku.getJumlah();
                        }
                    }

                    if (i == R.id.chip_keluar) {
                        if (transaksiku.getTipe().equals("Pengeluaran")) {
                            categorizedList.add(transaksiku);
                            total += transaksiku.getJumlah();
                        }
                    }
                }

                filteredList.clear();
                filteredList.addAll(categorizedList);
                adapterLaporan.setListTransaksi(filteredList);
                tvTotal.setText("Total: Rp " + total);

                spinnerBulan.setSelection(0);
            }
        });

        spinnerBulan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filteredList.clear();
                int total = 0;

                if (i == 0) {
                    filteredList.addAll(categorizedList);
                    adapterLaporan.setListTransaksi(filteredList);

                    for (Transaksiku transaksiku : categorizedList) {
                        total += transaksiku.getJumlah();
                    }
                    tvTotal.setText("Total: Rp " + total);

                    return;
                }

                for (Transaksiku transaksiku : categorizedList) {
                    try {
                        Date date = parseDate(transaksiku.getTanggal());
                        int monthNumber = getMonthNumber(date);
                        if (monthNumber == i) {
                            filteredList.add(transaksiku);
                            total += transaksiku.getJumlah();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                adapterLaporan.setListTransaksi(filteredList);
                tvTotal.setText("Total: Rp " + total);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        return dateFormat.parse(dateString);
    }

    private int getMonthNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH) + 1;
    }
}