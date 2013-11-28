package com.asprotunity.exchange.server;

import com.asprotunity.dbtools.SQLLoader;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class HSQLEventStoreTest {

    private HSQLEventStore store;
    private Connection connection;
    private SecurityData securityData;

    @Before
    public void setUp() throws SQLException, IOException {

        connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
        SQLLoader.executeSQLStatements(connection, com.asprotunity.fileio.FileReader.readFile("src/main/sql/EventStore.sql"));
        store = new HSQLEventStore(connection);

        DateTime timestamp = makeUTCTimestamp();
        int spot = 122;
        double volatility = 0.3;
        String currency = "USD";
        String security = "TIK_1";

        securityData = new SecurityData(timestamp, security, currency, spot, volatility);

    }


    @After
    public void tearDown() throws SQLException, IOException {
        SQLLoader.executeSQLStatements(connection, "SHUTDOWN");
        connection.close();
    }


    @Test
    public void storesSecurityDataCorrectly() throws Exception {

        store.store(securityData);

        SecurityData loadedData = queryData(securityData.security);

        assertThat(securityData, is(equalTo(loadedData)));

    }

    @Test
    public void queriesLatestDataForSecurity() {

        store.store(securityData);

        SecurityData newSecurityData = new SecurityData(securityData.timestamp.plusMillis(5),
                securityData.security,
                securityData.currency,
                securityData.spot,
                securityData.volatility);
        store.store(newSecurityData);

        SecurityData loadedData = store.queryLatest(securityData.security);

        assertThat(loadedData, is(equalTo(newSecurityData)));

    }

    @Test
    public void returnsNullIfNoDataForSecurity() {

        SecurityData loadedData = store.queryLatest(securityData.security);
        assertThat(loadedData, is(nullValue()));

    }


    private SecurityData queryData(String security) throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery(String.format("select creationTime, security, currency, spot, volatility " +
                " from SecurityData where security = '%s'", security));
        try {
            rs.next();

            return new SecurityData(new DateTime(rs.getString(1), DateTimeZone.UTC), rs.getString(2),
                    rs.getString(3), rs.getDouble(4), rs.getDouble(5));
        } finally {
            rs.close();
            statement.close();
            connection.rollback();
        }

    }

    private DateTime makeUTCTimestamp() {
        return DateTime.now(DateTimeZone.UTC);
    }

}
