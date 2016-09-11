package cz.datalite.service.impl;

import cz.datalite.dao.plsql.helpers.ObjectHelper;
import cz.datalite.helpers.StringHelper;
import cz.datalite.service.LocalSessionService;
import cz.datalite.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Služba pro získání informace o aktuálních lokalních operací
 */
@Service
public class LocalSessionServiceImpl implements LocalSessionService
{
    private Map<String, String> information = new HashMap<>() ;
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
        information.put(getCurrentSessionId(), clientInfo) ;
    }

    @Override
    public void setSessionInformation(String sessionId, String clientInfo)
    {
        information.put(sessionId, clientInfo) ;
    }

    @Override
    public synchronized void clearSessionInformation()
    {
        information.remove( getCurrentSessionId() ) ;
        id = null ;
    }

    @Override
    public void setCurrentSessionId(String id)
    {
        this.id = id ;
    }
}
