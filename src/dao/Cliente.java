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

public class Cliente {

    public int id;
    public String nombre;
    public String rfc;
    public String referencia;
    public String calle;
    public String num;
    public String colonia;
    public String municipio;
    public String estado;
    public String pais;
    public int codigo_postal;
    public String telefono;
    public String correo;

    public Cliente() {
        id = 0;
        nombre = "";
        referencia = "";
        rfc = "";
        calle = "";
        num = "";
        colonia = "";
        municipio = "";
        estado = "";
        pais = "";
        telefono = "";
        correo = "";
        codigo_postal = 85000;
    }

    /**
     * Guarda el cliente en la DBMS. Si existe, lo reemplaza.
     */
    public boolean save() {
        try {
            // String $id = String.valueOf(this.id);
            if (this.id == 0) {
                this.id = Cliente.getLastId();
            }

            String sql = String.format("insert or replace into clientes "
                    + "(id, nombre, rfc, referencia, calle, num, colonia, municipio,"
                    + "estado, pais, codigo_postal, telefono, correo) values"
                    + "(%d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %d,"
                    + "'%s', '%s')", this.id, this.nombre, this.rfc, this.referencia, this.calle,
                    this.num, this.colonia, this.municipio, this.estado, this.pais,
                    this.codigo_postal, this.telefono, this.correo);
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
            String sql = String.format("delete from clientes where id=%d", this.id);
            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    private static Cliente unserialize(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.id = rs.getInt("id");
        c.nombre = rs.getString("nombre");
        c.rfc = rs.getString("rfc");
        c.referencia = rs.getString("referencia");
        c.calle = rs.getString("calle");
        c.num = rs.getString("num");
        c.colonia = rs.getString("colonia");
        c.municipio = rs.getString("municipio");
        c.estado = rs.getString("estado");
        c.pais = rs.getString("pais");
        c.telefono = rs.getString("telefono");
        c.correo = rs.getString("correo");
        c.codigo_postal = rs.getInt("codigo_postal");
        return c;
    }

    public static Cliente findFirstById(int id) {
        try{
        String sql = String.format("select * from clientes where id=%d", id);
        Statement s = Store.drv.createQuery();
        ResultSet rs = s.executeQuery(sql);
        rs.next();
        return Cliente.unserialize(rs);
        }catch(Exception e){
            return null;
        }
    }

    public static Vector<Cliente> find(String keyword) {
        Vector<Cliente> v = new Vector<Cliente>();
        try {
            String sql = "select * from clientes where 1 ";
            if (keyword != null && Store.isNumeric(keyword)){
                int code = Integer.valueOf(keyword);
                sql += String.format("and (id=%d or codigo_postal=%d or num LIKE '%%%s%%')", code, code, keyword);
            }else
            if (keyword != null && keyword.length() > 0) {
                sql += String.format("and (nombre like '%%%s%%' or rfc='%s')", keyword, keyword);
            }
            
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                v.add(Cliente.unserialize(rs));
            }
        } catch (Exception e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return v;
    }

    public static int getLastId() throws SQLException {
        String sql = String.format("select id from clientes order by id desc limit 1");
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
        return "Cliente{" + "id=" + id + ", nombre=" + nombre + ", rfc=" + rfc + ", calle=" + calle + ", num=" + num + ", colonia=" + colonia + ", municipio=" + municipio + ", estado=" + estado + ", pais=" + pais + ", codigo_postal=" + codigo_postal + ", telefono=" + telefono + ", correo=" + correo + '}';
    }

}
