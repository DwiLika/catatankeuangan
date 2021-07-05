package com.example.keuangan.paketku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keuangan.R;

import java.util.List;

public class AdapterTransaksiku extends RecyclerView.Adapter<AdapterTransaksiku.ViewHolder>{

    Context context;
    List<Transaksiku> list;

    OnCallBack onCallBack;

    public void setOnCallBack(OnCallBack onCallBack){
        this.onCallBack = onCallBack;
    }

    public AdapterTransaksiku(Context context, List<Transaksiku> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaksi,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.teksTipe.setText(list.get(position).getTipe());
        holder.teksJumlah.setText(String.valueOf(list.get(position).getJumlah()));
        holder.teksData.setText(list.get(position).getIsi());
        holder.teksTanggal.setText(list.get(position).getTanggal());

        holder.tblEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onTblEdit(list.get(position));
            }
        });

        holder.tblHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onTblHapus(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView teksTipe;
        TextView teksJumlah;
        TextView teksData;
        TextView teksTanggal;
        ImageButton tblHapus, tblEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            teksTipe = itemView.findViewById(R.id.teks_tipe);
            teksJumlah = itemView.findViewById(R.id.teks_jumlah);
            teksData = itemView.findViewById(R.id.teks_data);
            teksTanggal = itemView.findViewById(R.id.teks_tanggal);
            tblHapus = itemView.findViewById(R.id.tbl_hapus);
            tblEdit = itemView.findViewById(R.id.tbl_edit);
        }
    }

    public interface OnCallBack{
        void onTblHapus(Transaksiku transaksiku);
        void onTblEdit(Transaksiku transaksiku);

    }
}
