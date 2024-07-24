package com.zet.gui;

public class OrderItem {
    private final String namaMakanan;
    private final int jumlah;
    private final double totalHarga;

    public OrderItem(String namaMakanan, int jumlah, double totalHarga) {
        this.namaMakanan = namaMakanan;
        this.jumlah = jumlah;
        this.totalHarga = totalHarga;
    }

    public String getNamaMakanan() {
        return namaMakanan;
    }
    public int getJumlah() {
        return jumlah;
    }

    public double getTotalHarga() {
        return totalHarga;
    }
}
