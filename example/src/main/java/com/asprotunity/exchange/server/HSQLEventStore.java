package com.asprotunity.exchange.server;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;

public class HSQLEventStore implements EventStore {

    private Connection connection;

    public HSQLEventStore(String jdbcConnection, String userName, String password) throws SQLException {
        this(DriverManager.getConnection(jdbcConnection, userName, password));
    }


    public HSQLEventStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SecurityData queryLatest(String security) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();

            rs = statement.executeQuery(String.format("select top 1 creationTime, security, currency, spot, volatility " +
                    " from SecurityData where security = '%s' order by creationTime desc", security));

            if (rs.next()) {

                return new SecurityData(new DateTime(rs.getString(1), DateTimeZone.UTC), rs.getString(2),
                        rs.getString(3), rs.getDouble(4), rs.getDouble(5));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                connection.rollback();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void store(SecurityData data) {
        try {
            Statement statement = connection.createStatement();

            statement.execute(String.format("insert into SecurityData " +
                    "(creationTime, security, spot, volatility, currency)" +
                    " VALUES ('%s', '%s', %f, %f, '%s')",
                    data.timestamp.toString(), data.security, data.spot, data.volatility, data.currency));
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException(e);
        }
    }
}
