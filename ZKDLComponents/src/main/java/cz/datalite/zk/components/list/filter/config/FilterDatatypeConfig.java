package cz.datalite.zk.components.list.filter.config;

import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.components.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class serves as a configuration file, there are defined basic datatypes
 * and their filter operators. It means QuickFilter operator and list of operators
 * which is shown in the default case in the normal filter. This configuration
 * can be overwritten by filter component.
 *
 * @author Karel Cemus
 */
public abstract class FilterDatatypeConfig extends InstanceFilterComponentFactory {

    /** Map of supported datatypes */
    public static final Map<Class, FilterDatatypeConfig> DEFAULT_CONFIGURATION = init();
    /** List of normal filter operators for the specific datatype */
    protected final List<DLFilterOperator> operators;
    /** quick filter operator */
    protected final DLFilterOperator quickOperator;

    protected FilterDatatypeConfig( final List<DLFilterOperator> operators, final DLFilterOperator quickOperator ) {
        super( "java.lang.Object" );
        this.operators = new ArrayList<DLFilterOperator>( operators );
        this.quickOperator = quickOperator;
    }

    public List<DLFilterOperator> getOperators() {
        return operators;
    }

    public DLFilterOperator getQuickOperator() {
        return quickOperator;
    }

    private static List<DLFilterOperator> createStringOperators() {
        final List<DLFilterOperator> operators = new ArrayList<DLFilterOperator>( 10 );
        operators.add( DLFilterOperator.EQUAL );
        operators.add( DLFilterOperator.NOT_EQUAL );
        operators.add( DLFilterOperator.LIKE );
        operators.add( DLFilterOperator.NOT_LIKE );
        operators.add( DLFilterOperator.START_WITH );
        operators.add( DLFilterOperator.END_WITH );
        operators.add( DLFilterOperator.EMPTY );
        operators.add( DLFilterOperator.NOT_EMPTY );
        return operators;
    }

    private static List<DLFilterOperator> createCharOperators() {
        final List<DLFilterOperator> operators = new ArrayList<DLFilterOperator>( 10 );
        operators.add( DLFilterOperator.EQUAL );
        operators.add( DLFilterOperator.NOT_EQUAL );
        operators.add( DLFilterOperator.EMPTY );
        operators.add( DLFilterOperator.NOT_EMPTY );
        return operators;
    }

