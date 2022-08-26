package org.ex20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>创建时间: 2022年08月26日 14:18 </p>
 * 数据库连接
 * @author 高诚政
 */
public class JdbcUtil {
    private static String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static String URL = "jdbc:oracle:thin:@//sx.qcerp.com.cn:1521/qc";
    private static String USERNAME = "demo_001";
    private static String PASSWORD = "123456";
    public static String TABLENAME = "SOD_DET";
    public static String FileName = "Template";
    public static final String SQL = "SELECT * FROM ";// 数据库操作
    static {
        try {
            Properties properties = new Properties();
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new FileReader("application-db.properties"));
            properties.load(bufferedReader);
            DRIVER = properties.getProperty("db.driver");
            URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
            TABLENAME = properties.getProperty("tablename");
            FileName = properties.getProperty("filename");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class.forName(DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("get connection failure " + e);
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
