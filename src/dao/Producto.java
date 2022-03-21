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

public class Producto {

    public int id;
    public float existencia;
    public String descripcion;
    public String codigo_barras;
    public String codigo_alterno;
    public float costo;
    public float precio;
    public float iva;
    public String clave_servicio;
    public String clave_unidad;
    public static int totalRows = 0;

    public Producto() {
        id = 0;
        existencia = 0;
        descripcion = "";
        codigo_barras = "";
        codigo_alterno = "";
        costo = 0;
        precio = 0;
        iva = 0;
        clave_servicio = "";
        clave_unidad = "";
    }

    /**
     * Guarda el producto en la DBMS. Si existe, lo reemplaza.
     */
    public boolean save() {
        try {
            // String $id = String.valueOf(this.id);
            if (this.id == 0) {
                this.id = Producto.getLastId();
            }

            String sql = String.format("insert or replace into productos "
                    + "(id, existencia, descripcion, codigo_barras, codigo_alterno,"
                    + "costo, precio, iva, clave_servicio, clave_unidad) values"
                    + "(%d, %.6f, '%s', '%s', '%s', %.2f, %.2f, %.2f, '%s', '%s')",
                    this.id, existencia, descripcion, codigo_barras, codigo_alterno,
                    costo, precio, iva, clave_servicio, clave_unidad);
            System.out.println(sql);
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
            String sql = String.format("delete from productos where id=%d", this.id);
            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    private static Producto unserialize(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.id = rs.getInt("id");
        p.existencia = rs.getFloat("existencia");
        p.descripcion = rs.getString("descripcion");
        p.codigo_barras = rs.getString("codigo_barras");
        p.codigo_alterno = rs.getString("codigo_alterno");
        p.costo = rs.getFloat("costo");
        p.precio = rs.getFloat("precio");
        p.iva = rs.getFloat("iva");
        p.clave_servicio = rs.getString("clave_servicio");
        p.clave_unidad = rs.getString("clave_unidad");
        return p;
    }

    public static Producto findFirstById(int id) {
        try {
            String sql = String.format("select * from productos where id=%d", id);
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            return Producto.unserialize(rs);
        } catch (Exception e) {
            return null;
        }
    }

    public static Producto findFirstByCodigo(String code) {
        try {
            String sql = String.format("select * from productos where codigo_barras='%s'", code);
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            return Producto.unserialize(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    public static Vector<Producto> find(String keyword) {
        Vector<Producto> v = new Vector<Producto>();
        try {
            String sql = "select * from productos where 1 ";
            if (Store.isNumeric(keyword)) {
                int code = Integer.valueOf(keyword);
                sql += String.format("and (id=%d or codigo_barras=%d or codigo_alterno='%s')", code, code, keyword);
            } else if (keyword != null && keyword.length() > 0) {
                sql += String.format("and (descripcion like '%%%s%%' or codigo_alterno='%s')", keyword, keyword);
            }

            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                v.add(Producto.unserialize(rs));
            }
        } catch (Exception e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return v;
    }

    public static int getLastId() throws SQLException {
        String sql = String.format("select id from productos order by id desc limit 1");
        Statement s = Store.drv.createQuery();
        ResultSet rs = s.executeQuery(sql);
        boolean row = rs.next();
        // sin registros
        if (!row) {
            return 1;
        }
        return rs.getInt("id") + 1;
    }

    @Override
    public String toString() {
        return "Producto{" + "id=" + id + ", existencia=" + existencia + ", descripcion=" + descripcion + ", codigo_barras=" + codigo_barras + ", codigo_alterno=" + codigo_alterno + ", costo=" + costo + ", precio=" + precio + ", iva=" + iva + ", clave_servicio=" + clave_servicio + ", clave_unidad=" + clave_unidad + '}';
    }

}
