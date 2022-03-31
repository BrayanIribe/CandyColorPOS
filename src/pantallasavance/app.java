/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pantallasavance;

import dao.LobbyStats;
import dao.Store;
import java.util.logging.Level;
import java.util.logging.Logger;
import pantallasavance.Splash;

/**
 *
 * @author ivy
 */
public class app {

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(pantallaInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pantallaInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pantallaInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pantallaInicio.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */

        Thread thread = new Thread() {
            public void run() {
                Splash splash = new Splash();
                splash.setAlwaysOnTop(true);
                splash.setVisible(true);
                long timestamp = System.currentTimeMillis();
                System.out.println("Initializing DBMS...");
                Store.initialize();
                long diff = System.currentTimeMillis() - timestamp;
                splash.setProgress(70);
                System.out.printf("DBMS initialized. Took: %dms\n", (int)diff);
                try {
                    splash.setProgress(90);
                    Thread.sleep(500);
                    splash.setProgress(100);
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(app.class.getName()).log(Level.SEVERE, null, ex);
                }
                splash.dispose();
                PantallaPrincipal inicio = new PantallaPrincipal();
                inicio.setVisible(true);
            }
        };

        thread.start();
    }
}
