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

public class TipoDocumento {

    public int id;
    public boolean es_fiscal;
    public int tipo; // 0=salida, 1=entrada
    public String nombre;
    public String serie;
    public int folio;

    public TipoDocumento() {
        id = 0;
        es_fiscal = false;
        tipo = 0;
        nombre = "";
        serie = "";
        folio = 1;
    }

    /**
     * Guarda el producto en la DBMS. Si existe, lo reemplaza.
     */
    public boolean save() {
        try {
            // String $id = String.valueOf(this.id);
            if (this.id == 0) {
                this.id = TipoDocumento.getLastId();
            }

            int $es_fiscal = es_fiscal == true ? 1 : 0;

            String sql = String.format("insert or replace into tipos_documentos "
                    + "(id, es_fiscal, tipo, nombre, serie, folio) values "
                    + "(%d, %d, %d, '%s', '%s', %d)",
                    this.id, $es_fiscal, tipo, nombre, serie, folio);

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
            String sql = String.format("delete from tipos_documentos where id=%d", this.id);
            Statement s = Store.drv.createQuery();
            s.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return false;
    }

    private static TipoDocumento unserialize(ResultSet rs) throws SQLException {
        TipoDocumento p = new TipoDocumento();
        p.id = rs.getInt("id");
        p.es_fiscal = rs.getInt("es_fiscal") == 1;
        p.tipo = rs.getInt("tipo");
        p.nombre = rs.getString("nombre");
        p.serie = rs.getString("serie");
        p.folio = rs.getInt("folio");
        return p;
    }

    public static TipoDocumento findFirstById(int id) {
        try {
            String sql = String.format("select * from tipos_documentos where id=%d", id);
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            return TipoDocumento.unserialize(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    public static TipoDocumento findFirstBySerie(String serie) {
        try {
            String sql = String.format("select * from tipos_documentos where serie='%s'", serie);
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            return TipoDocumento.unserialize(rs);
        } catch (Exception e) {
            return null;
        }
    }

    public static Vector<TipoDocumento> find(String serie) {
        Vector<TipoDocumento> v = new Vector<TipoDocumento>();
        try {
            String sql = "select * from tipos_documentos where 1 ";

            if (serie != null && serie.length() > 0) {
                sql += String.format("and serie='%s'", serie);
            }

            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                v.add(TipoDocumento.unserialize(rs));
            }
        } catch (Exception e) {
            Store.error("Ha ocurrido un problema", e.getMessage());
        }
        return v;
    }

    public static int getLastId() throws SQLException {
        String sql = String.format("select id from tipos_documentos order by id desc limit 1");
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
        return "TipoDocumento{" + "id=" + id + ", es_fiscal=" + es_fiscal + ", tipo=" + tipo + ", nombre=" + nombre + ", serie=" + serie + ", folio=" + folio + '}';
    }


}
