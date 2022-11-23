package org;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DBHelperPrivate {
    protected static Connection conn = null;

    public static Connection mysqlConnect() throws Exception {
        try {

            //LOCAL
            String url = "jdbc:mysql://127.0.0.1:3306/mishakim?useSSL=false&allowLoadLocalInfile=true";
            String user = "root";
            String password = "root";

//            //REMOTE
//            String url = "jdbc:mysql://191.96.56.154:3306/u204686394_mishakim?useSSL=false&allowLoadLocalInfile=true";
//            String user = "u204686394_mishakim";
//            String password = "Mishakim!@#$11";


            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new Exception("Not connected to DB: " + e);
        }
        return conn;
    }


    public static List<String> executeSelectQuery(String selectQuery, String columnLabel) throws SQLException {
        System.out.println("\nDB query: "+selectQuery+":\n");
        List lines;
        List lines_comp = new ArrayList();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(columnLabel));
                lines = new ArrayList();
                lines.add(resultSet.getString(columnLabel));
                lines_comp.add(lines.get(0));
            }
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (selectQuery.contains("*")) {
            System.out.println("(For column: " + columnLabel + ")");
        }else {
            System.out.println("\n");
        }
        return lines_comp;
    }

    public static void executeUpdate(String updateQuery) throws Exception {
        try {
            System.out.println(updateQuery);
            Statement statement = conn.createStatement();
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
            throw e;
        }
    }


    public static void mysqlConnectDisconnect() throws ClassNotFoundException {
        try {
            Thread.sleep(1000);
            conn.close();
        } catch (SQLException | InterruptedException e) {
            System.out.println("Disconnect DB failed");
            e.printStackTrace();
        }
    }
}
