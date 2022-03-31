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

public class LobbyStats {

    public int ventas;
    public float total_ventas;
    public int compras;
    public float total_compras;
    public int cancelaciones;
    public float total_utilidades;

    public LobbyStats() {
        ventas = 0;
        total_ventas = 0.0f;
        compras = 0;
        total_compras = 0.0f;
        cancelaciones = 0;
        total_utilidades = 0.0f;
    }

    private static LobbyStats unserialize(ResultSet rs) throws SQLException {
        LobbyStats p = new LobbyStats();
        return p;
    }

    public static LobbyStats get() {
        try {
            
            String sql = "select count(*) as ventas, sum(total) "
                    + "as total_ventas from documentos as d\n"
                    + "left join tipos_documentos as td on "
                    + "td.id=d.id_tipo_documento where td.tipo=0 and d.id_status=1";
            Statement s = Store.drv.createQuery();
            ResultSet rs = s.executeQuery(sql);
            rs.next();
            LobbyStats p = new LobbyStats();
            p.ventas = rs.getInt("ventas");
            p.total_ventas = rs.getFloat("total_ventas");
            sql = "select count(*) as compras, sum(total) "
                    + "as total_compras from documentos as d\n"
                    + "left join tipos_documentos as td on "
                    + "td.id=d.id_tipo_documento where td.tipo=1 and d.id_status=1";
            s = Store.drv.createQuery();
            rs = s.executeQuery(sql);
            rs.next();
            p.compras = rs.getInt("compras");
            p.total_compras = rs.getFloat("total_compras");
            sql = "select count(*) as cancelaciones "
                    + "from documentos_productos "
                    + "where id_status=0 and tipo_inv=1";
            s = Store.drv.createQuery();
            rs = s.executeQuery(sql);
            rs.next();
            p.cancelaciones = rs.getInt("cancelaciones");
            
            sql = "select sum(utilidades) as total_utilidades "
                    + "from documentos_productos as d\n"
                    + "left join documentos as doc on "
                    + "doc.id = d.id_documento "
                    + "left join tipos_documentos as td on "
                    + "td.id=doc.id_tipo_documento where td.tipo=0 and doc.id_status=1";
            s = Store.drv.createQuery();
            rs = s.executeQuery(sql);
            rs.next();
            p.total_utilidades = rs.getInt("total_utilidades");
            return p;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
