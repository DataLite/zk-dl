package cz.datalite.zk.components.list;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import cz.datalite.zk.components.list.enums.DLFilterOperator;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class FilterTest {

    private static final List<Entity> entities = new LinkedList<Entity>();
    private static final Entity A = new Entity( 1, "ABC", new Date( 10 ), new Entity( 6, "V", null ) );
    private static final Entity B = new Entity( 4, "BCD", new Date( 11 ), new Entity( 0, "Y", new Date( 50 ) ) );
    private static final Entity C = new Entity( 5, "CDE", new Date( 14 ), new Entity( 7, "I", null ) );
    private static final Entity D = new Entity( 2, "DEA", new Date( 12 ), new Entity( 6, "X", null ) );
    private static final Entity E = new Entity( 4, "EAB", new Date( 15 ), new Entity( 9, "H", null ) );

    @BeforeClass
    public static void init() {
        entities.add( A );
        entities.add( B );
        entities.add( C );
        entities.add( D );
        entities.add( E );
    }

    @Test
    public void testFilter1() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.EQUAL, 4, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", B, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", E, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.size() );
    }

    @Test
    public void testFilter2() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.NOT_EQUAL, 4, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", A, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", 3, filtered.size() );
    }

    @Test
    public void testFilter3() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "name", DLFilterOperator.LIKE, "C", null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", A, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", B, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", 3, filtered.size() );
    }

    @Test
    public void testFilter4() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "name", DLFilterOperator.NOT_LIKE, "C", null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", E, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.size() );
    }

    @Test
    public void testFilter5() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "name", DLFilterOperator.START_WITH, "C", null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", 1, filtered.size() );
    }

    @Test
    public void testFilter6() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "name", DLFilterOperator.END_WITH, "C", null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", A, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", 1, filtered.size() );
    }

    @Test
    public void testFilter7() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.GREATER_THAN, new Date( 12 ), null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", E, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.size() );
    }

    @Test
    public void testFilter8() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.GREATER_EQUAL, 2, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", B, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", E, filtered.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", 4, filtered.size() );
    }

    @Test
    public void testFilter9() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.GREATER_EQUAL, 2, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 1, 2 );

        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.size() );
    }

    @Test
    public void testFilter10() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.GREATER_EQUAL, 2, null ) );

        final DLResponse<Entity> filtered = DLFilter.filterAndCount( filters, entities, 1, 2 );

        assertEquals( "Filter neproběhl v pořádku", C, filtered.getData().get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.getData().get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.getData().size() );
        assertEquals( "Filter neproběhl v pořádku", (Integer) 4, filtered.getRows() );
    }

    @Test
    public void testFilter11() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.LESSER_THAN, 2, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", A, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", 1, filtered.size() );
    }

    @Test
    public void testFilter12() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.LESSER_EQUAL, new Date( 14 ), null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", A, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", B, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", 4, filtered.size() );
    }

    @Test
    public void testFilter13() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "ref.date", DLFilterOperator.EMPTY, null, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", A, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", E, filtered.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", 4, filtered.size() );
    }

    @Test
    public void testFilter14() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "ref.date", DLFilterOperator.NOT_EMPTY, null, null ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", B, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", 1, filtered.size() );
    }

    @Test
    public void testFilter15() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.BETWEEN, new Date( 12 ), new Date( 14 ) ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 0, 10 );

        assertEquals( "Filter neproběhl v pořádku", C, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.size() );
    }

    @Test
    public void testFilter16() {
        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.BETWEEN, new Date( 12 ), new Date( 14 ) ) );

        final List<Entity> filtered = DLFilter.filter( filters, entities, 1, 10 );

        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", 1, filtered.size() );
    }

    @Test
    public void testSort1() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "i", DLSortType.ASCENDING ) );

        final List<Entity> sorted = DLFilter.sort( sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", A, sorted.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D, sorted.get( 1 ) );
        // stejné
//        assertEquals( "DLFilter neproběhl v pořádku", E, sorted.get( 2 ) );
//        assertEquals( "DLFilter neproběhl v pořádku", B, sorted.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", C, sorted.get( 4 ) );
    }

    @Test
    public void testSort2() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "name", DLSortType.DESCENDING ) );

        final List<Entity> sorted = DLFilter.sort( sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", E, sorted.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D, sorted.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", C, sorted.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", B, sorted.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", A, sorted.get( 4 ) );
    }

    @Test
    public void testSort3() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "date", DLSortType.DESCENDING ) );

        final List<Entity> sorted = DLFilter.sort( sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", E, sorted.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", C, sorted.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", D, sorted.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", B, sorted.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", A, sorted.get( 4 ) );
    }

    @Test
    public void testSort4() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "date", DLSortType.ASCENDING ) );

        final List<Entity> sorted = DLFilter.sort( sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", A, sorted.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", B, sorted.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", C, sorted.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", D, sorted.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", E, sorted.get( 4 ) );


    }

    @Test
    public void testSort5() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "ref.i", DLSortType.ASCENDING ) );

        final List<Entity> sorted = DLFilter.sort( sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", B, sorted.get( 0 ) );
//        assertEquals( "DLFilter neproběhl v pořádku", A, sorted.get( 1 ) );
//        assertEquals( "DLFilter neproběhl v pořádku", D, sorted.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", C, sorted.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", E, sorted.get( 4 ) );
    }

    @Test
    public void testSort6() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "ref.i", DLSortType.DESCENDING ) );

        final List<Entity> sorted = DLFilter.sort( sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", E, sorted.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", C, sorted.get( 1 ) );
        //        assertEquals( "DLFilter neproběhl v pořádku", D, sorted.get( 2 ) );
