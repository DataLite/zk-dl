package cz.datalite.zk.components.list.filter;

import cz.datalite.zk.components.list.filter.compilers.FilterCompiler;
import cz.datalite.helpers.EqualsHelper;
import cz.datalite.zk.components.list.model.DLColumnUnitModel;
import java.util.List;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.components.FilterComponent;
import cz.datalite.zk.components.list.filter.components.FilterComponentFactory;
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
 * @author Karel Cemus
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
    
    /**
     * overriding property for filter compiler. This can be used when the
     * columnModel is not set or we want to override defined value. This
     * property was design to override compiler for QuickFilter#ALL option,
     * which has not columnModel and need specialized compiler.
     */
    protected FilterCompiler compiler;

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
        if ( operators.contains( operator ) )
            this.operator = operator;
        else if (!operators.isEmpty())
            this.operator = operators.get( 0 );
        else
            this.operator = null;
    }
    
    /** <p>Sets filter operator. If the force is false, the innernally is called 
     * {@link #setOperator(cz.datalite.zk.components.list.enums.DLFilterOperator) }.
     * If the force is true, then the operator is set nevertheless it is supported
     * by column model or event when it is not defined at all.</p>
     * 
     * <p>This setter was defined during ZK-161 changes to allow the quick filter ALL
     * option pass the tests. Otherwise this method shouldn't be used or by
     * expert users only!</p>
     * 
     * @param operator new operator
     * @param force don't check the support by columnModel. It can lead to the exception
     * because a compiler couldn't be able to process this operator with this entity class
     */
    public void setOperator( final DLFilterOperator operator, boolean force) {
        if (force) {
            this.operator = operator;
        } else {
            setOperator(operator);
        }
    }


    /**
     * 1-based index of operand (1 for unary and 1 and 2 for binary operators).
     *
     * @param index 1-based index
     * @return actual value
     */
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
        if (this.compiler == null) {
            // overriding compiler is not set, use default
            return columnModel == null ? null : columnModel.getFilterCompiler();
        } else {
            // overriding compiler is set, use it
            return this.compiler;
        }
    }
    
    public void setFilterCompiler( final FilterCompiler compiler) {
        this.compiler = compiler;
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
        setTemplate( templateModel );
        this.columnModel = templateModel.columnModel;
        this.property = templateModel.property;
        if ( !templateModel.getOperators().contains( operator ) || (value1 == null && value2 == null) ) {
            // if is selected unsupported operator
            // or if values are null then switch to default
            setDefaultOperator();
        }
        setOperator( operator );
    }

    public FilterComponentFactory getFilterComponentFactory() {
        return columnModel == null ? null : columnModel.getFilterComponentFactory();
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
}
