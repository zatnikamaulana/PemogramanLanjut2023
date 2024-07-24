package com.zet.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerPage extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/resto";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private JTable tableMenuMakanan;
    private JTextField textFieldNamaMakanan;
    private JTextField textFieldJumlahMakanan;
    private JButton buttonOrder;
    private JButton buttonTotalHarga;
    private JButton buttonKembali;
    private JPanel panelCustomer;
    private JTextField textFieldNamaCustomer;
    private DefaultTableModel tableModel;
    private String selectedMakanan = "";

    // New list to keep track of orders
    private final List<OrderItem> orderItems = new ArrayList<>();

    public CustomerPage() {
        setTitle("Customer");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(panelCustomer);

        tableModel = new DefaultTableModel(
                new Object[]{"No", "Nama Makanan", "Harga"},
                0
        );
        tableMenuMakanan.setModel(tableModel);
        loadDataToTable();
        setVisible(true);

        buttonOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderItem();
            }
        });

        this.tableMenuMakanan.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int row = tableMenuMakanan.getSelectedRow();
                        if (row < 0) return;

                        String namaMakanan = tableMenuMakanan.getValueAt(row, 1).toString();
                        if (selectedMakanan.equals(namaMakanan)) return;

                        selectedMakanan = namaMakanan;

                        textFieldNamaMakanan.setText(namaMakanan);
                    }
                }
        );

        buttonTotalHarga.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotalPrice();
            }
        });

        buttonKembali.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginPage();
                dispose();
            }
        });
    }

    private void loadDataToTable() {
        List<ManageMenu> manageMenus = getManageMenu();
        if (manageMenus == null) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (ManageMenu manageMenu : manageMenus) {
            tableModel.addRow(new Object[]{manageMenu.getId(), manageMenu.getNama(), manageMenu.getHarga()});
        }
    }

    private void orderItem() {
        int selectedRow = tableMenuMakanan.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item yang ingin diorder.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String namaMakanan = tableModel.getValueAt(selectedRow, 1).toString();
        String harga = tableModel.getValueAt(selectedRow, 2).toString();
        String jumlahMakanan = textFieldJumlahMakanan.getText();

        if (jumlahMakanan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah makanan.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jumlah = Integer.parseInt(jumlahMakanan);
        double hargaPerItem = Double.parseDouble(harga);
        double totalHarga = jumlah * hargaPerItem;

        // Add the order item to the list
        orderItems.add(new OrderItem(namaMakanan, jumlah, totalHarga));
        JOptionPane.showMessageDialog(this, "Item berhasil diorder.");

        // Add the order to DaftarTransaksi
        DaftarTransaksi daftarTransaksi = DaftarTransaksi.getInstance();
        daftarTransaksi.addOrder(textFieldNamaCustomer.getText(), orderItems);
    }

    private void calculateTotalPrice() {
        double total = 0;

        for (OrderItem item : orderItems) {
            total += item.getTotalHarga();
        }

        JOptionPane.showMessageDialog(this, "Total Harga: " + total);
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

    private static List<ManageMenu> getManageMenu() {
        List<ManageMenu> arrayListManageMenu = new ArrayList<>();
        ResultSet resultSet = executeQuery("SELECT * FROM managemenu");

        if (resultSet == null) {
            return null;
        }

        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nama = resultSet.getString("nama");
                String harga = resultSet.getString("harga");

                ManageMenu manageMenu = new ManageMenu(id, nama, harga);
                arrayListManageMenu.add(manageMenu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return arrayListManageMenu;
    }

    public static class OrderItem {
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
}