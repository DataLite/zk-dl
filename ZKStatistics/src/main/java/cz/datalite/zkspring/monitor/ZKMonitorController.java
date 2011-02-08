package cz.datalite.zkspring.monitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.SimpleTreeModel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * Monitor controller (to monitor.zul)
 *
 * @author Michal Pavlusek
 */
public class ZKMonitorController extends GenericForwardComposer
{
    private Collection allRequests;              // all request collection
    private SimpleTreeModel treeModel;          // object call tree
    private HashMap<String, Long> desktopTimeMap = new HashMap();    // id_desktop -> last call at (for sorting)
    
    /**
     * Call tree renderer
     *
     * Three columns - name, call at, call duration
     */
    private TreeitemRenderer treeItemRenderer = new TreeitemRenderer()
    {
        public void render(Treeitem item, Object data) throws Exception
        {
            ZKRequestMonitorMethod requestData = (ZKRequestMonitorMethod)data;
            Treecell tcName = new Treecell(requestData.getName());
            Treecell tcTime = new Treecell("" + convertDate(requestData.getStartTime()));
            Treecell tcDuration = new Treecell("" + requestData.getDuration());
            Treerow tr = null;

            if(item.getTreerow() == null)
            {
                tr = new Treerow();
                tr.setParent(item);
            }
            else
            {
                tr = item.getTreerow();
                tr.getChildren().clear();
            }
            // add treecell to treerow
            tcName.setParent(tr);
            tcTime.setParent(tr);
            tcDuration.setParent(tr);
            item.setOpen(true);
        }
    };

    // comparator for statistics sorting
    private Comparator requestComparator = new Comparator()
    {
            public int compare(Object o1, Object o2)
            {
                ZKRequestMonitor s1 = (ZKRequestMonitor) o1;
                ZKRequestMonitor s2 = (ZKRequestMonitor) o2;

                if (s1.getDesktopId().equals(s2.getDesktopId()))
                {
                    return (int)( s1.getTimeStartAtServer() - s2.getTimeStartAtServer() );
                }
                else
                {
                    Long long1 = desktopTimeMap.get(s1.getDesktopId());
                    Long long2 = desktopTimeMap.get(s2.getDesktopId());
                    // time from hasmap
                    if(long1 == long2)
                    {
                        return 0;
                    }
                    else if(long1 < long2)
                    {
                        return 1;
                    }
                    else if(long1 > long2)
                    {
                        return -1;
                    }
                }
                return 0;   // this should never happen
            }
        };

        
    // Inject from ZUL
    Listbox request;
    Tree tree;

    @Override
    public void doAfterCompose(Component comp) throws Exception
    {
        super.doAfterCompose(comp);
        comp.setVariable("ctl", this, true);

        onRefresh();
    }

    /**
     * Clear all statistics data from session
     *
     */
    public void onClearData()
    {
        session.removeAttribute("RequestStatisticsMap");
        onRefresh();
    }

    public void onEnableMonitor()
    {
        session.setAttribute("RequestStatisticsEnabled", true);
        self.getFellow("enableMonitor").setVisible(false);
        self.getFellow("disableMonitor").setVisible(true);
    }

    public void onDisableMonitor()
    {
        self.getFellow("enableMonitor").setVisible(true);
        self.getFellow("disableMonitor").setVisible(false);
        
        session.removeAttribute("RequestStatisticsEnabled");
        session.removeAttribute("RequestStatisticsMap");
        onRefresh();
    }

    public void onRefresh()
    {
        // get statistics from session
        if(session.getAttribute("RequestStatisticsMap") != null)
        {
            allRequests = ((Map)session.getAttribute("RequestStatisticsMap")).values();
        }
        else
        {
            allRequests = new ArrayList();
        }

        fillRequestTable();
    }


