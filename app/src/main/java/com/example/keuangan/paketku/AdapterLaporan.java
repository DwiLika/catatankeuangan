package com.example.keuangan.paketku;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keuangan.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterLaporan extends RecyclerView.Adapter<AdapterLaporan.ViewHolder> {

    private List<Transaksiku> listTransaksi = new ArrayList<>();

    public void setListTransaksi(List<Transaksiku> list) {
        listTransaksi.clear();
        listTransaksi.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laporan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLaporan.ViewHolder holder, int position) {
        Transaksiku transaksiku = listTransaksi.get(position);
        holder.teksTipe.setText(transaksiku.getTipe());
        holder.teksJumlah.setText(String.valueOf(transaksiku.getJumlah()));
        holder.teksData.setText(transaksiku.getIsi());
        holder.teksTanggal.setText(transaksiku.getTanggal());
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView teksTipe;
        TextView teksJumlah;
        TextView teksData;
        TextView teksTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            teksTipe = itemView.findViewById(R.id.teks_tipe);
            teksJumlah = itemView.findViewById(R.id.teks_jumlah);
            teksData = itemView.findViewById(R.id.teks_data);
            teksTanggal = itemView.findViewById(R.id.teks_tanggal);
        }
    }
}
