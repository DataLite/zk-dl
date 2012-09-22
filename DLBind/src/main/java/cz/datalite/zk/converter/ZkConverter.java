package cz.datalite.zk.converter;

/**
 * Converter adapter above real ZK implementations like {@link org.zkoss.bind.Converter},
 * {@link org.zkoss.zkplus.databind.TypeConverter}, {@link MethodConverter} etc.
 *
 * @author Karel Cemus
 */
public interface ZkConverter {

    /**
     * Converts model value to value in view
     *
     * @param model value in model
     * @return value in view
     */
    Object convertToView( Object model );
}
