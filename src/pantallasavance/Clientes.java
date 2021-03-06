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
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Admin
 */
public class Clientes extends javax.swing.JDialog {

    /**
     * Creates new form Clientes
     */
    private boolean esProveedores;
    public boolean isModal;
    public Cliente cliente;
    public Vector<Cliente> clientes;
    public Cliente selectedClient;
    public String modelName;

    public Clientes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setResizable(false);
        Store.centerFrame(this);
        this.modelName = "cliente";
        this.cliente = null;
        this.selectedClient = null;
        this.clientes = new Vector<Cliente>();
        this.isModal = modal;
        this.esProveedores = false;
        this.query();
        Clientes $vm = this;
        this.table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                selectedClient = clientes.get(row);
                btnEliminarCliente.setEnabled(true);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    if (row >= clientes.size()) {
                        return;
                    }
                    if (modal) {
                        $vm.cliente = selectedClient;
                        $vm.dispose();
                        return;
                    }
                    ClienteFrm frm = new ClienteFrm(null, true, selectedClient, esProveedores);
                    frm.setVisible(true);
                    // refrescar lista despues de cerrar el form
                    $vm.query();
                }
            }
        });
        Store.attachKeys(this, false);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // si es modal, solo permitir seleccionar cliente
        if (modal) {
            this.labelTitle.setText("Seleccionar cliente");
            Store.attachKeys(this, this.table);
        }

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (modal) {
                    int idx = $vm.table.getSelectedRow();
                    if (idx == -1 || idx >= clientes.size())
                        return;
                    
                    $vm.cliente = clientes.get(idx);
                    $vm.dispose();
                    return;
                }
            }
        };
        
        this.btnEliminarCliente.setEnabled(false);
        this.inputKeyword.addActionListener(action);
    }
    
    public void esProveedores(){
        esProveedores = true;
        modelName = "proveedor";
        jLabel1.setText("Buscar proveedores por Id o descripci??n");
        labelTitle.setText("Proveedores");
        btnEliminarCliente.setText("Eliminar proveedor seleccionado");
        this.setTitle("Proveedores");
        this.query();
    }

    public void query() {
        clientes = Cliente.find(this.inputKeyword.getText(), esProveedores);
        String[] header = {"Id", "Nombre", "RFC", "Telefono", "Calle"};
        DefaultTableModel dtm = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;//This causes all cells to be not editable
            }
        };

        dtm.setColumnIdentifiers(header);
        for (Cliente c : clientes) {
            String datos[] = new String[header.length];
            datos[0] = String.valueOf(c.id);
            datos[1] = c.nombre;
            datos[2] = c.rfc;
            datos[3] = c.telefono;
            datos[4] = c.calle;
            dtm.addRow(datos);
        }
        this.table.setModel(dtm);
        this.labelTooltip.setText(String.format("Mostrando %d de %d registros", dtm.getRowCount(), dtm.getRowCount()));
        selectedClient = null;
        Clientes $vm = this;
        if (this.isModal) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    $vm.table.setRowSelectionInterval(0, 0);
                }
            });

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        labelTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        inputKeyword = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnEliminarCliente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        labelTooltip = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clientes");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 54, 159));
        jPanel1.setForeground(new java.awt.Color(255, 54, 159));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTitle.setBackground(new java.awt.Color(255, 255, 255));
        labelTitle.setFont(labelTitle.getFont().deriveFont(labelTitle.getFont().getSize()+6f));
        labelTitle.setForeground(new java.awt.Color(255, 255, 255));
        labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelTitle.setText("Clientes");
        jPanel1.add(labelTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(235, 20, 380, -1));

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inputKeyword.setToolTipText("B??squeda por Id o descripci??n");
        inputKeyword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputKeywordActionPerformed(evt);
            }
        });
        jPanel3.add(inputKeyword, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 31, 347, 32));

        jButton2.setBackground(new java.awt.Color(0, 109, 186));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Buscar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(375, 36, 108, -1));

        jLabel1.setText("Buscar clientes por Id o descripci??n");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 12, -1, -1));

        btnEliminarCliente.setBackground(new java.awt.Color(186, 0, 0));
        btnEliminarCliente.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarCliente.setText("Eliminar cliente seleccionado");
        btnEliminarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClienteActionPerformed(evt);
            }
        });
        jPanel3.add(btnEliminarCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 320, 220, -1));

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
        jScrollPane1.setViewportView(table);

        jPanel3.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 73, 611, 239));

        labelTooltip.setText("--- tooltip ---");
        jPanel3.add(labelTooltip, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, -1, -1));

        jButton4.setBackground(new java.awt.Color(186, 0, 0));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Nuevo");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(511, 36, 120, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/logo-pink.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jLabel2.setMaximumSize(new java.awt.Dimension(200, 61));
        jLabel2.setPreferredSize(new java.awt.Dimension(200, 61));
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEliminarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClienteActionPerformed
        if (selectedClient == null || selectedClient.id <= 0){
            return;
        }
        
        boolean isOk = Store.question("Confirmaci??n", "??Desea eliminar el '" + modelName + ' ' + selectedClient.nombre + "'? Este cambio ser?? irreversible.");
        if (!isOk) {
            return;
        }

        boolean result = selectedClient.delete();
        if (!result) {
            Store.error("Error", "No se elimin?? el " + modelName + ".");
            return;
        }
        
        
        this.query();
    }//GEN-LAST:event_btnEliminarClienteActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.query();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void inputKeywordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputKeywordActionPerformed
        // TODO add your handling code here:
        this.query();
    }//GEN-LAST:event_inputKeywordActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
// TODO add your handling code here:
        ClienteFrm frm = new ClienteFrm(null, true, null, esProveedores);
        frm.setVisible(true);
        // refrescar lista despues de cerrar el form
        this.query();        // TODO add your handling code here:
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
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Clientes dialog = new Clientes(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JTextField inputKeyword;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JLabel labelTooltip;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
