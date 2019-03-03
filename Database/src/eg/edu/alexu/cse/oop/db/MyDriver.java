package eg.edu.alexu.cse.oop.db;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Mohamed Abdelrehim on 4/28/2017.
 */
public class MyDriver implements Driver {
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        try {
            URL url1 = new URL(url);
            Path path = Paths.get(url1.toURI());
            Connection connection = new MyConnection(path);
            return connection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
