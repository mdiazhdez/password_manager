package com.morsaprogramando.secret_manager.ui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TrayManager {

    private TrayIcon trayIcon;
    private final SystemTray tray = SystemTray.getSystemTray();
    private final JFrame frame;

    public TrayManager(JFrame frame) {
        this.frame = frame;

        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(frame, "The system does not support System Tray", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear ícono simple azul en memoria
        Image trayImage = createDefaultTrayImage();

        // Crear menú popup
        PopupMenu popup = new PopupMenu();

        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(e -> restoreWindow());

        MenuItem exitItem = new MenuItem("exit");
        exitItem.addActionListener(e -> {
            tray.remove(trayIcon);
            System.exit(0);
        });

        popup.add(openItem);
        popup.add(exitItem);

        // Crear el TrayIcon
        trayIcon = new TrayIcon(trayImage, "My secret manager", popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> restoreWindow());

        // Agregar listener al frame
        frame.addWindowStateListener(e -> {
            if (e.getNewState() == Frame.ICONIFIED) {
                minimizeToTray();
            }
        });
    }

    private void minimizeToTray() {
        try {
            tray.add(trayIcon);
            frame.setVisible(false);
            trayIcon.displayMessage("My secret manager", "The application remains active in the background", TrayIcon.MessageType.INFO);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    private void restoreWindow() {
        SwingUtilities.invokeLater(() -> {
            tray.remove(trayIcon);
            frame.setVisible(true);
            frame.setExtendedState(JFrame.NORMAL);
        });
    }

    private Image createDefaultTrayImage() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLUE);
        g.fillOval(0, 0, 16, 16);
        g.dispose();
        return image;
    }
}

