package cz.datalite.dao;

import java.util.Collections;
import java.util.List;

/**
 * Container for database response. This containter includes result list and
 * total length. If row count isn't required, this container can be
 * created by user only with data.
 * @param <T> entity in result list
 * @author Karel Cemus
 */
public class DLResponse<T> {

    /** result list */
    private final List<T> data;
    /** total length - if null, total size is not known */
    private final Integer rows;

    /** Empty response for a request. */
    @SuppressWarnings("unchecked")
    public static DLResponse EMPTY_RESPONSE = new DLResponse(Collections.EMPTY_LIST, 0);

    /** Empty response for a request. */
    @SuppressWarnings("unchecked")
    public static <T> DLResponse<T> emptyResponse() {
        return EMPTY_RESPONSE;
    }

    /**
     * Create un-full-filled container. Total length is set on 0, disabled paging
     * or not countPages in paging is recommended.
     * @param data result list
     */
    public static <T> DLResponse<T> response(List<T> data) {
        return new DLResponse<T>(data);
    }

    /**
     * Create filled container
     * @param data result list
     * @param rows total length
     */
    public static <T> DLResponse<T> response(List<T> data, final Integer rows) {
        return new DLResponse<T>(data, rows);
    }

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
     * Create un-full-filled container. Total length is not set.
     *
     * <p>Implementation of paging - total pages should be unknown until
     * data.size() < requested pageSize. Than we can infer, that end of data
     * is reached.
     * </p>
     * @param data result list
     */
    public DLResponse( final List<T> data ) {
        this.data = data;
        this.rows = null;
    }

    public List<T> getData() {
        return data;
    }

    public Integer getRows() {
        return rows;
    }
}
