/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author ivy
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class Documento {

    public int id;
    public int id_status;
    public int id_cliente_proveedor;
    public int id_tipo_documento;
    public int folio;
    public float subtotal;
    public float impuestos;
    public float total;
    public float tipo_cambio;
    public float efectivo;
    public Cliente cliente;
    public Vector<DocumentoProducto> conceptos;
    public TipoDocumento tipo_documento;

    public Documento() {
        this.id = 0;
        this.id_status = 1;
        this.id_cliente_proveedor = -1;
        this.id_tipo_documento = -1;
        this.folio = 1;
        this.total = 0;
        this.efectivo = 0;
        this.tipo_cambio = 1;
        this.cliente = null;
        this.tipo_documento = null;
        this.conceptos = new Vector<>();
    }

    /**
     * Cancela el documento y devuelve existencias
     *
     * @return
     */
    public boolean cancelar() {
        if (this.id == 0) {
            return false;
        }

        boolean esVenta = this.tipo_documento.tipo == 0;

        for (DocumentoProducto c : conceptos) {
            c.id_status = 0;
            if (esVenta) {
                c.producto.existencia += c.cantidad;
            } else {
                c.producto.existencia -= c.cantidad;
            }
            if (!c.producto.save()) {
                return false;
            }
            c.save();
        }

        this.id_status = 0;
        return this.save();
    }

    /**
     * Guarda el producto en la DBMS. Si existe, lo reemplaza.
     */
    public boolean save() {
        try {
            // String $id = String.valueOf(this.id);
            if (this.id == 0) {
                this.id = Documento.getLastId();
            }

            String sql = String.format("insert or replace into documentos "
                    + "(id, id_status, id_cliente_proveedor, id_tipo_documento, "
                    + "folio, total, efectivo, tipo_cambio) values "
                    + "(%d, %d, %d, %d, %d, %.2f, %.2f, %.2f)",
                    this.id, id_status, id_cliente_proveedor, id_tipo_documento,
                    folio, total, efectivo, tipo_cambio);

            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    public DocumentoProducto findProductoById(int id_producto) {
        for (DocumentoProducto p : conceptos) {
            if (p.id_producto == id_producto && p.id_status == 1) {
                return p;
            }
        }
        return null;
    }

    public boolean delete() {
        try {
            if (this.id == 0) {
                throw new SQLException("No existe el registro en la DB.");
            }
            String sql = String.format("delete from documentos where id=%d", this.id);
            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    private static Documento unserialize(ResultSet rs) throws SQLException {
        Documento p = new Documento();
        p.id = rs.getInt("id");
        p.id_status = rs.getInt("id_status");
        p.id_cliente_proveedor = rs.getInt("id_cliente_proveedor");
        p.id_tipo_documento = rs.getInt("id_tipo_documento");
        p.folio = rs.getInt("folio");
        p.total = rs.getFloat("total");
        p.efectivo = rs.getFloat("efectivo");
        p.tipo_cambio = rs.getFloat("tipo_cambio");

        if (p.id_cliente_proveedor > 0) {
            p.cliente = dao.Cliente.findFirstById(p.id_cliente_proveedor);
        }

        if (p.id_tipo_documento > 0) {
            p.tipo_documento = dao.TipoDocumento.findFirstById(p.id_tipo_documento);
        }

        // obtener conceptos de este documento
        if (p.id > 0) {
            p.conceptos = dao.DocumentoProducto.findByIdDocumento(p.id);
        }

        return p;
    }

    public static Documento findFirstById(int id) {
        try {
            String sql = String.format("select * from documentos where id=%d", id);
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            return Documento.unserialize(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    public static Vector<Documento> find() {
        Vector<Documento> v = new Vector<Documento>();
        try {
            String sql = String.format("select * from documentos order by id desc");

            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                v.add(Documento.unserialize(rs));
            }
        } catch (Exception e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return v;
    }

    public static int getLastId() throws SQLException {
        String sql = String.format("select id from documentos order by id desc limit 1");
        Statement s = Store.drv.createQuery();
        ResultSet rs = s.executeQuery(sql);
        boolean row = rs.next();
        // sin registros
        if (!row) {
            return 1;
        }
        return rs.getInt("id") + 1;
    }
}
