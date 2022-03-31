/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pantallasavance;

import javax.swing.JFrame;
import dao.Producto;
import dao.Store;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import pantallasavance.ClienteFrm;
import dao.Cliente;
import dao.Documento;
import dao.LobbyStats;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Admin
 */
public class Documentos extends javax.swing.JDialog {

    /**
     * Creates new form Clientes
     */
    public Documento documento;
    public Vector<Documento> documentos;

    public Documentos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setResizable(false);
        Store.centerFrame(this);
        this.documento = null;
        this.documentos = new Vector<Documento>();
        Documentos $vm = this;
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                $vm.mapFields();
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    if (row >= documentos.size()) {
                        return;
                    }
                    Sistema frm = new Sistema(null, true, documentos.get(row));
                    frm.setVisible(true);
                }
            }
        });
        Store.attachKeys(this, false);
        this.mapFields();
        this.inputKeyword.requestFocus();
        
        ActionListener cancelarDocListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                $vm.btnCancelarActionPerformed(e);
            }
        };

        $vm.getRootPane().registerKeyboardAction(cancelarDocListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }

    public void mapFields() {
        int row = table.getSelectedRow();
        
        if (row != -1) {
            this.documento = documentos.get(row);
        }
        
        documentos = Documento.findDocumentos(this.inputKeyword.getText());
        String[] header = {"Id", "Documento", "Estado", "Tipo", "Cliente", "Total"};
        DefaultTableModel dtm = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;//This causes all cells to be not editable
            }
        };
        dtm.setColumnIdentifiers(header);
        this.table.setModel(dtm);
        int idx = -1;
        for (Documento d : documentos) {
            String datos[] = new String[header.length];
            String $tipo = "???";
            String $estado = d.id_status == 0 ? "Cancelado" : "Vigente";
            if (d.tipo_documento.tipo == 0)
                $tipo = "Venta";
            else if (d.tipo_documento.tipo == 1)
                $tipo = "Compra";
            else if (d.tipo_documento.tipo == 2)
                $tipo = "Pedido";
            datos[0] = String.valueOf(d.id);
            datos[1] = String.format("%s-%d", d.tipo_documento.serie, d.folio);
            datos[2] = $estado;
            datos[3] = $tipo;
            datos[4] = d.cliente.nombre;
            datos[5] = String.format("$%.2f", d.total);
            dtm.addRow(datos);
            if (this.documento != null && d.id == this.documento.id) {
                idx = dtm.getRowCount() - 1;
            }
        }
        if (idx != -1) {
            this.table.setRowSelectionInterval(idx, idx);
        }
        this.labelTooltip.setText(String.format("Mostrando %d de %d registros", dtm.getRowCount(), dtm.getRowCount()));

        // actualizar stats
        LobbyStats stats = LobbyStats.get();
        if (stats != null){
        this.labelVentas.setText(
                String.format("Ventas: $%.2f MXN", stats.total_ventas, stats.ventas)
        );
        this.labelCompras.setText(
                String.format("Compras: $%.2f MXN", stats.total_compras, stats.compras)
        );
        this.labelUtilidades.setText(String.format("Utilidades: $%.2f MXN", stats.total_utilidades));
        }
        // toggle cancelar btn
        this.btnCancelar.setEnabled(this.documento != null && this.documento.id_status == 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner1 = new javax.swing.JSpinner();
        jPanel1 = new javax.swing.JPanel();
        labelTitle = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        labelTooltip = new javax.swing.JLabel();
        labelVentas = new javax.swing.JLabel();
        btnCancelar = new javax.swing.JButton();
        labelCompras = new javax.swing.JLabel();
        labelUtilidades = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        inputKeyword = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Documentos");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 54, 159));
        jPanel1.setForeground(new java.awt.Color(255, 54, 159));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitle.setBackground(new java.awt.Color(255, 255, 255));
        labelTitle.setFont(labelTitle.getFont().deriveFont(labelTitle.getFont().getSize()+6f));
        labelTitle.setForeground(new java.awt.Color(255, 255, 255));
        labelTitle.setText("Pedidos");
        jPanel1.add(labelTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/logo-pink.png"))); // NOI18N
        jLabel6.setText("jLabel2");
        jLabel6.setMaximumSize(new java.awt.Dimension(200, 61));
        jLabel6.setPreferredSize(new java.awt.Dimension(200, 61));
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jScrollPane1.setForeground(new java.awt.Color(200, 200, 200));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        labelTooltip.setText("--- tooltip ---");

        labelVentas.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        labelVentas.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelVentas.setText("Ventas: ---");

        btnCancelar.setBackground(new java.awt.Color(186, 0, 0));
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar pedido seleccionado (F1)");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        labelCompras.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        labelCompras.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCompras.setText("Compras: ---");

        labelUtilidades.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        labelUtilidades.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelUtilidades.setText("Utilidades: ---");

        jLabel1.setText("Buscar pedidos por cliente o folio");

        inputKeyword.setToolTipText("Búsqueda por Id o descripción");
        inputKeyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKeywordActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 109, 186));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Buscar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(186, 0, 0));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Nuevo");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelCompras)
                            .addComponent(labelVentas)
                            .addComponent(labelUtilidades))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(labelTooltip)
                                .addGap(9, 9, 9))
                            .addComponent(btnCancelar, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(inputKeyword, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(3, 3, 3)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inputKeyword, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton2)
                            .addComponent(jButton4))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelVentas)
                    .addComponent(btnCancelar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCompras)
                    .addComponent(labelTooltip))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUtilidades)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        this.mapFields();
        if (this.documento == null)
            return;
        String msg = String.format(
                "Estas apunto de cancelar el documento %s-%d.\n"
                + "Este cambio será irreversible.", this.documento.tipo_documento.serie,
                this.documento.folio);
        boolean ok = Store.question("¿Estas seguro?", msg);
        if (!ok){
            return;
        }
        
        boolean result = this.documento.cancelar();
        if (!result)
            Store.error("Ha ocurrido un problema", "No se ha podido cancelar el documento por razones desconocidas.");
        this.mapFields();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tableKeyPressed

    private void tableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyReleased
        // TODO add your handling code here:
        this.mapFields();
    }//GEN-LAST:event_tableKeyReleased

    private void inputKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKeywordActionPerformed
        // TODO add your handling code here:
        this.mapFields();
    }//GEN-LAST:event_inputKeywordActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.mapFields();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        Sistema frm = new Sistema(null, true, null);
        frm.setVisible(true);
        // refrescar lista despues de cerrar el form
        this.mapFields();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(Documentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Documentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Documentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Documentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Documentos dialog = new Documentos(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JTextField inputKeyword;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JLabel labelCompras;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JLabel labelTooltip;
    private javax.swing.JLabel labelUtilidades;
    private javax.swing.JLabel labelVentas;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
