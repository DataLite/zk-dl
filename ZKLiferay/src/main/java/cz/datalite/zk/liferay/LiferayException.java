package cz.datalite.zk.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * Wrapper for Liferay system and portal exceptions - to make them runtime exceptions.
 *
 * @author Jiri Bubnik
 */
public class LiferayException extends RuntimeException
{
    public LiferayException(String message)
    {
        super(message);
    }

    public LiferayException(SystemException e)
    {
        super(e);
    }

    public LiferayException(PortalException e)
    {
        super(e);
    }

    public LiferayException(String message, SystemException e)
    {
        super(message, e);
    }

    public LiferayException(String message, PortalException e)
    {
        super(message, e);
    }

    @Override
    public String getMessage() {
        if (getCause() != null)
            return super.getMessage() + " - " + getCause().toString();
        else
            return super.getMessage();
    }
}
