package cz.datalite.dao.plsql;

import cz.datalite.stereotype.Autowired;
import cz.datalite.stereotype.DAO;
import org.springframework.jdbc.core.DisposableSqlTypeValue;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

import java.io.InputStream;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: karny
 * Date: 11/8/12
 * Time: 2:04 PM
 */
@DAO
public class SqlLobValueFactory
{
    @Autowired
    LobHandler lobHandler ;

    public DisposableSqlTypeValue createLobValue( Object content )
    {
            return new SqlLobValue( content, lobHandler.getLobCreator() ) ;
    }

    class SqlLobValue implements DisposableSqlTypeValue
    {
            public void cleanup()
            {
                this.creator.close() ;
            }

            /**
             * Vytvarec lob hodnoty
             */
            private final LobCreator creator ;

            /**
             * Hodnota pro prevod na LOB
             */
            private Object content ;

            /**
             * Velikost dat
             */
            private int length ;


            /**
             * @param content       hodnota pro prevod
             */
            public SqlLobValue( Object content, LobCreator creator )
            {
                this.content = content;
                this.creator = creator ;

                if ( content instanceof byte[] )
                {
                    this.length = ((byte[])content).length ;
                }
                else if ( content instanceof String )
                {
                    this.length = ((String)content).length() ;
                }
                else if ( content == null )
                {
                    this.length = 0 ;
                }
                else
                {
                    throw new IllegalArgumentException( "Nepodporovaný type hodnoty [" + this.content.getClass().getName() + "]" ) ;
                }
            }

        public void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, String typeName) throws SQLException
        {
            if ( sqlType == Types.BLOB )
            {
                if ( ( this.content instanceof byte[] ) || ( this.content == null ) )
                {
                        this.creator.setBlobAsBytes(ps, paramIndex, (byte[]) this.content) ;
                }
                else if ( this.content instanceof String )
                {
                        this.creator.setBlobAsBytes(ps, paramIndex, ((String) this.content).getBytes()) ;
                }
                else if ( this.content instanceof InputStream)
                {
                        this.creator.setBlobAsBinaryStream(ps, paramIndex, (InputStream) this.content, this.length) ;
                }
                else
                {
                    throw new IllegalArgumentException( "Nepodporovaný type hodnoty [" + this.content.getClass().getName() + "]" ) ;
                }
            }
            else if (sqlType == Types.CLOB)
            {
                if ( ( this.content instanceof String ) || ( this.content == null ) )
                {
                        this.creator.setClobAsString(ps, paramIndex, (String) this.content) ;
                }
                else if ( this.content instanceof InputStream )
                {
                        this.creator.setClobAsAsciiStream(ps, paramIndex, (InputStream) this.content, this.length) ;
                }
                else if ( this.content instanceof Reader )
                {
                    this.creator.setClobAsCharacterStream(ps, paramIndex, (Reader) this.content, this.length ) ;
                }
                else
                {
                    throw new IllegalArgumentException( "Nepodporovaný type hodnoty [" + this.content.getClass().getName() + "]" ) ;
                }
            }
            else
            {
                    throw new IllegalArgumentException("Špatný typ hodnoty. Podporované typu jsou CLOB nebo BLOB" ) ;
            }
        }
    }
}

