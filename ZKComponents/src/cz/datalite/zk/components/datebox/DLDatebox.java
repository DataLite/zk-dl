package cz.datalite.zk.components.datebox;

import org.zkoss.zul.Datebox;

/**
 * ZK Datebox extensions.
 *
 * List of extensions:
 * <ul>
 *   <li>Readonly on datebox should hide button as well.</li>
 * </ul>

 * @author Jiri Bubnik
 */
public class DLDatebox extends Datebox {

    /**
     * Readonly on datebox should hide button as well.
     *
     * @param readonly
     */
    @Override
    public void setReadonly( final boolean readonly ) {
        super.setReadonly( readonly );
        setButtonVisible( !readonly );
    }
}
