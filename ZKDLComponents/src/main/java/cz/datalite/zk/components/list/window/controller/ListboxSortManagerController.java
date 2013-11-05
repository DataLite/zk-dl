package cz.datalite.zk.components.list.window.controller;

import cz.datalite.dao.DLSortType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zkplus.databind.TypeConverter;

import java.util.*;
import java.util.Map.Entry;

/**
 * Controller for the manager which allows user to set advanced configuration for
 * sorting.
 * @author Karel Cemus
 */
public class ListboxSortManagerController extends GenericAutowireComposer {

    // maximum number of sort columns
    private static final int MAX_SORT_COUNT = 10;

    public static class SortTypeNameConverter implements TypeConverter
    {
        public Object coerceToUi(Object o, Component cmpnt)
        {
            String value = o == null ? "natural" : o.toString();
            return Labels.getLabel("listbox.sortingManager.sort." + value, value);
        }
        public Object coerceToBean(Object o, Component cmpnt)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    protected List<Map<String, Object>> model = new java.util.ArrayList<Map<String, Object>>();
    protected final List<DLSortType> sortTypes;

    public ListboxSortManagerController() {
        sortTypes = new LinkedList<DLSortType>( Arrays.asList( DLSortType.values() ) );
        }
        
    @Override
    @SuppressWarnings( "unchecked" )
    public void doAfterCompose( final org.zkoss.zk.ui.Component comp ) throws Exception {
        super.doAfterCompose( comp );
        comp.setAttribute( "ctl", this, Component.SPACE_SCOPE);

        final List<Map<String, Object>> unitModels = ( List<Map<String, Object>> ) arg.get( "columnModels" );

        // sort by sortOrder, secundary by order
        java.util.Collections.sort( unitModels, new java.util.Comparator<Map<String, Object>>() {

            public int compare( final Map<String, Object> o1, final Map<String, Object> o2 ) {
                final Integer sortOrder1 = ( Integer ) o1.get( "sortOrder" );
                final Integer sortOrder2 = ( Integer ) o2.get( "sortOrder" );
                if ( !sortOrder1.equals( sortOrder2 ) ) {
                    return sortOrder1 == 0 ? 1 : sortOrder2 == 0 ? -1 : sortOrder1 - sortOrder2;
                }
                final Integer order1 = ( Integer ) o1.get( "order" );
                final Integer order2 = ( Integer ) o2.get( "order" );
                return order1 - order2;
            }
        } );

        // sort types
        final List<DLSortType> modelSortTypes = getModelSortTypes();

        // column types
        final List<Entry<Integer, String>> modelColumns = new LinkedList<Entry<Integer, String>>();

        // model setting
        int order = 0;
        for ( final Map<String, Object> unitMap : unitModels ) {
            modelColumns.add( new Entry<Integer, String>() {

                public Integer getKey() {
                    return ( Integer ) unitMap.get( "index" );
                }

                public String getValue() {
                    return ( String ) unitMap.get( "label" );
                }

                public String setValue( final String value ) {
                    throw new UnsupportedOperationException( "Not supported yet." );
                }
            } );

            // only for columns with sort order
            final Integer sortOrder = ( Integer ) unitMap.get( "sortOrder" );
            if (sortOrder != 0) {
                model.add( order, prepareMap( order + 1, unitMap, modelColumns, modelSortTypes ) );
                ++order;
            }
        }

        // up to MAX_SORT_COUNT insert empty rows
        for (int i=order; i< Math.min(unitModels.size(), MAX_SORT_COUNT); i++) {
            model.add( order, prepareMap( order + 1, null, modelColumns, modelSortTypes ) );
            ++order;
        }
    }

    protected List<DLSortType> getModelSortTypes() {
        return sortTypes;
    }

    protected Map<String, Object> prepareMap( final Integer number, final Map<String, Object> unitMap,
            final List<Entry<Integer, String>> modelColumns, final List<DLSortType> modelSortTypes ) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put( "number", number + "." );
        map.put( "numberInt", number );
        map.put( "column", unitMap == null ? null : modelColumns.get( modelColumns.size() - 1 ) );
        map.put( "sortType", unitMap == null ? DLSortType.NATURAL : unitMap.get( "sortType" ) );
        map.put( "modelColumns", modelColumns );
        map.put( "modelSortTypes", modelSortTypes );
        return map;
    }

    public List<Map<String, Object>> getModel() {
        return model;
    }

    public void onOk() {
        Events.postEvent( new Event( "onSave", self, model ) );
        self.detach();
    }

    public void onStorno() {
        self.detach();
    }
}
