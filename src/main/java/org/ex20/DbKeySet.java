package org.ex20;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * <p>创建时间: 2022年08月26日 14:23 </p>
 * 数据库中注解获取
 * @author 高诚政
 */
public class DbKeySet {
    public static ArrayList<String> keysSet(Connection conn){
        Statement statement;
        ResultSet resultSet;
        ArrayList<String> keys = new ArrayList<>();
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery("select a.constraint_name,  a.column_name " +
                    "from user_cons_columns a, user_constraints b " +
                    "where a.constraint_name = b.constraint_name " +
                    "and b.constraint_type = 'P' and a.table_name = '" + JdbcUtil.TABLENAME + "'");
            while (resultSet.next()) {
                keys.add(resultSet.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keys;
    }
}
