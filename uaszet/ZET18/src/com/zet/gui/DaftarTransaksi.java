package com.zet.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DaftarTransaksi extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/resto";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private JPanel panelDaftarTransaksi;
    private JButton kembaliButton;
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;

    // Singleton instance
    private static DaftarTransaksi instance;

    DaftarTransaksi() {
        setTitle("Daftar Transaksi");
        setSize(1000, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(panelDaftarTransaksi);
        setVisible(true);

        tableModel = new DefaultTableModel(
                new Object[]{"No", "Name Customer", "Waktu", "Total Harga"},
                0
        );
        tableTransaksi.setModel(tableModel);
        loadDataToTable();

        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardWaitress();
                dispose();
            }
        });
    }

    // Singleton pattern to get the instance of DaftarTransaksi
    public static DaftarTransaksi getInstance() {
        if (instance == null) {
            instance = new DaftarTransaksi();
        }
        return instance;
    }

    // Method to add new order to the table
    public void addOrder(String customerName, List<CustomerPage.OrderItem> orderItems) {
        double totalHarga = 0;
        for (CustomerPage.OrderItem item : orderItems) {
            totalHarga += item.getTotalHarga();
        }

        String waktu = java.time.LocalDateTime.now().toString();
        tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, customerName, waktu, totalHarga});

        // Insert the new transaction into the database
        insertTransaksi(customerName, waktu, totalHarga);
    }

    private void loadDataToTable() {
        List<DaftarTransaksiEntry> transaksiList = getDaftarTransaksi();
        if (transaksiList == null) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (DaftarTransaksiEntry transaksi : transaksiList) {
            tableModel.addRow(new Object[]{transaksi.getId(), transaksi.getNamaCustomer(), transaksi.getWaktuTransaksi(), transaksi.getHargaTotal()});
        }
    }

    private static void executeSql(String sql) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResultSet executeQuery(String query) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void insertTransaksi(String namaCustomer, String waktuTransaksi, double hargaTotal) {
        String sql = "INSERT INTO transaksi (nama_customer, waktu_transaksi, harga_total) VALUES ('" + namaCustomer + "', '" + waktuTransaksi + "', " + hargaTotal + ")";
        executeSql(sql);
    }

    private static List<DaftarTransaksiEntry> getDaftarTransaksi() {
        List<DaftarTransaksiEntry> transaksiList = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM transaksi");

        if (resultSet == null) {
            return null;
        }

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String namaCustomer = resultSet.getString("nama_customer");
                String waktuTransaksi = resultSet.getString("waktu_transaksi");
                double hargaTotal = resultSet.getDouble("harga_total");

                DaftarTransaksiEntry transaksi = new DaftarTransaksiEntry(id, namaCustomer, waktuTransaksi, hargaTotal);
                transaksiList.add(transaksi);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return transaksiList;
    }

    // Class to represent a transaction entry
    private static class DaftarTransaksiEntry {
        private final int id;
        private final String namaCustomer;
        private final String waktuTransaksi;
        private final double hargaTotal;

        public DaftarTransaksiEntry(int id, String namaCustomer, String waktuTransaksi, double hargaTotal) {
            this.id = id;
            this.namaCustomer = namaCustomer;
            this.waktuTransaksi = waktuTransaksi;
            this.hargaTotal = hargaTotal;
        }

        public int getId() {
            return id;
        }

        public String getNamaCustomer() {
            return namaCustomer;
        }

        public String getWaktuTransaksi() {
            return waktuTransaksi;
        }

        public double getHargaTotal() {
            return hargaTotal;
        }
    }
}