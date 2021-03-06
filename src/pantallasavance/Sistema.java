/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pantallasavance;

import dao.Store;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import dao.Producto;
import dao.TipoDocumento;
import dao.Documento;
import dao.Cliente;
import dao.DocumentoProducto;
import dao.CmdType;
import dao.SystemState;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Admin
 */
public class Sistema extends javax.swing.JDialog {

    public String[] header = {"Cantidad", "Descripción", "Precio", "Subtotal"};
    public DefaultTableModel dtm;
    public Documento documento;
    public Vector<TipoDocumento> documentos;
    public Cliente cliente;
    private long timestamp = 0;
    private SystemState state = SystemState.VENTA;

    /**
     * Creates new form Nosotros
     */
    public Sistema(java.awt.Frame parent, boolean modal, Documento documento) {
        super(parent, modal);
        initComponents();
        setResizable(false);
        Store.centerFrame(this);
        
        this.reset();
        if (documento != null){
            this.documento = documento;
            this.inputObservaciones.setText(this.documento.observaciones);
            this.inputCodigo.setEnabled(false);
            this.btnSeleccionarCliente.setEnabled(false);
            this.selectTipoDoc.setEnabled(false);
            this.jButton1.setEnabled(false);
        }
        this.mapFields();
        
        Sistema $vm = this;
        this.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                $vm.inputCodigo.requestFocus();
            }
        });

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                $vm.procesarCodigo($vm.inputCodigo.getText());
                $vm.inputCodigo.setText("");
            }
        };

        this.inputCodigo.addActionListener(action);

        // Store.attachKeys(this, true);

        ActionListener escListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long oldTimestamp = $vm.timestamp;
                $vm.timestamp = System.currentTimeMillis();
                long diff = $vm.timestamp - oldTimestamp;

                $vm.inputCodigo.requestFocus();
                if (diff > 500) {
                    return;
                }

                boolean q = Store.question("¿Estás seguro que deseas salir?", "Perderás todos los cambios no guardados.");
                if (!q) {
                    return;
                }
                $vm.dispose();
            }
        };

        ActionListener clientesModalListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                $vm.fireClientesModal();
            }
        };

        ActionListener selectTipoDocListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idx = $vm.selectTipoDoc.getSelectedIndex();
                idx++;
                if (idx >= $vm.selectTipoDoc.getItemCount())
                    idx = 0;
                $vm.selectTipoDoc.setSelectedIndex(idx);
            }
        };

        $vm.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        $vm.getRootPane().registerKeyboardAction(clientesModalListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        if (false)
        $vm.getRootPane().registerKeyboardAction(selectTipoDocListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    // Reinicia la VM a su estado inicial.
    public void reset(){
        this.state = SystemState.VENTA;
        this.documento = new Documento();
        this.cliente = Cliente.findFirstById(1);

        if (cliente == null || !cliente.rfc.equals("XAXX0101000")) {
            Store.error("Sistema dañado", "No se encontró cliente publico en general.");
            this.dispose();
            return;
        }

        this.documentos = TipoDocumento.find(null);

        String keys[] = new String[documentos.size()];

        for (int i = 0; i < documentos.size(); i++) {
            TipoDocumento d = documentos.get(i);
            String tipo = d.tipo == 0 ? "venta" : "compra";
            keys[i] = String.format("%s", d.nombre);
        }

        int lastIdx = this.selectTipoDoc.getModel().getSize() > 0 ? this.selectTipoDoc.getSelectedIndex() : 0;
        DefaultComboBoxModel selectVentaModel = new DefaultComboBoxModel(keys);
        this.selectTipoDoc.setModel(selectVentaModel);
        if (lastIdx > 0)
        this.selectTipoDoc.setSelectedIndex(lastIdx);

        dtm = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;//This causes all cells to be not editable
            }
        };

        dtm.setColumnIdentifiers(header);
        this.tableConceptos.setModel(dtm);

        this.documento = new Documento();
        this.inputObservaciones.setText("");
        this.mapFields();
    }

    public void fireClientesModal() {
        // TODO add your handling code here:
        Clientes frm = new Clientes(null, true);
        frm.setVisible(true);

        // esperar por el modal
        if (frm.cliente != null) {
            this.cliente = frm.cliente;
            this.mapFields();
        }
    }

    public void mapFields() {
        int tipo_documento_idx = Integer.valueOf(this.selectTipoDoc.getSelectedIndex());
        TipoDocumento tipo_documento = documentos.get(tipo_documento_idx);
        this.documento.tipo_documento = tipo_documento;
        this.documento.id_tipo_documento = tipo_documento.id;
        this.documento.id_cliente_proveedor = cliente.id;
        this.documento.folio = tipo_documento.folio;
        this.labelCliente.setText(String.format("Cliente: %d - %s", cliente.id, cliente.nombre));
        this.labelFolio.setText(String.format("Folio: %s-%d", tipo_documento.serie, tipo_documento.folio));
        
        this.labelInventario.setText("???");
        this.labelInventario.setBackground(Color.gray);
        if (tipo_documento.tipo == 0){
            // salida
            this.labelInventario.setText("VENTA");
            this.labelInventario.setForeground(Color.WHITE);
            this.labelInventario.setBackground(Color.RED);
            this.paneInventario.setBackground(Color.RED);
        }else if (tipo_documento.tipo == 1){
            // compra
            this.labelInventario.setText("COMPRA");
            this.labelInventario.setForeground(Color.WHITE);
            this.labelInventario.setBackground(Color.DARK_GRAY);
            this.paneInventario.setBackground(Color.DARK_GRAY);
        } else if (tipo_documento.tipo == 2){
            this.labelInventario.setText("PEDIDO");
            this.labelInventario.setForeground(Color.MAGENTA);
            this.labelInventario.setBackground(Color.WHITE);
            this.paneInventario.setBackground(Color.WHITE);
        }
        // mapear conceptos en la tabla
        if (dtm.getRowCount() > 0) {
            for (int i = dtm.getRowCount() - 1; i > -1; i--) {
                dtm.removeRow(i);
            }
        }

        for (DocumentoProducto p : this.documento.conceptos) {
            String datos[] = new String[header.length];
            datos[0] = String.format("%.2f", p.cantidad);
            datos[1] = String.format("%s", p.descripcion);
            datos[2] = String.format("$%.2f", p.precio);
            datos[3] = String.format("$%.2f", p.precio * p.cantidad);
            if (p.id_status == 0) {
                datos[0] = "-" + datos[0];
                datos[1] = "(CANCELADO) " + datos[1];
            }
            dtm.addRow(datos);
        }

        // update documentos
        float $subtotal = 0;
        float $impuestos = 0;
        float $total = 0;

        for (DocumentoProducto p : this.documento.conceptos) {
            if (p.id_status != 1) {
                continue;
            }

            float subtotal = p.cantidad * p.precio;

            $subtotal += subtotal;
            $impuestos += p.impuestos;
            $total += subtotal + p.impuestos;
        }

        if (this.state == SystemState.PAGO) {

            float faltante = $total - documento.efectivo;
            this.labelCodigo.setText("Efectivo");
            this.labelTotal.setText(String.format("Faltante: $%.2f MXN", faltante));
            this.labelTotal.setForeground(Color.red);
            if (faltante <= 0) {
                this.state = SystemState.CAMBIO;
            }
        }

        if (this.state == SystemState.CAMBIO) {
            float cambio = Math.abs(documento.efectivo - $total);
            this.labelTotal.setForeground(Color.BLACK);
            this.labelTotal.setText(String.format("Cambio: $%.2f MXN", cambio));
        }

        if (this.state != SystemState.VENTA) {
            return;
        }

        this.labelCodigo.setText("Código");
        this.labelTotal.setText(String.format("Total: $%.2f MXN", $total));

        this.documento.subtotal = $subtotal;
        this.documento.impuestos = $impuestos;
        this.documento.total = $total;

    }

    public boolean subtotal(){
            // actualizar la VM
            this.mapFields();
            if (this.documento.conceptos.size() <= 0
                    || (this.documento.subtotal <= 0 && this.documento.tipo_documento.tipo == 0)) {
                Store.error("Ha ocurrido un problema", "No puede terminar una cuenta en ceros.");
                return false;
            }

            // volver a actualizar la VM
            this.state = this.documento.tipo_documento.tipo == 2 ? SystemState.CAMBIO : SystemState.PAGO;
            this.mapFields();
            return true;
    }
    
    public void procesarCodigo(String cmd) {

        CmdType cmdType = CmdType.AGREGAR; // 0=agregar, 1=cancelar
        cmd = cmd.toLowerCase();

        if (cmd.equals("sbt") && this.state == SystemState.VENTA) {
           if (!subtotal())
               return;
        }

        if (this.state == SystemState.PAGO) {
            try {
                float val = Float.valueOf(cmd);
                this.documento.efectivo += val;
                if (this.documento.efectivo < 0) {
                    this.documento.efectivo = 0;
                }
                this.mapFields();
            } catch (Exception e) {
                System.out.println("(SystemState.PAGO) Handled exception: " + e.getMessage());
            }
        }

        if (this.state == SystemState.CAMBIO) {
            // TERMINARVENTAA()
            // registrar el documento en DBMS

            this.documento.observaciones = this.inputObservaciones.getText();
            boolean esVenta = this.documento.tipo_documento.tipo == 0;
            boolean res = this.documento.save();
            if (!res) {
                this.state = SystemState.PAGO;
                this.documento.efectivo = 0;
            }
            for (DocumentoProducto p : this.documento.conceptos) {
                p.id_documento = this.documento.id;
                p.tipo_inv = this.documento.tipo_documento.tipo;
                p.existencia = p.producto.existencia;
                p.save();
                
                // actualizar existencia
                
                if (this.documento.tipo_documento.tipo != 2){
                if (esVenta) {
                    p.producto.existencia -= p.cantidad;
                } else {
                    p.producto.existencia += p.cantidad;
                }
                }

                p.producto.save();
            }
            
            // al terminar el documento, incrementar folio y limpiar
            this.documento.tipo_documento.folio++;
            this.documento.tipo_documento.save();
            
            Store.success("",  "Documento tipo " + this.documento.tipo_documento.nombre + " completado satisfactoriamente.");
            // reiniciar la VM a su estado inicial
            this.reset();
            return;
        }

        if (this.state != SystemState.VENTA) {
            return;
        }

        // desea cancelar
        if (cmd.indexOf("can") == 0 && cmd.length() > 3) {
            cmdType = CmdType.CANCELAR;
            cmd = cmd.substring(3);
        }
        
        float cantidad = 1;
        
        // verificar si contiene codigo de qty
        int qtyIdx = cmd.indexOf("*");
        if (qtyIdx != -1){
            try{
            String $cantidad = cmd.substring(0, qtyIdx);
            String $cmd = cmd.substring(qtyIdx + 1);
            cantidad = Float.valueOf($cantidad);
            cmd = $cmd;
            }catch(Exception e){return;}
        }

        // ningun codigo funciono, intentar agregarlo al documento
        Producto p = Producto.findFirstByCodigo(cmd);

        if (p == null) {
            Store.error(
                    "Ha ocurrido un problema",
                    String.format("El producto con código '%s' no se encontró en el sistema.", cmd)
            );
            return;
        }

        DocumentoProducto concepto = documento.findProductoById(p.id);

        if (cmdType == CmdType.CANCELAR) {
            if (concepto == null || concepto.id_status == 0) {
                Store.error(
                        "Ha ocurrido un problema",
                        "No existe registro en el documento con dicho codigo.");
                return;
            }

            concepto.id_status = 0;
            this.mapFields();
            return;
        }

        boolean update = true;
        if (concepto == null) {
            concepto = new DocumentoProducto();
            update = false;
        }

        if (update)
            cantidad += concepto.cantidad;

        float subtotal = p.precio * cantidad;
        float impuestos = 0;

        concepto.id = documento.conceptos.size();
        concepto.cantidad = cantidad;
        concepto.costo = p.costo;
        concepto.descripcion = p.descripcion;
        concepto.existencia = p.existencia;
        concepto.id_documento = documento.id;
        concepto.id_producto = p.id;
        concepto.id_status = 1;
        concepto.impuestos = 0.0f;
        concepto.precio = p.precio;
        concepto.impuestos = impuestos;
        concepto.producto = p;
        concepto.utilidades = subtotal - (p.costo * cantidad);
        if (!update) {
            this.documento.conceptos.add(concepto);
        }

        this.mapFields();
    }
    
    
    public void setDocumento(String documentoName){
        if (documentoName.equals("venta")){
            this.selectTipoDoc.setSelectedIndex(1);
        }else if (documentoName.equals("pedido")){
            this.selectTipoDoc.setSelectedIndex(0);
        }
        this.selectTipoDoc.setEnabled(false);
        this.mapFields();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableConceptos = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        inputCodigo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelCodigo = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        selectTipoDoc = new javax.swing.JComboBox<>();
        labelFolio = new javax.swing.JLabel();
        paneInventario = new javax.swing.JPanel();
        labelInventario = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labelCliente = new javax.swing.JLabel();
        btnSeleccionarCliente = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        labelObservaciones = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        inputObservaciones = new javax.swing.JTextArea();
        labelTotal = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tableConceptos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tableConceptos);

        jLabel3.setForeground(new java.awt.Color(80, 80, 80));
        jLabel3.setText("<cantidad>*<codigo>");

        jLabel4.setForeground(new java.awt.Color(80, 80, 80));
        jLabel4.setText("SBT - Terminar cuenta");

        jLabel5.setForeground(new java.awt.Color(80, 80, 80));
        jLabel5.setText("CAN<codigo> - Cancelar");

        labelCodigo.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        labelCodigo.setText("Código");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(labelCodigo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelCodigo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jLabel2.setText("Documento (F1)");

        selectTipoDoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        selectTipoDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTipoDocActionPerformed(evt);
            }
        });

        labelFolio.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        labelFolio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelFolio.setText("Folio: ---");

        paneInventario.setBackground(new java.awt.Color(180, 180, 180));
        paneInventario.setForeground(new java.awt.Color(255, 255, 255));

        labelInventario.setBackground(new java.awt.Color(0, 0, 0));
        labelInventario.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        labelInventario.setForeground(new java.awt.Color(255, 255, 255));
        labelInventario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelInventario.setText("COMPRA");

        javax.swing.GroupLayout paneInventarioLayout = new javax.swing.GroupLayout(paneInventario);
        paneInventario.setLayout(paneInventarioLayout);
        paneInventarioLayout.setHorizontalGroup(
            paneInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneInventarioLayout.createSequentialGroup()
                .addComponent(labelInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );
        paneInventarioLayout.setVerticalGroup(
            paneInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labelInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        labelCliente.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        labelCliente.setText("Cliente: ---");

        btnSeleccionarCliente.setText("Seleccionar (F2)");
        btnSeleccionarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSeleccionarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCliente)
                    .addComponent(btnSeleccionarCliente))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectTipoDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 277, Short.MAX_VALUE)
                .addComponent(labelFolio)
                .addGap(18, 18, 18)
                .addComponent(paneInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(selectTipoDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(labelFolio))
                    .addComponent(paneInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 54, 159));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/logo-pink.png"))); // NOI18N
        jLabel6.setText("jLabel2");
        jLabel6.setMaximumSize(new java.awt.Dimension(200, 61));
        jLabel6.setPreferredSize(new java.awt.Dimension(200, 61));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        labelObservaciones.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        labelObservaciones.setText("Observaciones");

        inputObservaciones.setColumns(20);
        inputObservaciones.setRows(5);
        jScrollPane2.setViewportView(inputObservaciones);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(labelObservaciones)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelObservaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        labelTotal.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        labelTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelTotal.setText("Total: ---");

        jButton1.setBackground(new java.awt.Color(255, 54, 159));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Guardar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(labelTotal)
                                .addContainerGap())))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectTipoDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectTipoDocActionPerformed
        // TODO add your handling code here:
        this.mapFields();
    }//GEN-LAST:event_selectTipoDocActionPerformed

    private void btnSeleccionarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarClienteActionPerformed
        this.fireClientesModal();
    }//GEN-LAST:event_btnSeleccionarClienteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        procesarCodigo("sbt");
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSeleccionarCliente;
    private javax.swing.JTextField inputCodigo;
    private javax.swing.JTextArea inputObservaciones;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labelCliente;
    private javax.swing.JLabel labelCodigo;
    private javax.swing.JLabel labelFolio;
    private javax.swing.JLabel labelInventario;
    private javax.swing.JLabel labelObservaciones;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JPanel paneInventario;
    private javax.swing.JComboBox<String> selectTipoDoc;
    private javax.swing.JTable tableConceptos;
    // End of variables declaration//GEN-END:variables
}
