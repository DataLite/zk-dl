package cz.datalite.zkdl.demo.hibernateDao;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSearch;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.DLListboxCriteriaController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericComposer;

/**
 * This example is very similar to zkDlComponents - basic listbox usage. But now it is actually connected to the database
 * and the filtering is done by Hibernate Criteria. The DAO is now created without any integration layer - you definitely should
 * use something (Spring, EJB, Google Guice, ...). Reason behind is example is, that it is possible to use it as is and that
 * you can use any integration framework you choose. Meybe then you can check our examle with Spring.
 *
 * @author Jiri Bubnik
 */
public class HibernateDaoController extends GenericComposer
{
    // this DAO will be normaly injected by some framework
    HibernateDAO dao = new HibernateDAO();

    // after compose, before binding, setup some example data.
    @Override
    public void doAfterCompose(Component comp) throws Exception
    {
        super.doAfterCompose(comp);
        dao.setupExampleData();
    }


    // selected item (used via databinding from ZUL)
    HibernateTodo todo;

    // if you don't want to explicitly write these getters and setters, check our DLComposer class (in the ZKComposer module).
    public HibernateTodo getTodo()
    {
        return todo; 
    }

    public void setTodo(HibernateTodo todo)
    {
        this.todo = todo;
    }



    // controller for the main list
    //   we have propared everyting for you - into the Search object via DLListboxCriteriaController.
    //   you can add your own restrictions or you can modify the database quuery - as long as it is based on Hibernate Criteria,
    //   you can do almost anything you want.
    //   but best to use with our GenericDAO, where you have basic method like search() and searchAndCount() already implemented.
    DLListboxController<HibernateTodo> listCtl = new DLListboxCriteriaController<HibernateTodo>("HibernateDaoController") {
        @Override
        protected DLResponse<HibernateTodo> loadData(DLSearch<HibernateTodo> search)
        {
            // all of the basic filters and sorts from standard objects are part of the search object

            // add any other restrictions from custom fields - you can almost construct your enitre query here
            // search.addFilterCriterion(Restrictions.eq("property", value));

            // for @ManyToOne queries, you might specify join type (or default type is added automaticly)
            // search.addAlias("my.path", CriteriaSpecification.LEFT_JOIN);

            // this example shows only basic usage, but we can handle any kind of @ManyToOne, filtering, joining - this
            // is only a thin layer on Hibernate Criteria API

            // the entire logic for standard query is inside generic dao.
            return dao.searchAndCount(search);
        }
    };

    // if you don't want to explicitly write these getters and setters, check our DLComposer class (in the ZKComposer module).
    public DLListboxController<HibernateTodo> getListCtl()
    {
        return listCtl;
    }

}
