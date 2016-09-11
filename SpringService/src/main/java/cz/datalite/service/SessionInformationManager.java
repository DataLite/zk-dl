package cz.datalite.service;


import cz.datalite.service.impl.LocalSessionServiceImpl;

@SuppressWarnings("unused")
public class SessionInformationManager implements SessionInformation
{
    /**
     * Aktuální instance
     */
    private static SessionInformationManager instance ;

    /**
     * Implementace informovače
     */
    private SessionInformation sessionInformation ;

    public SessionInformationManager()
    {
        instance = this ;
    }

    /**
     * @return aktuální instance informovovače
     */
    public static SessionInformation getInstance()
    {
        if ( instance == null )
        {
             instance = new SessionInformationManager() ;
        }

        return instance ;
    }

    protected SessionInformation getSessionInformationProvider()
    {
        if ( sessionInformation == null )
        {
            sessionInformation = new LocalSessionServiceImpl();
        }

        return sessionInformation ;
    }

    @Override
    public String getSessionInformation(String id)
    {
        return getSessionInformationProvider().getSessionInformation(id);
    }

    @Override
    public String getCurrentSessionId()
    {
        return getSessionInformationProvider().getCurrentSessionId();
    }

    @Override
    public void setSessionInformation(String clientInfo)
    {
        getSessionInformationProvider().setSessionInformation(clientInfo);
    }

    @Override
    public void clearSessionInformation()
    {
        getSessionInformationProvider().clearSessionInformation();
    }

    @Override
    public void setSessionInformation(String sessionId, String clientInfo)
    {
        getSessionInformationProvider().setSessionInformation(sessionId, clientInfo);
    }

    @Override
    public void setCurrentSessionId(String id)
    {
        getSessionInformationProvider().setCurrentSessionId(id);
    }
}
