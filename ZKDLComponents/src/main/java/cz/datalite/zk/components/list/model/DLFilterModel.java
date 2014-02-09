package cz.datalite.zk.components.list.model;

import cz.datalite.helpers.TypeConverter;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.EasyFilterMapKeyValue;
import cz.datalite.zk.components.list.filter.EasyFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterModel;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.list.filter.QuickFilterModel;
import org.zkoss.lang.Library;

/**
 * Filter model - groups all filter models.
 * @author Karel Cemus
 */
public class DLFilterModel {

    /** property describing behavior of the quick filter - it says to use contains operator only */
    protected static final boolean USE_WYSIWYG = "true".equalsIgnoreCase( Library.getProperty( "zk-dl.filter.wysiwyg" ) );
    
    /** easy filter - binding */
    protected final EasyFilterModel _easy = new EasyFilterModel();
    
    /** quick filter - component based on hbox */
    protected final QuickFilterModel _quick = new QuickFilterModel();
    
    /** normal filter - modal window */
    protected final NormalFilterModel _normal = new NormalFilterModel();
    
    /** default filter - model */
    protected final NormalFilterModel _default = new NormalFilterModel();
    
    /** ZK-164 It says that the QuickFilter should use the Contains operator for quick filter base comparison */
    private Boolean wysiwyg = null;

    public NormalFilterModel getDefault() {
        return _default;
    }

    public EasyFilterModel getEasy() {
        return _easy;
    }

    public NormalFilterModel getNormal() {
        return _normal;
    }

    public QuickFilterModel getQuick() {
        return _quick;
    }

    public void clear() {
        _easy.clear();
        _normal.clear();
        _quick.clear();
    }

    public NormalFilterModel toNormal( final DLColumnModel columnModel ) {
        final NormalFilterModel normalFilterModel = new NormalFilterModel( _normal );
        normalFilterModel.addAll( _default );
        normalFilterModel.addAll( compileEasyFilter( _easy, columnModel ) );
        normalFilterModel.addAll( compileQuickFilter( _quick, columnModel ) );
        return normalFilterModel;
}

    /**
     * Converts easy filter to the normal filter
     * @param easyFilter easy filter model
     * @return normal filter model
     */
    protected NormalFilterModel compileEasyFilter( final EasyFilterModel easyFilter, final DLColumnModel columnModel ) {
        final NormalFilterModel compiledEasyFilter = new NormalFilterModel();
        for ( DLFilterOperator operator : DLFilterOperator.values() ) {
            final EasyFilterMapKeyValue filterMap = easyFilter.get( operator );
            for ( String key : filterMap.keySet() ) {
                final Object value = filterMap.get( key );
                if ( value == null ) {
                    continue;
                }
                key = key.replace( "#", "." );  // replacement because of definition in the zul - there cannot be used "."
                final DLColumnUnitModel column = columnModel.getByName( key );
                final NormalFilterUnitModel unit = column == null ? new NormalFilterUnitModel( key ) : new NormalFilterUnitModel( column );
                unit.setOperator( operator, true );
                unit.setValue( 1, value );
                compiledEasyFilter.add( unit );
            }
        }
        return compiledEasyFilter;
    }

    /**
     * Converts quick filter model to the normal filter model. There is problem
     * because keyword ALL is not supported.
     * @param quickFilter quick filter model
     * @return normal filter model
     */
    protected NormalFilterModel compileQuickFilter( final QuickFilterModel quickFilter, final DLColumnModel columnModel ) {
        if ( quickFilter.getValue() == null || quickFilter.getValue().length() == 0 ) {
            return new NormalFilterModel();
        }
        
        final DLColumnUnitModel column =  quickFilter.getModel();
        final NormalFilterUnitModel unit = column == null ? new NormalFilterUnitModel( quickFilter.getKey() ) : new NormalFilterUnitModel( column );
        if ( QuickFilterModel.CONST_ALL.equals( quickFilter.getKey() ) ) { // quickfilter key == ALL
            unit.setValue( 1, quickFilter.getValue() );
            // ZK-161 this is hack to support filter operator ALL in DLFilter which runs without 
            // an access to a database. Since now the filter wasn't able to support this operator
            // because there wasn't column model so filter processor didn't know what the ALL
            // means. Due to that the whole model si send as a 2nd parameter to allow filter
            // get what the All means. This propagation of inner model out is potentially dangerous
            // and shoudl be fixed in newer versions after refactoring
            unit.setValue(2, columnModel.getColumnModels());
        } else if ( unit.getFilterCompiler() != null ) { // has own compiler
            unit.setValue( 1, quickFilter.getValue() );
            unit.setOperator( unit.getQuickFilterOperator( quickFilter.getValue() ) );
        } else if ( column == null ) { // quickfilter column model not found
            throw new IllegalStateException( "Column "
                    + quickFilter.getKey()
                    + " was not found in column model. This can happen when column is java primitive type (filter by primitive is not supported)." );
        } else { // standard case
            unit.setOperator( column.getQuickFilterOperator( quickFilter.getValue()  ), true );
            final Object value = column.getColumnType() == null ? quickFilter.getValue() : TypeConverter.convertToSilent( quickFilter.getValue(), column.getColumnType() );
            unit.setValue( 1, value ); // can be null if conversion failed
        }
        final NormalFilterModel model = new NormalFilterModel();
        model.add( unit );
        return model;
    }

    public boolean isEmpty() {
        return _quick.isEmpty() && _normal.isEmpty() && _easy.isEmpty();
    }
    
    public boolean isWysiwyg() {
        // ZK-164
        // determine wheather or not the Quick Filter should use operator contains
        //
        // if the local atribute is TRUE
        // or local attribute is NOT DEFINED but the library property is TRUE
        // then set the operator to LIKE
        //
        // this allows to override the library attribute by local definition
        return (wysiwyg == Boolean.TRUE || (wysiwyg == null && USE_WYSIWYG));
    }

    public void setWysiwyg( final Boolean wysiwyg ) {
        this.wysiwyg = wysiwyg;
    }
}
