package longa.domains.sql.tools;

import com.sun.rowset.CachedRowSetImpl;
import longa.domains.sql.models.ConnectionModel;

import java.sql.*;

public class MySQLHelper {
    public static ConnectionModel connection;
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   // static final String DB_URL = "jdbc:mysql://localhost:3306/testdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static int executeUpdate(String sql) {
        if(connection==null)
        {
            System.err.println("you must set connection with 'connection_chain' before access database ");
            return -1;
        }

       int count=-1;
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(connection.toUrl(), connection.user, connection.password);
            stmt = conn.createStatement();
            count = stmt.executeUpdate(sql);
            close(stmt);
            close(conn);
            return count;
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            close(stmt);
            close(conn);
        }
        return count;
    }

    public static CachedRowSetImpl executeQuery(String sql) {
        if(connection==null)
        {
            System.err.println("you must set connection with 'connection_chain' before access database ");
            return null;
        }
        ResultSet rs = null;
        CachedRowSetImpl rowset = null;
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER);

            conn = DriverManager.getConnection(connection.toUrl(), connection.user, connection.password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            rowset=new CachedRowSetImpl();
            rowset.populate(rs);
            rs.close();
            close(stmt);
            close(conn);
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        }
        finally
        {
            close(stmt);
            close(conn);
        }
        return rowset;
    }

    public static void close(Statement stmt )
    {
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void close(Connection conn)
    {
        try {
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
