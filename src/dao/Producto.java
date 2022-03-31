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
    public float costo;
    public float precio;
    public boolean materia_prima;
    public static int totalRows = 0;

    public Producto() {
        id = 0;
        existencia = 0;
        descripcion = "";
        codigo_barras = "";
        costo = 0;
        precio = 0;
        materia_prima = false;
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
                    + "(id, existencia, descripcion, codigo_barras, "
                    + "costo, precio, materia_prima) values"
                    + "(%d, %.6f, '%s', '%s', %.2f, %.2f, %d)",
                    this.id, existencia, descripcion, codigo_barras,
                    costo, precio, materia_prima == true ? 1 : 0);
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
        p.materia_prima = rs.getInt("materia_prima") == 1;
        p.costo = rs.getFloat("costo");
        p.precio = rs.getFloat("precio");
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
                sql += String.format("and (id=%d or codigo_barras=%d)", code, code, keyword);
            } else if (keyword != null && keyword.length() > 0) {
                sql += String.format("and (descripcion like '%%%s%%')", keyword, keyword);
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

   
}
