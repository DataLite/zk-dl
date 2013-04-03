package cz.datalite.dao;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.sql.JoinType;

/**
 * <p>Class for transfer filter parameters like criterion, sort, paging and projection.</p>
 * <p>This class is using Hibernate utils.</p>
 * @param <T> root class in definition hierarchy
 * @author Karel Cemus
 */
public class DLSearch<T> {

    /** Criterions in filter */
    private final List<Criterion> criterions = new LinkedList<Criterion>();
    /** Sorts - order by definition */
    private List<DLSort> sorts;
    /** Requested rowCount - if it is 0 then request all rows*/
    private int rowCount;
    /** First row in selection */
    private int firstRow;
    /** Return distinct values. */
    private boolean distinct;
    /** Aliases are used for hierarchy structure */
    private final Set<Alias> aliases = new HashSet<Alias>();
    /** Projection type like distinct or row count */
    private final List<Projection> projections = new LinkedList<Projection>();
    /** Defines constant for disabled paging */
    public static final int NOT_PAGING = -1;
    private Class<T> persistentClass = null;

    /**
     * Returns the generic class this object is constucted for (may be null)
     * @return the generic class
     */
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    /**
     * Create DLSearch
     * @param rowCount requested row count
     * @param firstRow index of 1st row (starts at 0)
     */
    public DLSearch( final int rowCount, final int firstRow ) {
        this( new LinkedList<DLSort>(), rowCount, firstRow );
    }

    /**
     * Create DLSearch
     */
    public DLSearch() {
        this( new LinkedList<DLSort>() );
    }

    /**
     * Create DLSearch
     * @param sorts order by definition
     */
    public DLSearch( final List<DLSort> sorts ) {
        this( sorts, 0, DLSearch.NOT_PAGING );
    }

