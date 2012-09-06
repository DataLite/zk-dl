package cz.datalite.zk.liferay;

import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.Locale;

/**
 * Wrapper for Liferay localized exceptions.
 * getMessage() returns localized exception
 *
 * @author Jiri Bubnik
 */
public class LiferayLocalizedException extends Exception
{
    String key;
    
    public LiferayLocalizedException(Locale locale, String key, Exception originalException)
    {
        super(LanguageUtil.get(locale, key), originalException);

        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
