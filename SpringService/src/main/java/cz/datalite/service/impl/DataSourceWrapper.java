package cz.datalite.service.impl;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

@SuppressWarnings("UnusedDeclaration")
public class DataSourceWrapper implements DataSource
{
    private AbstractDataSourceSwitcherService original ;
    private String database ;

    DataSourceWrapper( AbstractDataSourceSwitcherService original, String database )
    {
        this.original = original;
        this.database = database ;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return original.getConnection( database ) ;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException
    {
        return original.getConnection( this.database, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        return original.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException
    {
        original.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException
    {
        original.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException
    {
        return original.getLoginTimeout();
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return original.getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        return original.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        return original.isWrapperFor(iface);
    }
}