    /**
     * Fills data into request table
     */
    private void fillRequestTable()
    {
        // remove all previous data
        List toRemove = new LinkedList(request.getChildren());
        for (Component c : (List<Component>) toRemove)
        {
            if (!(c instanceof Listhead))
            {
                request.removeChild(c);
            }
        }


        ArrayList usedDesktops = new ArrayList();   // used desktops
        List listRequests = new ArrayList(allRequests); // list statistics

        
        // fill list
        for(Object st : listRequests)
        {
            ZKRequestMonitor stat = (ZKRequestMonitor) st;

            // is this desktop already in list?
            if(desktopTimeMap.containsKey(stat.getDesktopId()))
            {
                // is the time in list less than mine?
                if( ((Long)desktopTimeMap.get(stat.getDesktopId())) < stat.getTimeStartAtServer())
                {
                    desktopTimeMap.put(stat.getDesktopId(), stat.getTimeStartAtServer());
                }
                else    // time in list more then mine
                {
                    continue;
                }
            }
            else    // my desktop not in list
            {
                desktopTimeMap.put(stat.getDesktopId(), stat.getTimeStartAtServer());
            }
        }


        // sort list by time
        Collections.sort(listRequests,requestComparator);

        // for all statistics
        for(Object objStats : listRequests)
        {
            ZKRequestMonitor stat = (ZKRequestMonitor) objStats;
            String desktopText = stat.getContextPath() + " (" + stat.getDesktopId() + ") -> ";

            // if statistics not yet in my array, add it to the page
            if(! usedDesktops.contains(desktopText))
            {
                Listgroup newGroup = new Listgroup();

                String desktopLive = "";
                String desktopScopedEntities = "";
                Desktop statDesktop = ((SessionCtrl)session).getDesktopCache().getDesktopIfAny(stat.getDesktopId());
                // it the desktop is still available (not GC)
                
                if (statDesktop != null)
                {
                    desktopLive = " (live) ";
                    // and entity manager is desktop scoped)
                    EntityManager entityManager = (EntityManager) statDesktop.getAttribute("HibernateEntityManager");
                    if (entityManager != null && entityManager.getDelegate() instanceof Session)
                    {
                       desktopScopedEntities = "   Cached entities: " +
                               ((Session)entityManager.getDelegate()).getStatistics().getEntityCount();

                    }
                }

                DateFormat format = new SimpleDateFormat("d.M.yyyy H:mm:ss");
                newGroup.setLabel(desktopText + " " + format.format(new Date(stat.getTimeStartAtServer())) +
                        desktopLive + desktopScopedEntities);
                newGroup.setParent(request);
                newGroup.setOpen(false);
                usedDesktops.add(desktopText);
            }

            // new data  row
            Listitem newItem = new Listitem();
            newItem.setParent(request);
            newItem.setValue(stat);
            newItem.addEventListener(Events.ON_CLICK, new EventListener()
            {
                public void onEvent(Event event) throws Exception
                {
                    // new root (root is not shown in tree)
                    ZKRequestMonitorMethod root = new ZKRequestMonitorMethod("root");

                                                // take  event -> listitem -> statistics -> tree model
                    root.getChildren().add(((ZKRequestMonitor)((Listitem)event.getTarget()).getValue()).getParentInvocation());

                    treeModel = new SimpleTreeModel( root );
                    tree.setModel(treeModel);
                }
            });

            // fill in data
            newCell(stat.getContextPath(), newItem, "padding-left: 20px;");
            newCell(stat.getRequestId(), newItem);
            newCell(converzeData(stat.getOverallDuration()), newItem);
            newCell(converzeData(stat.getNetworkLatency()), newItem);
            newCell(converzeData(stat.getBrowserExecution()), newItem);
            newCell(convertDate(stat.getTimeStartAtClient()), newItem);
            newCell(convertDate(stat.getTimeStartAtServer()), newItem);
            newCell(convertDate(stat.getTimeCompleteAtServer()), newItem);
            newCell(convertDate(stat.getTimeRecieveAtClient()), newItem);
            newCell(convertDate(stat.getTimeCompleteAtClient()), newItem);
        }
    }

    /**
     * Add new cell to the page
     *
     * @param label 
     * @param item label parent
     */
    private void newCell(String label, Listitem item)
    {
        Listcell cell = new Listcell();
        cell.setLabel(label);
        cell.setParent(item);
    }

    /**
     * Add new cell with style
     *
     * 
     * @param label description
     * @param item label parent
     * @param style CSS style
     */
    private void newCell(String label, Listitem item, String style)
    {
        Listcell cell = new Listcell();
        cell.setLabel(label);
        cell.setStyle(style);
        cell.setParent(item);
    }

    /**
     * Convert date to string with format H:mm:ss.S
     *
     * @param datum date
     * @return string 0 for empty or datum
     */
    private String convertDate(long datum)
    {
        DateFormat format = new SimpleDateFormat("H:mm:ss.S");
         return (datum == 0) ? "0" : format.format(new Date(datum));
    }

    /**
     * Convert date to string with format H:mm:ss.S
     * 
     * @param datum date
     * @return string 0 for empty or datum
     */
    private String converzeData(Long datum)
    {
        return (datum == null) ? "" : datum.toString();
    }


    /******************** GETTERS ***************************************/
    public Collection getAllRequests() {
        return allRequests;
    }


    /********************** SETTERS *************************************/
    public void setAllRequests(Collection allRequests) {
        this.allRequests = allRequests;
    }

    /**
     * @return the tree
     */
    public SimpleTreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * @return the renderer
     */
    public TreeitemRenderer getTreeItemRenderer() {
        return treeItemRenderer;
    }


}