    private static FilterDatatypeConfig createCharConfig() {
        final List<DLFilterOperator> operators = createCharOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new CharFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return CharFilterComponent.class;
            }
        };
        return config;
    }

    private static FilterDatatypeConfig createStringConfig() {
        final List<DLFilterOperator> operators = createStringOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.LIKE ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new StringFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return StringFilterComponent.class;
            }
        };
        return config;
    }

    private static List<DLFilterOperator> createNumberOperators() {
        final List<DLFilterOperator> operators = new ArrayList<DLFilterOperator>( 10 );
        operators.add( DLFilterOperator.EQUAL );
        operators.add( DLFilterOperator.NOT_EQUAL );
        operators.add( DLFilterOperator.GREATER_EQUAL );
        operators.add( DLFilterOperator.GREATER_THAN );
        operators.add( DLFilterOperator.LESSER_EQUAL );
        operators.add( DLFilterOperator.LESSER_THAN );
        operators.add( DLFilterOperator.BETWEEN );
        operators.add( DLFilterOperator.EMPTY );
        operators.add( DLFilterOperator.NOT_EMPTY );
        return operators;
    }

    private static FilterDatatypeConfig createIntConfig( final int minValue, final int maxValue ) {
        final List<DLFilterOperator> operators = createNumberOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new IntegerFilterComponent( minValue, maxValue );
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return IntegerFilterComponent.class;
            }
        };
        return config;
    }

    private static FilterDatatypeConfig createDoubleConfig() {
        final List<DLFilterOperator> operators = createNumberOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new DoubleFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return DoubleFilterComponent.class;
            }
        };
        return config;
    }

    private static FilterDatatypeConfig createBigDecimalConfig() {
        final List<DLFilterOperator> operators = createNumberOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new DecimalFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return DecimalFilterComponent.class;
            }
        };
        return config;
    }

    private static FilterDatatypeConfig createLongConfig() {
        final List<DLFilterOperator> operators = createNumberOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new LongFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return LongFilterComponent.class;
            }
        };
        return config;
    }

    private static List<DLFilterOperator> createDateOperators() {
        final List<DLFilterOperator> operators = new ArrayList<DLFilterOperator>( 10 );
        operators.add( DLFilterOperator.EQUAL );
        operators.add( DLFilterOperator.NOT_EQUAL );
        operators.add( DLFilterOperator.GREATER_EQUAL );
        operators.add( DLFilterOperator.GREATER_THAN );
        operators.add( DLFilterOperator.LESSER_EQUAL );
        operators.add( DLFilterOperator.LESSER_THAN );
        operators.add( DLFilterOperator.BETWEEN );
        operators.add( DLFilterOperator.EMPTY );
        operators.add( DLFilterOperator.NOT_EMPTY );
        return operators;
    }

    private static FilterDatatypeConfig createDateConfig() {
        final List<DLFilterOperator> operators = createDateOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new DateFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return DateFilterComponent.class;
            }
        };
        return config;
    }

    private static List<DLFilterOperator> createBooleanOperators() {
        final List<DLFilterOperator> operators = new ArrayList<DLFilterOperator>( 10 );
        operators.add( DLFilterOperator.EQUAL );
        operators.add( DLFilterOperator.NOT_EQUAL );
        return operators;
    }

    private static FilterDatatypeConfig createBooleanConfig() {
        final List<DLFilterOperator> operators = createBooleanOperators();
        final FilterDatatypeConfig config = new FilterDatatypeConfig( operators, DLFilterOperator.EQUAL ) {

            @Override
            public FilterComponent createFilterComponent() {
                return new BooleanFilterComponent();
            }

            @Override
            public Class<? extends FilterComponent> getComponentClass() {
                return BooleanFilterComponent.class;
            }
        };
        return config;
    }

    private static Map<Class, FilterDatatypeConfig> init() {
        final Map<Class, FilterDatatypeConfig> config = new HashMap<Class, FilterDatatypeConfig>();

        // String configuration
        config.put( String.class, createStringConfig() );

        final FilterDatatypeConfig charConfig = createCharConfig();
        config.put( Character.class, charConfig );
        config.put( Character.TYPE, charConfig );


        // number configuration
        final FilterDatatypeConfig intConfig = createIntConfig( Integer.MIN_VALUE, Integer.MAX_VALUE );
        config.put( Integer.class, intConfig );
        config.put( Integer.TYPE, intConfig );

        final FilterDatatypeConfig shortConfig = createIntConfig( Short.MIN_VALUE, Short.MAX_VALUE );
        config.put( Short.class, shortConfig );
        config.put( Short.TYPE, shortConfig );

        final FilterDatatypeConfig byteConfig = createIntConfig( Byte.MIN_VALUE, Byte.MAX_VALUE );
        config.put( Byte.class, byteConfig );
        config.put( Byte.TYPE, byteConfig );

        final FilterDatatypeConfig longConfig = createLongConfig();
        config.put( Long.class, longConfig );
        config.put( Long.TYPE, longConfig );

        final FilterDatatypeConfig doubleConfig = createDoubleConfig();
        config.put( Double.class, doubleConfig );
        config.put( Double.TYPE, doubleConfig );
        config.put( Float.class, doubleConfig );
        config.put( Float.TYPE, doubleConfig );

        final FilterDatatypeConfig bigDecimalConfig = createBigDecimalConfig();
        config.put( BigDecimal.class, bigDecimalConfig );

        // date configuration
        final FilterDatatypeConfig dateConfig = createDateConfig();
        config.put( Date.class, dateConfig );

        // boolean configuraion
        final FilterDatatypeConfig booleanConfig = createBooleanConfig();
        config.put( Boolean.class, booleanConfig );
        config.put( Boolean.TYPE, booleanConfig );

        return config;
    }
}
