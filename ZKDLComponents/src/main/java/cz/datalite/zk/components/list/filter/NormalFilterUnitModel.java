package cz.datalite.zk.components.list.filter;

import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.helpers.EqualsHelper;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import java.util.List;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.components.FilterComponent;
import java.util.Collections;

/**
 * Model for the normal filter - other filter models can be converted to
 * this type.
 *
 * This model is based on columnModel, which defines many properties which can
 * be taken from the ZUL files.
 *
 * To eliminate the duplicate records and properties there are direct link to
 * that model which make simpler extension in the future. To make this extension
 * there is needed to add only getter and thats all.
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class NormalFilterUnitModel implements Cloneable {

    /** column configuration */
    protected DLColumnUnitModel columnModel;
    /** can be defined instead of column model */
    protected String property;
    /** filter operator */
    protected DLFilterOperator operator;
    /** filter values */
    protected Object value1;
    protected Object value2;
    /** defines selected template in the filter. It is used to looking for 
     * selected value in filter comboboxes. */
    protected NormalFilterUnitModel template;

    public NormalFilterUnitModel() {
        // creates new empty unit
    }

    /**
     * This constructor is used to clone the object
     * @param unitModel
     */
    protected NormalFilterUnitModel( final NormalFilterUnitModel unitModel ) {
        this.columnModel = unitModel.columnModel;
        this.operator = unitModel.operator;
        this.value1 = unitModel.value1;
        this.value2 = unitModel.value2;
        this.property = unitModel.property;
    }

    public NormalFilterUnitModel( final DLColumnUnitModel columnModel ) {
        this.columnModel = columnModel;
        this.property = columnModel.getColumn();
    }

    public NormalFilterUnitModel( final String property ) {
        this.property = property;
    }

    public DLFilterOperator getOperator() {
        return operator;
    }

    public void setOperator( final DLFilterOperator operator ) {
        final List<DLFilterOperator> operators = getOperators();
        if ( operators.contains( operator ) ) {
            this.operator = operator;
        } else {
            this.operator = operators.get( 0 );
        }
    }

    public Object getValue( final int index ) {
        switch ( index ) {
            case 1:
                return value1;
            case 2:
                return value2;
            default:
                throw new IndexOutOfBoundsException( "Index: " + index + " is not allowed for the normal filter." );
        }
    }

    public void setValue( final int index, final Object value ) {
        switch ( index ) {
            case 1:
                value1 = value;
                break;
            case 2:
                value2 = value;
                break;
            default:
                throw new IndexOutOfBoundsException( "Index: " + index + " is not allowed for the normal filter." );
        }
    }

    public List<DLFilterOperator> getOperators() {
        if ( columnModel == null ) {
            return Collections.emptyList();
        } else {
            return columnModel.getFilterOperators();
        }
    }

    public FilterComponent createFilterComponent() {
        return columnModel == null ? null : columnModel.createFilterComponent();
    }

    public Class getTypeOfFilterComponent() {
        return columnModel == null ? null : columnModel.getTypeOfFilterComponent();
    }

    public FilterCompiler getFilterCompiler() {
        return columnModel == null ? null : columnModel.getFilterCompiler();
    }

    public String getColumn() {
        return property;
    }

    public Class getType() {
        return columnModel == null ? null : columnModel.getColumnType();
    }

    public String getLabel() {
        return columnModel == null ? null : columnModel.getLabel();
    }

    public DLFilterOperator getQuickFilterOperator() {
        return columnModel == null ? null : columnModel.getQuickFilterOperator();
    }

    public NormalFilterUnitModel getTemplate() {
        return template;
    }

    public void setTemplate( final NormalFilterUnitModel template ) {
        this.template = template;
    }

    public DLColumnUnitModel getColumnModel() {
        return columnModel;
    }

    @Override
    public NormalFilterUnitModel clone() {
        return new NormalFilterUnitModel( this );
    }

    public void update( final NormalFilterUnitModel templateModel ) {
        if ( columnModel == null || !EqualsHelper.isEquals( columnModel.getTypeOfFilterComponent(), templateModel.getTypeOfFilterComponent() ) ) {
            value1 = null;
            value2 = null;
        }
        this.columnModel = templateModel.columnModel;
        this.property = templateModel.property;
        if ( !templateModel.getOperators().contains( operator ) || (value1 == null && value2 == null) ) {
            // if is selected unsupported operator
            // or if values are null then switch to default
            setDefaultOperator();
        }
        setOperator( operator );
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( !EqualsHelper.isSameClass( this, obj ) ) {
            return false;
        }
        final NormalFilterUnitModel unit = ( NormalFilterUnitModel ) obj;
        if ( !EqualsHelper.isEqualsNull( this.property, unit.property )
                || !EqualsHelper.isEqualsNull( this.operator, unit.operator )
                || !EqualsHelper.isEqualsNull( this.value1, unit.value1 )
                || !EqualsHelper.isEqualsNull( this.value2, unit.value2 ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 3
                + (property == null ? 11 : property.hashCode())
                + (value1 == null ? 5 : value1.hashCode())
                + (value2 == null ? 7 : value2.hashCode());
    }

    /**
     * Tries to set the default operator for this type of value
     */
    public void setDefaultOperator() {
        if ( getQuickFilterOperator() != null ) { // prefer default
            if ( getOperators().contains( getQuickFilterOperator() ) ) {
                this.operator = getQuickFilterOperator();
            } else {
                this.operator = getOperators().get( 0 );
            }
        }
    }
}
