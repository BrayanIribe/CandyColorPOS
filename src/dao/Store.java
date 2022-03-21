/*
 * Store global para mantener una conexion estable al driver
 */
package dao;

/**
 *
 * @author Ivy
 */
import dao.Driver;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;

public class Store {

    public static Driver drv;

    public static void initialize() {
        try {
            Store.drv = new Driver();
            Store.drv.createTables();
        } catch (SQLException e) {
            Store.error("Error al inicializar la DBMS", e.getMessage());
        }
    }
    
    public static void success(String title, String msg) {
        JFrame jf = new JFrame();
        jf.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(jf, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(String title, String msg) {
        JFrame jf = new JFrame();
        jf.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(jf, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean question(String title, String message) {
        JFrame jf = new JFrame();
        jf.setAlwaysOnTop(true);
        int reply = JOptionPane.showConfirmDialog(jf, message, title, JOptionPane.YES_NO_OPTION);
        return reply == JOptionPane.YES_OPTION;
    }

    public static void attachKeys(JDialog frm, boolean askOnDismiss) {
        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (askOnDismiss){
                    boolean q = Store.question("¿Estás seguro que deseas salir?", "Perderás todos los cambios no guardados.");
                    if (!q)
                        return;
                }
                frm.dispose();
            }
        };

        frm.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    public static void attachKeys(JDialog frm, JTable table){
        ActionListener upListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = table.getSelectedRow();
                idx--;
                if (idx < 0){
                    idx = table.getRowCount() - 1;
                }
                table.setRowSelectionInterval(idx, idx);
            }
        };
        
        ActionListener downListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = table.getSelectedRow();
                idx++;
                if (idx >= table.getRowCount()){
                    idx = 0;
                }
                table.setRowSelectionInterval(idx, idx);
            }
        };

        frm.getRootPane().registerKeyboardAction(upListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        frm.getRootPane().registerKeyboardAction(downListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void centerFrame(Frame frame) {
        //Obtiene el tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Obtiene el tamaño de la ventana de la aplicación
        Dimension frameSize = frame.getSize();

        // Se asegura que el tamaño de la ventana de la aplicación
        // no exceda el tamaño de la pantalla
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        // Centra la ventana de la aplicación sobre la pantalla
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }

    public static void centerFrame(JDialog frame) {
        //Obtiene el tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Obtiene el tamaño de la ventana de la aplicación
        Dimension frameSize = frame.getSize();

        // Se asegura que el tamaño de la ventana de la aplicación
        // no exceda el tamaño de la pantalla
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        // Centra la ventana de la aplicación sobre la pantalla
        frame.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
