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

public class ManageMenu extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/resto";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private JButton buttonUpdate;
    private JButton buttonClear;
    private JButton buttonDelete;
    private JTextField textFieldNama;
    private JTextField textFieldHarga;
    private JPanel panelManageMenu;
    private JButton buttonAdd;
    private JButton kembaliButton;
    private JTable jTableManageMenu;
    private DefaultTableModel tableModel;
    private String selectedMakanan = "";

    public ManageMenu() {
        setTitle("Dashboard");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(panelManageMenu);

        // Inisialisasi DefaultTableModel dan mengatur JTable
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama", "Harga"}, 0);
        jTableManageMenu.setModel(tableModel);

        // Mengisi data ke JTable
        loadDataToTable();

        this.jTableManageMenu.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int row = jTableManageMenu.getSelectedRow();
                        if (row < 0) return;

                        String makanan = jTableManageMenu.getValueAt(row, 1).toString();
                        if (selectedMakanan.equals(makanan)) return;

                        selectedMakanan = makanan;
                        String harga = jTableManageMenu.getValueAt(row, 2).toString();

                        textFieldNama.setText(makanan);
                        textFieldHarga.setText(harga);
                    }
                }
        );

        kembaliButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardWaitress();
                dispose();
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRowToTable();
            }
        });

        buttonUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedRow();
            }
        });

        buttonDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedRow();
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
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

    private void addRowToTable() {
        String makanan = textFieldNama.getText();
        String harga = textFieldHarga.getText();

        if (makanan.isEmpty() || harga.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = generateNewId();  // Fungsi untuk menghasilkan ID baru

        tableModel.addRow(new Object[]{id, makanan, harga});
        insertManageMenu(new ManageMenu(id, makanan, harga));
        clearInputFields();
    }

    private void updateSelectedRow() {
        int selectedRow = jTableManageMenu.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diupdate.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String makanan = textFieldNama.getText();
        String harga = textFieldHarga.getText();

        if (makanan.isEmpty() || harga.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);

        tableModel.setValueAt(makanan, selectedRow, 1);
        tableModel.setValueAt(harga, selectedRow, 2);

        updateManageMenu(new ManageMenu(id, makanan, harga));
        clearInputFields();
    }

    private void deleteSelectedRow() {
        int selectedRow = jTableManageMenu.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        tableModel.removeRow(selectedRow);
        deleteManageMenu(id);
    }

    private void clearInputFields() {
        textFieldNama.setText("");
        textFieldHarga.setText("");
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

    private static void insertManageMenu(ManageMenu manageMenu) {
        String sql = "INSERT INTO managemenu (id, nama, harga) VALUES (" + manageMenu.getId() + ", '" + manageMenu.getNama() + "', '" + manageMenu.getHarga() + "')";
        executeSql(sql);
    }

    private static void updateManageMenu(ManageMenu manageMenu) {
        String sql = "UPDATE managemenu SET nama = '" + manageMenu.getNama() + "', harga = '" + manageMenu.getHarga() + "' WHERE id = " + manageMenu.getId();
        executeSql(sql);
    }

    private static void deleteManageMenu(int id) {
        String sql = "DELETE FROM managemenu WHERE id = " + id;
        executeSql(sql);
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

    private int generateNewId() {
        List<ManageMenu> manageMenus = getManageMenu();
        int maxId = 0;
        for (ManageMenu menu : manageMenus) {
            if (menu.getId() > maxId) {
                maxId = menu.getId();
            }
        }
        return maxId + 1;
    }

    private int id;
    private String nama;
    private String harga;

    public ManageMenu(int id, String nama, String harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }
}
