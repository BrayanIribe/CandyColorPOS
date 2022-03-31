/*
 * Driver usado para acceder a la DB de forma programable
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Ivy
 */
public class Driver {

    public Connection link = null;
    public boolean setupRequired = false;

    public Driver() throws SQLException {
        link = DriverManager.getConnection("jdbc:sqlite:app.db");
    }

    public Statement createQuery() throws SQLException {
        Statement s = link.createStatement();
        s.setQueryTimeout(10);
        return s;
    }

    private boolean tableExists(String name) throws SQLException {
        Statement s = link.createStatement();
        ResultSet rs = s.executeQuery("SELECT count(*) as c FROM sqlite_master WHERE type='table' AND name='" + name + "';");
        rs.next();
        return rs.getInt("c") > 0;
    }

    /**
     * Crea las tablas, de ser necesario para hacer funcionar el sistema
     */
    public void createTables() throws SQLException {
        Statement statement = link.createStatement();
        statement.setQueryTimeout(30);

        // crear tabla de productos
        statement.executeUpdate("create table if not exists productos "
                + "(id integer primary key, existencia float, descripcion string,"
                + "codigo_barras string, costo float, precio float, "
                + "materia_prima integer, updatedAt integer, createdAt integer)");
        
        Producto p = Producto.findFirstById(1);
        if (p == null){
            p = new Producto();
            p.id = 1;
            p.descripcion = "Articulo de prueba";
            p.existencia = 0;
            p.precio = 20;
            p.costo = 10;
            p.codigo_barras = "123";
            p.materia_prima = false;
            p.save();
        }

        // crear tabla de clientes_proveedores
        statement.executeUpdate("create table if not exists clientes "
                + "(id integer primary key, nombre string,"
                + "rfc string, referencia string, calle string, num string,"
                + "colonia string, municipio string, estado string, pais string,"
                + "codigo_postal integer, telefono string, correo string,"
                + "updatedAt integer, createdAt integer)");

        // crear cliente publico en general, en caso de no existir
        Cliente cliente = Cliente.findFirstById(1);
        if (cliente == null || cliente.rfc != "XAXX0101000") {
            cliente = new Cliente();
            cliente.id = 1;
            cliente.nombre = "Público en general";
            cliente.rfc = "XAXX0101000";
            cliente.save();
        }

        // crear tabla de tipos_documentos
        statement.executeUpdate("create table if not exists tipos_documentos "
                + "(id integer primary key, es_fiscal tinyint,"
                + "tipo tinyint, nombre string, serie string, folio integer,"
                + "updatedAt integer, createdAt integer)");

        // crear documentos, en caso de no existir
        TipoDocumento remision = TipoDocumento.findFirstBySerie("R");
        if (remision == null) {
            remision = new TipoDocumento();
            remision.es_fiscal = false;
            remision.folio = 1;
            remision.nombre = "Remision";
            remision.serie = "R";
            remision.tipo = 0;
            remision.save();
        }

        TipoDocumento compra = TipoDocumento.findFirstBySerie("C");
        if (compra == null) {
            compra = new TipoDocumento();
            compra.es_fiscal = false;
            compra.folio = 1;
            compra.nombre = "Compra";
            compra.serie = "C";
            compra.tipo = 1;
            compra.save();
        }

        // crear tabla de documentos
        statement.executeUpdate("create table if not exists documentos "
                + "(id integer primary key, id_status tinyint,"
                + "id_cliente_proveedor integer, id_tipo_documento integer, folio integer,"
                + "total float, efectivo float, tipo_cambio float)");

        statement.executeUpdate("create table if not exists documentos_productos "
                + "(id integer primary key, id_status tinyint, es_salida tinyint,"
                + "id_documento integer, id_producto integer, descripcion string,"
                + "cantidad float, costo float, precio float, impuestos float,"
                + "utilidades float, existencia float)");
    }
}
