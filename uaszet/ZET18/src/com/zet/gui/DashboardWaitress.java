package com.zet.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardWaitress extends JFrame {
    private JPanel panelDashboard;
    private JButton manageMenuButton;
    private JButton daftarTransaksiButton;
    private JButton buttonKembali;

    public DashboardWaitress() {
        setContentPane(panelDashboard);
        setTitle("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,520);
        setLocationRelativeTo(null);
        setVisible(true);

        manageMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageMenu manageMenu = new ManageMenu();
                manageMenu.setVisible(true);
                dispose();
            }
        });

        daftarTransaksiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DaftarTransaksi daftarTransaksi = new DaftarTransaksi();
                daftarTransaksi.setVisible(true);

                dispose();
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
}
