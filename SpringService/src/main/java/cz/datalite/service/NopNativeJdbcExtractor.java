package cz.datalite.service;

import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

import java.sql.*;

public class NopNativeJdbcExtractor
    implements NativeJdbcExtractor
{
    @Override
    public boolean isNativeConnectionNecessaryForNativeStatements()
    {
        return false;
    }

    @Override
    public boolean isNativeConnectionNecessaryForNativePreparedStatements()
    {
        return false;
    }

    @Override
    public boolean isNativeConnectionNecessaryForNativeCallableStatements()
    {
        return false;
    }

    @Override
    public Connection getNativeConnection(Connection con) throws SQLException
    {
        return con ;
    }

    @Override
    public Connection getNativeConnectionFromStatement(Statement stmt) throws SQLException
    {
        return ( stmt != null ) ? stmt.getConnection() : null ;
    }

    @Override
    public Statement getNativeStatement(Statement stmt) throws SQLException
    {
        return stmt ;
    }

    @Override
    public PreparedStatement getNativePreparedStatement(PreparedStatement ps) throws SQLException
    {
        return ps;
    }

    @Override
    public CallableStatement getNativeCallableStatement(CallableStatement cs) throws SQLException
    {
        return cs;
    }

    @Override
    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException
    {
        return rs;
    }
}
