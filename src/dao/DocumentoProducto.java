/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author Ivy
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class DocumentoProducto {

    public int id;
    public int id_status; // 0=cancelado, 1=vigente, 2=devuelto
    public int tipo_inv;
    public int id_documento;
    public int id_producto;
    public String descripcion;
    public float cantidad;
    public float costo;
    public float precio;
    public float impuestos;
    public float utilidades;
    public float existencia;
    public Producto producto;

    public DocumentoProducto() {
        id = 0;
        id_status = 1;
        id_documento = -1;
        id_producto = -1;
        tipo_inv = 0;
        descripcion = "";
        cantidad = 0;
        costo = 0;
        precio = 0;
        impuestos = 0;
        utilidades = 0;
        existencia = 0;
        producto = null;
    }

    /**
     * Guarda el producto en la DBMS. Si existe, lo reemplaza.
     */
    public boolean save() {
        try {
            // String $id = String.valueOf(this.id);
            if (this.id == 0) {
                this.id = DocumentoProducto.getLastId();
            }
            
            String sql = String.format("insert or replace into documentos_productos "
                    + "(id, id_status, tipo_inv, id_documento, id_producto, "
                    + "descripcion, cantidad, costo, precio, impuestos, utilidades,"
                    + "existencia) values (%d, %d, %d, %d, %d, '%s',"
                    + "%.2f, %.2f, %.2f, %.2f, %.2f, %.2f)",
                    this.id, id_status, tipo_inv, id_documento, id_producto, descripcion,
                    cantidad, costo, precio, impuestos, utilidades, existencia);

            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    public boolean delete() {
        try {
            if (this.id == 0) {
                throw new SQLException("No existe el registro en la DB.");
            }
            String sql = String.format("delete from documentos_productos where id=%d", this.id);
            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    private static DocumentoProducto unserialize(ResultSet rs) throws SQLException {
        DocumentoProducto p = new DocumentoProducto();
        p.id = rs.getInt("id");
        p.id_status = rs.getInt("id_status");
        p.id_documento = rs.getInt("id_documento");
        p.id_producto = rs.getInt("id_producto");
        p.descripcion = rs.getString("descripcion");
        p.cantidad = rs.getFloat("cantidad");
        p.costo = rs.getFloat("costo");
        p.precio = rs.getFloat("precio");
        p.impuestos = rs.getFloat("impuestos");
        p.utilidades = rs.getFloat("utilidades");
        p.existencia = rs.getFloat("existencia");
        p.producto = dao.Producto.findFirstById(p.id_producto);
        return p;
    }
    
    public Documento obtenerDocumento(){
        return dao.Documento.findFirstById(this.id_documento);
    }

    public static DocumentoProducto findFirstById(int id) {
        try {
            String sql = String.format("select * from documentos_productos where id=%d", id);
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            return DocumentoProducto.unserialize(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    public static Vector<DocumentoProducto> findByIdDocumento(int id) {
        Vector<DocumentoProducto> v = new Vector<DocumentoProducto>();
        try {
            String sql = String.format(
                    "select * from documentos_productos where id_documento=%d",
                    id
            );

            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                v.add(DocumentoProducto.unserialize(rs));
            }
        } catch (Exception e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return v;
    }

    public static int getLastId() throws SQLException {
        String sql = String.format("select id from documentos_productos order by id desc limit 1");
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