//        assertEquals( "DLFilter neproběhl v pořádku", A, sorted.get( 3 ) );
        assertEquals( "Filter neproběhl v pořádku", B, sorted.get( 4 ) );
    }

    @Test
    public void testLoadData1() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "i", DLSortType.DESCENDING ) );
        sorts.add( new DLSort( "ref.name", DLSortType.ASCENDING ) );

        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.LESSER_THAN, 5, null ) );
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.BETWEEN, new Date( 11 ), new Date( 20 ) ) );

        final List<Entity> filtered = DLFilter.filter( filters, 1, 1, sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", 1, filtered.size() );
    }

    @Test
    public void testLoadData2() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "i", DLSortType.DESCENDING ) );
        sorts.add( new DLSort( "ref.name", DLSortType.ASCENDING ) );

        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.LESSER_THAN, 5, null ) );
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.BETWEEN, new Date( 11 ), new Date( 20 ) ) );

        final List<Entity> filtered = DLFilter.filter( filters, 0, 5, sorts, entities );

        assertEquals( "Filter neproběhl v pořádku", E, filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D, filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", B, filtered.get( 2 ) );
        assertEquals( "Filter neproběhl v pořádku", 3, filtered.size() );
    }

    @Test
    public void testFilterDistinct() {
        final List<DLSort> sorts = new LinkedList<DLSort>();
        sorts.add( new DLSort( "i", DLSortType.DESCENDING ) );
        sorts.add( new DLSort( "ref.name", DLSortType.ASCENDING ) );

        final List<NormalFilterUnitModel> filters = new LinkedList<NormalFilterUnitModel>();
        filters.add( new ModifiedNormalFilterUnitModel( "i", DLFilterOperator.LESSER_THAN, 5, null ) );
        filters.add( new ModifiedNormalFilterUnitModel( "date", DLFilterOperator.BETWEEN, new Date( 11 ), new Date( 20 ) ) );

        final List<Object> filtered = DLFilter.filterDistinct( filters, 0, 5, sorts, entities, "i" );

        assertEquals( "Filter neproběhl v pořádku", E.getI(), filtered.get( 0 ) );
        assertEquals( "Filter neproběhl v pořádku", D.getI(), filtered.get( 1 ) );
        assertEquals( "Filter neproběhl v pořádku", 2, filtered.size() );
    }

    public static class Entity {

        protected Integer i;
        protected String name;
        protected Date date;
        protected Entity ref;

        public Entity( final Integer i, final String name, final Date date ) {
            this.i = i;
            this.name = name;
            this.date = date;
        }

        public Entity( final Integer i, final String name, final Date date, final Entity entity ) {
            this.i = i;
            this.name = name;
            this.date = date;
            this.ref = entity;
        }

        public Entity() {
            this.i = 0;
            this.name = null;
            this.date = null;
            this.ref = null;
        }

        public Date getDate() {
            return date;
        }

        public Integer getI() {
            return i;
        }

        public String getName() {
            return name;
        }

        public Entity getRef() {
            return ref;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

class ModifiedNormalFilterUnitModel extends NormalFilterUnitModel {

    public ModifiedNormalFilterUnitModel( final String property, final DLFilterOperator operator, final Object value1, final Object value2 ) {
        super( property );
        setOperator( operator );
        setValue( 1, value1 );
        setValue( 2, value2 );
    }

    @Override
    public void setOperator( final DLFilterOperator operator ) {
        this.operator = operator;
    }
}