    /**
     * Create DLSearch
     * @param sorts order by definition
     * @param rowCount requested row count
     * @param firstRow index of 1st row (starts at 0)
     */
    public DLSearch( final List<DLSort> sorts, final int rowCount, final int firstRow ) {
        this.sorts = sorts;
        this.rowCount = rowCount;
        this.firstRow = firstRow;

        // setup persistence class, only if set
        if ( getClass().getGenericSuperclass() instanceof ParameterizedType
                && ((( ParameterizedType ) getClass().getGenericSuperclass()).getActualTypeArguments()).length > 0 ) {
            this.persistentClass = ( Class<T> ) (( ParameterizedType ) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
    }

    /**
     * Remove all filter criterions
     */
    public void clearFilterCriterions() {
        this.criterions.clear();
    }

    /**
     * Add all filter criterions
     * @param criterions filter criterions
     */
    public void addFilterCriterions( final List<Criterion> criterions ) {
        this.criterions.addAll( criterions );
    }

    /**
     * Add filter criterion
     * @param criterion filter criterion
     */
    public void addFilterCriterion( final Criterion criterion ) {
        this.criterions.add( criterion );
    }

    /**
     * Remove filter criterion
     * @param criterion filter criterion
     */
    public void removeFilterCriterions( final Criterion criterion ) {
        this.criterions.remove( criterion );
    }

    /**
     * Returns index of 1st row
     * @return index of 1st row, starts at 0
     */
    public int getFirstRow() {
        return firstRow;
    }

    /**
     * Set first row index - starts at 0
     * @param firstRow row index
     */
    public void setFirstRow( final int firstRow ) {
        this.firstRow = firstRow;
    }

    /**
     * Requested row count. If 0 then request all results.
     * @return row count - max result list length
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Set requested row count. If 0 then request all results.
     * @param rowCount requested row count
     */
    public void setRowCount( final int rowCount ) {
        this.rowCount = rowCount;
    }

    /**
     * Returns iterator for all sorts. It is ordered structure.
     * @return iterator on ordered structure
     */
    public Iterator<DLSort> getSorts() {
        return sorts.iterator();
    }

    /**
     * Set order by definition. It is ordered structure
     * @param sorts order by definitions
     */
    public void setSorts( final List<DLSort> sorts ) {
        this.sorts = sorts;
    }

    /**
     * Add order by definition. It is ordered structure so this metod append
     * on the tail.
     * @param sort order by definition
     */
    public void addSort( final DLSort sort ) {
        this.sorts.add( sort );
    }

    /**
     * Add order by definition by column ascending. It is ordered structure so this metod append
     * on the tail.
     * This method is shortcut for addSort(new DLSort(column, DLSortType.ASCENDING)).
     * @param column
     */
    public void addSort( final String column ) {
        addSort( new DLSort( column, DLSortType.ASCENDING ) );
    }

    /**
     * Odstraní všechny informace o sortování
     */
    public void clearSorts() {
        this.sorts.clear();
    }

    /**
     * Returns unordered criterions.
     * @return collection of criterions
     */
    public Collection<Criterion> getCriterions() {
        return criterions;
    }

    /**
     * <p>Adds alias name for your property. If alias is already defined
     * nothing is done. Else your entered alias is added.</p>
     * <p>Notes: <br/>Alias must containt EXACTLY one dot.<br/>Alias name
     * mustn't be same like some field name.</p>
     * <h2>Example:</h2>
     * <i>path:</i> human.hobby<br/>
     * <i>alias:</i> hobby<br/>
     * <h2>Example:</h2>
     * <i>path:</i> hobby.type<br/>
     * <i>alias:</i> typeAlias<br/>
     * @param path table address - contains exactly one dot
     * @param alias pseudonym for the path
     * @param joinType {@link org.hibernate.criterion.CriteriaSpecification#FULL_JOIN},
     * {@link org.hibernate.criterion.CriteriaSpecification#INNER_JOIN},
     * {@link org.hibernate.criterion.CriteriaSpecification#LEFT_JOIN}
     * @deprecated as of Hibernate 4  use JoinType object
     */
    @Deprecated
    public void addAlias( final String path, final String alias, final int joinType ) {
       addAlias(path, alias, JoinType.parse(joinType));
    }

    /**
     * <p>Adds alias name for your property. If alias is already defined
     * nothing is done. Else your entered alias is added.</p>
     * <p>Notes: <br/>Alias must containt EXACTLY one dot.<br/>Alias name
     * mustn't be same like some field name.</p>
     * <h2>Example:</h2>
     * <i>path:</i> human.hobby<br/>
     * <i>alias:</i> hobby<br/>
     * <h2>Example:</h2>
     * <i>path:</i> hobby.type<br/>
     * <i>alias:</i> typeAlias<br/>
     * @param path table address - contains exactly one dot
     * @param alias pseudonym for the path
     * @param joinType {@link JoinType#FULL_JOIN}, {@link JoinType#LEFT_OUTER_JOIN} {@link JoinType#INNER_JOIN}
     */
    public void addAlias( final String path, final String alias, final JoinType joinType ) {
        String fullPath = path.lastIndexOf( '.' ) == -1 ? "" : path.substring( 0, path.lastIndexOf( '.' ) );
        Alias parent = null;
        if ( fullPath.length() > 0 ) {
            for ( Alias a : aliases ) {
                if ( a.getAlias().equals( fullPath ) ) {
                    parent = a;
                    break;
                }
            }
        }

        if ( parent == null ) {
            fullPath = path;
        } else {
            fullPath = parent.getFullPath() + '.' + path.substring( path.indexOf( '.' ) + 1 );
        }

        for ( Alias a : aliases ) {
            if ( a.getFullPath().equals( fullPath ) ) {
                return;
            }
        }

        aliases.add( new Alias( fullPath, path, alias, joinType ) );
    }

    /**
     * <p>Adds alias name for your property. If alias is already defined
     * nothing is done. Else your entered alias is added. Calls addAlias(path, path, joinType).</p>
     * @param path table address - contains exactly one dot
     * @param joinType {@link JoinType#FULL_JOIN}, {@link JoinType#LEFT_OUTER_JOIN} {@link JoinType#INNER_JOIN}
     */
    public void addAlias( final String path, final JoinType joinType ) {
        addAlias( path, path, joinType );
    }

    /**
     * <p>Adds alias name for your property. If alias is already defined
     * nothing is done. Else your entered alias is added. Calls addAlias(path, path, joinType).</p>
     * @param path table address - contains exactly one dot
     * @param joinType {@link org.hibernate.criterion.CriteriaSpecification#FULL_JOIN},
     * {@link org.hibernate.criterion.CriteriaSpecification#INNER_JOIN},
     * {@link org.hibernate.criterion.CriteriaSpecification#LEFT_JOIN}
     * @deprecated as of Hibernate 4 use JPA JoinType
     */
    @Deprecated
    public void addAlias( final String path, final int joinType ) {
        addAlias( path, path, joinType );
    }

    /**
     * <p>Returns alias name for your property. If alias is already defined
     * for this path it is returned. Else your entered alias is added and
     * returned. As joinType is used {@link org.hibernate.criterion.CriteriaSpecification#INNER_JOIN}.</p>
     * <p>Notes: <br/>Alias must containt EXACTLY one dot.<br/>Alias name
     * mustn't be same like some field name.</p>
     * <h2>Example:</h2>
     * <i>path:</i> human.hobby<br/>
     * <i>alias:</i> hobby<br/>
     * <h2>Example:</h2>
     * <i>path:</i> hobby.type<br/>
     * <i>alias:</i> typeAlias<br/>
     * @param path table address - contains exactly one dot
     * @param alias pseudonym for the path
     */
    public void addAlias( final String path, final String alias ) {
        addAlias( path, alias, JoinType.INNER_JOIN );
    }

    /**
     * Returns alias for the full path with property
     * @param fullPath full path with property
     * @return alias witch property or null
     */
    public String getAliasForPath( final String fullPath ) {
        final String path = getPath( fullPath );
        for ( Alias a : aliases ) {
            if ( a.getFullPath().equals( path ) ) {
                return a.getAlias() + '.' + getEndOfPath( fullPath );
            }
        }
        return fullPath;
    }

    /**
     * Returns all registered aliases.
     * @return collection with aliases
     */
    public Set<Alias> getAliases() {
        return aliases;
    }

    /**
     * Adds all aliases for this full field path.
     * Join type is INNER.
     * If aliases already exists nothing is done
     * @param fullPath full field path
     */
    public void addAlias( final String fullPath ) {
        addAliases( fullPath, JoinType.INNER_JOIN );
    }

    /**
     * Adds all aliases for this full field path.
     * If aliases already exists nothing is done
     * @param fullPath full field path
     * @param joinType type of the join in SQL {@link org.hibernate.criterion.CriteriaSpecification}
     * @deprecated as of hibernate 4
     */
    @Deprecated
    public void addAliases( final String fullPath, final int joinType ) {
        addAliases(fullPath, JoinType.parse(joinType));
    }

    /**
     * Adds all aliases for this full field path.
     * If aliases already exists nothing is done
     * @param fullPath full field path
     * @param joinType type of the join in SQL {@link org.hibernate.criterion.CriteriaSpecification}
     */
    public void addAliases( final String fullPath, final JoinType joinType ) {
        final List<String> parts = DLSearch.getAliasesForWholePath( fullPath );
        for ( int i = -1; i < parts.size() - 1; i++ ) {
            // aliases.get(i) = sth
            // aliases.get(i+1) = sth2
            // i == 0
            // sth.sth2 as sth2Alias
            //----------------------------
            // aliases.get(i) = sth
            // aliases.get(i+1) = sth2
            // i != 0
            // sthAlias.sth2 as sth2Alias
            final String path = (i == -1 ? "" : parts.get( i ) + "Alias.") + parts.get( i + 1 );
            final String alias = parts.get( i + 1 ) + "Alias";
            addAlias( path, alias, joinType );
        }
    }

    /**
     * Add projection like distinct or row count
     * @param projection hibernate projection
     */
    public void addProjection( final Projection projection ) {
        projections.add( projection );
    }

    /**
     * Return collection (unordered) with all projections
     * @return all defined projections
     */
    public Collection<Projection> getProjections() {
        return projections;
    }

    /**
     * Returns aliases for path whole path to the field
     * @param fieldPath whole path with dots
     * @return required subpaths
     */
    public static List<String> getAliasesForWholePath( final String fieldPath ) {
        // path: sth1.sth2.sth3.sth4.field
        // result:
        //      sth1
        //      sth2
        //      sth3
        //      sth4
        final String[] strings = fieldPath.split( "\\." );
        final List<String> paths = new LinkedList<String>();
        for ( int i = 0; i < strings.length - 1; i++ ) {
            paths.add( strings[i] );
        }
        return paths;
    }

    /**
     * Returns end of the path. If it is full field path
     * returned value is field name
     * @param path path
     * @return end from dot to the end
     */
    public static String getEndOfPath( final String path ) {
        return path.substring( path.lastIndexOf( '.' ) + 1 );
    }

    /**
     * Returns path from the full field path - field name is trimmed
     * @param fieldPath full field path
     * @return path
     */
    public static String getPath( final String fieldPath ) {
        return fieldPath.lastIndexOf( '.' ) == -1 ? "" : fieldPath.substring( 0, fieldPath.lastIndexOf( '.' ) );
    }

    public static class Alias {

        protected String _fullPath;
        protected String _path;
        protected String _alias;

        protected JoinType _joinType;

        /**
         * @deprecated as of Hibernate 4 user JoinType
         */
        @Deprecated
        public Alias( final String fullPath, final String path, final String alias, final int joinType ) {
            this._fullPath = fullPath;
            this._path = path;
            this._alias = alias;
            this._joinType = JoinType.parse(joinType);
        }

        public Alias( final String fullPath, final String path, final String alias, final JoinType joinType ) {
            this._fullPath = fullPath;
            this._path = path;
            this._alias = alias;
            this._joinType = joinType;
        }

        public String getAlias() {
            return _alias;
        }

        public void setAlias( final String alias ) {
            this._alias = alias;
        }

        /**
         * @deprecated as of Hibernate 4 user JoinType
         */
        @Deprecated
        public int getJoinTypeCriteria() {
            switch (_joinType) {
                case INNER_JOIN: return Criteria.INNER_JOIN;
                case LEFT_OUTER_JOIN: return Criteria.LEFT_JOIN;
                case FULL_JOIN:return Criteria.FULL_JOIN;

                default: throw new IllegalStateException("JoinType not supported: " + _joinType);
            }

        }

        /**
         * @deprecated as of Hibernate 4 user JoinType
         */
        @Deprecated
        public void setJoinTypeCriteria( final int joinType ) {
            this._joinType = JoinType.parse(joinType);
        }

        public JoinType getJoinType() {
            return _joinType;
        }

        public void setJoinType(JoinType joinTypeJpa) {
            this._joinType = joinTypeJpa;
        }

        public String getPath() {
            return _path;
        }

        public void setPath( final String path ) {
            this._path = path;
        }

        public String getFullPath() {
            return _fullPath;
        }

        public void setFullPath( final String _fullPath ) {
            this._fullPath = _fullPath;
        }

        @Override
        public boolean equals( final Object obj ) {
            if ( obj == null ) {
                return false;
            }
            if ( getClass() != obj.getClass() ) {
                return false;
            }
            final Alias other = ( Alias ) obj;
            if ( (this._path == null) ? (other._path != null) : !this._path.equals( other._path ) ) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 23 * hash + (this._path == null ? 0 : this._path.hashCode());
            return hash;
        }
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct( final boolean distinct ) {
        this.distinct = distinct;
    }
}
