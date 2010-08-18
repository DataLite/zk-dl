/**
 * <p>The listbox component in MVC design, this is the most advanced component.</p>
 * 
 * <p>You can change the listbox behaviour by attaching controller to it: &ltlistbox apply="${listboxController}"&gt;.
 * Then all data related events are served by this controller and all you have to do is implement the controller loadData() method. You can attach
 * paging component and/or listControl component to the controller and get additional behaviour for free.
 * </p>
 *
 * <p>
 * Look at @link cz.datalite.zk.components.list.DLListboxController for controlle definition. There are existing implementations:
 * <ul>
 *   <li>cz.datalite.zk.components.list.DLListboxGeneralController - master implementation. You should always start at least with this class</li>
 *   <li>cz.datalite.zk.components.list.DLListboxSimpleController - prepared for custom data filtering</li>
 *   <li>cz.datalite.zk.components.list.DLListboxCriteriaController - complete solution for Hibernate with DeatchedCriteria</li>
 * </ul>
 * </p>
 *
 *     <p>
 *        <b>Listbox controller and DAO:</b><br/>
 *        The main and most convenient implementation of listbox controller is  DLListboxCriteriaController which works
 *        with Hibernate DetachedCriteria. This library contains Generic DAO Hibernate implemetation which contains
 *        search(DLSearch) method - which concludes the story. You can let DLListboxCriteriaController to generate
 *        or filters, sorting, paging and let GenericDAO to execute the query. However - you are encuraged to
 *        study DLListboxCriteriaController#getDefaultSearchObject and GenericDAOImpl#prepareDetachedCriteria
 *        methods. It is very easy to implement your controller which will work with any persistence mechanism - JPA or
 *        lets say ISearch.<br/>
 *        Have a look at DLListboxSimpleController as well, while it compiles all filters/sorts/paging into simple object structure, which
 *        you can parse and filter virtually anything. In combination with DLFilter,
 *        you can use simple list as data source and gain all filtering/sorgin/paging functionality as with database list for free.
 *    </p>
 * 
 */
package cz.datalite.zk.components.list;

