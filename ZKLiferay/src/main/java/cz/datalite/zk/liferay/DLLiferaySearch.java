package cz.datalite.zk.liferay;

import com.liferay.portal.kernel.dao.search.DisplayTerms;
import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.dao.DLSort;
import cz.datalite.dao.DLSortType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.portlet.context.PortletRequestAttributes;

import javax.portlet.RenderRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Generic search instance.
 *
 * It extends DisplayTerms, because mapping of display terms is hardwired and need to be directly
 * a property of mapped object.
 *
 * @param <T>
 * @author Jiri Bubnik
 */
public class DLLiferaySearch<T> extends DisplayTerms {

    /**
     * Public constructor (adhere to javabean notation)
     */
    public DLLiferaySearch() {
        // Don't know where to get request from Liferay within static method, use Spring instead.
        this((RenderRequest) ((PortletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * The prefered way of how to construct the Liferay Search object.
     *
     * In case of Spring Portlet MVC, it should be able to construct iself as javabean, so we provide
     * a no-arg constructor which finds current request via Spring RequestContextHolder
     * static variables.
     *
     * @param request current request
     */
    public DLLiferaySearch(RenderRequest request)
    {
        // Display Terms needs request in constructor (for parameter retrieval).
        super(request);

        this.request = request;
    }

    // Actual request
    private RenderRequest request;

    /**
     * Current position in resultset.
     */
    private int cur = 1;

    /**
     * Delta in resultset. *
     */
    private int delta = 5;

    /**
     * Search container result data.
     */
    private List<T> data;

    /**
     * Search container total count.
     */
    private int total;

    /**
     * An instance of the model (to store search criteria).
     */
    private T model;

    /**
     * Order by column
     */
    private String orderByCol;

    /**
     * Order type (asc/desc)
     */
    private DLSortType orderByType = DLSortType.ASCENDING;


    /**
     * @return the data
     */
    public List<T> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return the cur
     */
    public int getCur() {
        return cur;
    }

    /**
     * @param cur the cur to set
     */
    public void setCur(int cur) {
        this.cur = cur;
    }

    /**
     * @return the delta
     */
    public int getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getStart() {
        return delta * (cur - 1);
    }

    public int getEnd() {
        return delta * cur;
    }

    /**
     * An instance of the model (to store search criteria).
     */
    public T getModel() {
        return model;
    }

    /**
     * An instance of the model (to store search criteria).
     */
    public void setModel(T model) {
        this.model = model;
    }

    public String getOrderByCol() {
        return orderByCol;
    }

    public void setOrderByCol(String orderByCol) {
        this.orderByCol = orderByCol;
    }

    public DLSortType getOrderByTypeEnum() {
        return orderByType;
    }

    public void setOrderByType(DLSortType orderByType) {
        this.orderByType = orderByType;
    }

    public String getOrderByType() {
        if (DLSortType.DESCENDING.equals(getOrderByTypeEnum()))
            return "desc";
        else
            return "asc";
    }

    public void setOrderByType(String orderByType) {
        if ("desc".equalsIgnoreCase(orderByType))
            setOrderByType(DLSortType.DESCENDING);
        else
            setOrderByType(DLSortType.ASCENDING);
    }

    public DLSearch<T> getZKDLSearch()
    {
        DLSearch<T> search = new DLSearch<>();

        search.setFirstRow(getStart());
        search.setRowCount(getEnd() - getStart());

        if (getOrderByCol() != null)
            search.setSorts(Arrays.asList(new DLSort(getOrderByCol(), getOrderByTypeEnum())));

        return search;
    }

    public void setZKDLResponse(DLResponse<T> response)
    {
        setData(response.getData());
        setTotal(response.getRows());
    }

}
