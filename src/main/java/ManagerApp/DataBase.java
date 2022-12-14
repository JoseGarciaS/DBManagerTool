/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ManagerApp;

import java.util.Properties;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author AORUS
 */
public class DataBase {

    private String path;
    private String alias;

    private Properties props;

    public DataBase(String path, String name, String user, String password) {
        props = new Properties();

        this.path = path;
        this.alias = name;

        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("encoding", "UTF8");

        // <editor-fold defaultstate="collapsed" desc="Trash Code">
        /* 
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:firebirdsql:"+path+name,
                    props);
            Statement declara;
            declara = connection.createStatement();
            
            ResultSet res = declara.executeQuery("""
                                                 select rdb$relation_name
                                                 from rdb$relations
                                                 where rdb$view_blr is null
                                                 and (rdb$system_flag is null or rdb$system_flag = 0);
                                                 """);
            int cont = 0;
            while (res.next()) {
                cont++;
                System.out.println(res.getNString(1));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println(ex.toString());
        } 
         */
        // </editor-fold>
    }
    
    public void create() throws SQLException {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            Connection connection = DriverManager.getConnection("jdbc:firebirdsql://localhost:3050/C:/Archivos de Prgrama/Firebird/Firebird_4_0/security4.fdb", "SYSDBA", "masterkey");
            String dir = path.substring(15);
            Statement declara = connection.createStatement();
            declara.executeUpdate("CREATE DATABASE '"+dir+"';");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        Connection connection = DriverManager.getConnection(
                "jdbc:firebirdsql://" + path,
                props);
        return connection;
    }

    public ResultSet query(String consulta) throws SQLException {
        try {
            Connection con = getConnection();
            Statement declara;
            declara = con.createStatement();
            ResultSet respuesta = declara.executeQuery(consulta);
            return respuesta;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error de Conexion", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void execute(String query) throws SQLException {
        try {
            Connection con = getConnection();
            Statement declara = con.createStatement();
            declara.execute(query);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeUpdate(String query) throws SQLException {
        try {
            Connection con = getConnection();
            Statement declara = con.createStatement();
            declara.executeUpdate(query);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPath() {
        return path;
    }

    public String getAlias() {
        return alias;
    }

    public Properties getProps() {
        return props;
    }
}
