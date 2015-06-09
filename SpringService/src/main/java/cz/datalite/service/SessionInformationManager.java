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
        sessionInformation = new LocalSessionServiceImpl() ;
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

    @Override
    public String getSessionInformation(String id)
    {
        return sessionInformation.getSessionInformation(id);
    }

    @Override
    public String getCurrentSessionId()
    {
        return sessionInformation.getCurrentSessionId();
    }

    @Override
    public void setSessionInformation(String clientInfo)
    {
        sessionInformation.setSessionInformation(clientInfo);
    }

    @Override
    public void clearSessionInformation()
    {
        sessionInformation.clearSessionInformation();
    }

    @Override
    public void setSessionInformation(String sessionId, String clientInfo)
    {
        sessionInformation.setSessionInformation(sessionId, clientInfo);
    }

    @Override
    public void setCurrentSessionId(String id)
    {
        sessionInformation.setCurrentSessionId(id);
    }
}
