package org.ex20;
import java.sql.*;
/**
 * <p>创建时间: 2022年08月24日 17:21 </p>
 * PowerBuilder DataWindow源码生成
 * @author 高诚政
 */
public class DatabaseUtil {

    public static void main(String[] args) {
        /* 获取连接 */
        Connection conn = JdbcUtil.getConnection();
        DataWindow.DataWindowGuid(conn);// 生成Guid风格的数据窗口
    }
}
