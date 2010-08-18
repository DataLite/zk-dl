package cz.datalite.dao;

import java.util.List;

/**
 * Container for database response. This containter includes result list and
 * total length. If row count isn't required, this container can be
 * created by user only with data.
 * @param <T> entity in result list
 * @author Karel Čemus <cemus@datalite.cz>
 */
public class DLResponse<T> {

    /** result list */
    private final List<T> data;
    /** total length */
    private final Integer rows;

    /**
     * Create filled container
     * @param data result list
     * @param rows total length
     */
    public DLResponse( final List<T> data, final Integer rows ) {
        this.data = data;
        this.rows = rows;
    }

    /**
     * Create un-full-filled container. Total length is set on 0, disabled paging
     * or not countPages in paging is recommended.
     * @param data result list
     */
    public DLResponse( final List<T> data ) {
        this.data = data;
        this.rows = 0;
    }

    public List<T> getData() {
        return data;
    }

    public Integer getRows() {
        return rows;
    }
}
