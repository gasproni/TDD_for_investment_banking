package com.asprotunity.dbtools;

import com.asprotunity.fileio.FileReader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLLoader {

    public static void executeSQLStatements(Connection connection, String sql) throws IOException, SQLException {
        String[] statements = sql.split(";");

        for (String sqlStatement : statements) {
            Statement statement = connection.createStatement();
            statement.execute(sqlStatement);
            statement.close();
        }
        connection.commit();
    }


    public static void main(String[] args) throws IOException, SQLException {

        String jdbcString = args[0];
        String sqlFileName = args[1];
        String userName = args[2];
        String password = args[3];

        String sql = FileReader.readFile(sqlFileName);

        Connection connection = DriverManager.getConnection(jdbcString, userName, password);

        executeSQLStatements(connection, sql);

    }
}
