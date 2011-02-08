package cz.datalite.zkdl.demo.zkDlComponents;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.zk.components.list.DLFilter;
import cz.datalite.zk.components.list.DLListboxController;
import cz.datalite.zk.components.list.DLListboxSimpleController;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.zkoss.zk.ui.util.GenericComposer;

/**
 *
 * @author Jiri Bubnik
 */
public class DataDrivenController extends GenericComposer
{
    // just init some variables for dates
    Calendar today = Calendar.getInstance();
    Calendar tomorrow = new GregorianCalendar() {{
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 1);
        setTime(date.getTime());
    }};

    // model - prepare same data - normally you will load them from database or some service layer
    List<SimpleTodo> simpleTodoModel = Arrays.asList(
                new SimpleTodo("Check out ZK-DL", "Download maven artifact and start today!", today.getTime()),
                new SimpleTodo("Look at ZK-DL Composer", "You can read your controller code like a story with these nice annotations", today.getTime()),
                new SimpleTodo("Get rid of getters/setters", "DLComposer has many more use", tomorrow.getTime()),                
                new SimpleTodo("Ease of development - Maven", "You can start your own demo project with a  simple maven artifact.", tomorrow.getTime()),
                new SimpleTodo("Ease of development - Jetty", "Just write mvn jetty:run in a commmand line and launch our demo in 5 seconds.", tomorrow.getTime()),
                new SimpleTodo("Ease of development - JRebel", "No need to restart the server, just install JRebel - save source & refresh in the browser.", tomorrow.getTime()),
                new SimpleTodo("Test ZK with Selenium 2.0 (WebDriver)", "While still in beta, this testing style is very promissing", tomorrow.getTime())
            );


    // selected item (used via databinding from ZUL)
    SimpleTodo todo;

    // if you don't want to explicitly write these getters and setters, check our DLComposer class (in the ZKComposer module).
    public SimpleTodo getSimpleTodo()
    {
        return todo;
    }

    public void setSimpleTodo(SimpleTodo SimpleTodo)
    {
        this.todo = SimpleTodo;
    }


    // controller for the main list
    //   if you want to filter and sort data manually, use DLListboxSimpleController, we have other use like
    //   DLListboxCriteriaController for automatic mapping to Hibernate Criteria API or event DLListboxLiferayController to
    //   work with Liferay Portal API. Check implemetnation of some of the classes
    DLListboxController<SimpleTodo> listCtl = new DLListboxSimpleController<SimpleTodo>("SimpleTodoController")
    {
        // Listbox controller is always abstract - just implement the method, all parameters are prepared for you according to controller type
        @Override
        protected DLResponse<SimpleTodo> loadData(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts)
        {
            // we even prepared a class to work with in memory model (items in model have to comply to javabean standard)
            return DLFilter.filterAndCount(filter, simpleTodoModel, firstRow, rowCount, sorts);
        }
    };

    // if you don't want to explicitly write these getters and setters, check our DLComposer class (in the ZKComposer module).
    public DLListboxController<SimpleTodo> getListCtl()
    {
        return listCtl;
    }

}
