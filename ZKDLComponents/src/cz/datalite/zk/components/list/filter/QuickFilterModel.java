package cz.datalite.zk.components.list.filter;

/**
 * Model for quick filter
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class QuickFilterModel {

    protected String key;
    protected String value;
    public static final String CONST_ALL = "ALL_FILTER_VALUES";

    public QuickFilterModel( final String key, final String value ) {
        assert key != null && key.length() > 0 : "Invalid argument. Key cannot be null or empty in quick filter.";
        this.key = key;
        this.value = value;
    }

    public QuickFilterModel( final String value ) {
        this.value = value;
        key = CONST_ALL;
    }

    public QuickFilterModel() {
        this.value = null;
        key = CONST_ALL;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey( final String key ) {
        this.key = key;
    }

    public void setValue( final String value ) {
        this.value = value;
    }

    public boolean isAllColumns() {
        return CONST_ALL.equals( key );
    }

    public boolean isEmpty() {
        return value == null || value.length() == 0;
    }

    public void clear() {
        key = CONST_ALL;
        value = null;
    }
}
