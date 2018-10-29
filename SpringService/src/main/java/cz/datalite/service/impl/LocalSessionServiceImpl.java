package cz.datalite.service.impl;

import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.StringHelper;
import cz.datalite.service.LocalSessionService;
import cz.datalite.stereotype.Service;
import cz.datalite.time.DateTimeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Služba pro získání informace o aktuálních lokalních operací
 */
@Service
public class LocalSessionServiceImpl implements LocalSessionService
{
    private final static String NULL_VALUE = "__NULL__" ;
    private Map<String, String> information = new ConcurrentHashMap<>() ;
    private Map<String, Long> startTime = new ConcurrentHashMap<>() ;
    private String id ;

    @Override
    public String getSessionInformation( String id )
    {
        return information.get(id) ;
    }

    @Override
    public String getCurrentSessionId()
    {
        return StringHelper.nvl(id, ObjectHelper.extractString( Thread.currentThread().getId())) ;
    }

    @Override
    public synchronized void setSessionInformation(String clientInfo)
    {
        setSessionInformation( getCurrentSessionId(), clientInfo ) ;
    }

    @Override
    public void setSessionInformation(String sessionId, String clientInfo)
    {
        if ( StringHelper.isNull( sessionId ) )
        {
            information.put( NULL_VALUE, StringHelper.nvl(clientInfo, ""));
        }
        else
        {
            information.put(sessionId, StringHelper.nvl(clientInfo, ""));
        }
    }

    @Override
    public synchronized void clearSessionInformation()
    {
        information.remove( getCurrentSessionId() ) ;
        startTime.remove( getCurrentSessionId() ) ;
        id = null ;
    }

    @Override
    public void setCurrentSessionId(String id)
    {
        this.id = id ;
    }

    @Override
    public void setSessionInformationWithTime(String sessionId, String format, int current, int total, String... args)
    {
        if ( ! startTime.containsKey( sessionId ) ) {
            startTime.put( sessionId, System.currentTimeMillis() ) ;
        }

        long duration = System.currentTimeMillis() - startTime.get( sessionId ) ;
        String procentoD = DateTimeUtil.formatDuration( duration, false, true )  ;
        String procentoT = "" ;

        if ( current > 0 ) {
            long total_duration = total * duration / current ;

            procentoT = DateTimeUtil.formatDuration( total_duration, false, true ) ;
        }

        double p = 0 ;

        if ( total != 0 ) {
            p = ((double)( 100 * current )) / ((double)total) ;

        }

        String messageFormat = format
                                    .replace( "%D", procentoD )
                                    .replace( "%T", procentoT )
                                    .replace( "%C", "" + current )
                                    .replace( "%S", "" + total )
                                    .replace( "%P", "" + ( Math.round(p*100)/100 ) + "%%" )
        ;

        setSessionInformation( sessionId, String.format( messageFormat, (Object[]) args) ) ;
    }

    @Override
    public void setSessionInformationWithTime(String format, int current, int total, String... args)
    {
        setSessionInformationWithTime( getCurrentSessionId(), format, current, total, args ) ;
    }
}
