package com.example.keuangan.paketku;

public class Transaksiku {
    String kunci;
    String tipe;
    String isi;
    int jumlah;
    String tanggal;

    public Transaksiku(){

    }

    public Transaksiku(String kunci, String tipe, String isi, int jumlah, String tanggal) {
        this.kunci = kunci;
        this.tipe = tipe;
        this.isi = isi;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getKunci() {
        return kunci;
    }

    public void setKunci(String kunci) {
        this.kunci = kunci;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
