package cz.datalite.zk.components.image;

import org.zkoss.zul.Image;

/**
 * ZK Image extensions.
 *
 * List of extensions:
 * <ul>
 *    <li>Assign arbitrary value to an image (use in list to hand over selected record)</li>
 * </ul>
 *
 * @author Jiri Bubnik <bubnik at datalite.cz>
 */
public class DLImage extends Image {

    /**
     * Arbitrary value associated to the image.
     */
    private Object value;

    /**
     * Get arbitrary value associated to the image.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set arbitrary value associated to the image.
     */
    public void setValue(Object value) {
        this.value = value;
    }


}
