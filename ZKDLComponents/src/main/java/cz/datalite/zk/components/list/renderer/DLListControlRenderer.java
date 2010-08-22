//package cz.datalite.zk.components.list.renderer;
//
//import cz.datalite.zk.components.list.view.DLListControl;
//import cz.datalite.zk.components.list.view.DLListboxManager;
//import cz.datalite.zk.components.list.view.DLQuickFilter;
//import cz.datalite.zk.components.paging.DLPaging;
//import java.io.IOException;
//import java.io.Writer;
//import java.util.List;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.HtmlBasedComponent;
//import org.zkoss.zk.ui.render.ComponentRenderer;
//import org.zkoss.zk.ui.render.SmartWriter;
//import org.zkoss.zul.Separator;
//
///**
// * Renderer pro komponentu DLListControl, která řídí zobrazení
// * listbox manager / paging / quick filter.
// *
// * @author Michal Pavlusek
// */
//public class DLListControlRenderer implements ComponentRenderer
//{
//    public void render(Component comp, Writer out) throws IOException
//    {
//
//        final SmartWriter wh = new SmartWriter(out);
//        final DLListControl self = (DLListControl) comp;
//
//        // proměné ze zul
//        final boolean paging = self.isPaging();
//        final boolean qfilter = self.isQfilter();
//        final boolean manager = self.isManager();
//
//        // objekty na vykreslení
//        final DLQuickFilter qFilterComponent = self.getQFilterComponent();
//        final DLListboxManager managerComponent = self.getManagerComponent();
//        final DLPaging pagingComponent = self.getPagingComponent();
//
//        final String uuid = self.getUuid();
//
//            wh.write("<div style=\"height: 25px; width: ").write(self.getWidth()).write(";").write(self.getStyle()).write("\" ")
//            .write("class=\"z-paging ").write(self.getSclass()).write("\" ").write("id=\"").write(uuid).write("\" name=\"")
//            .write(uuid).write("\" z.type=\"zul.pg.Pg\"").write("z.zcls=\"z-paging\"")
//            .write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
//
//        // mám zaplý paging
//        if(paging)
//        {
//            pagingComponent.redraw(out);
//
//            wh.write("<div style=\"position: relative;\">");
//            wh.write("<table style=\"position: absolute; top: -30px; right: 100px; float: right;\"><tr><td>");
//
//            if(qfilter)
//            {
//                qFilterComponent.redraw(out);
//            }
//
//            wh.write("</td><td>");
//
//            if(manager)
//            {
//                managerComponent.redraw(out);
//            }
//
//            wh.write("</td>");
//
//            // zkusím vypsat děti
//            writeChild(comp, wh, out);
//
//            wh.write("</tr></table>");
//
//            wh.write("</div>");
//
//        }
//        else    // nemám zaplý paging
//        {
//            wh.write("<table width=\"100%\"><tr><td width='1%'><span style='padding-left:3px'/></td><td width='1%'>");
//
//            if(qfilter)
//            {
//                wh.write("<div class='pointer'>");
//                qFilterComponent.redraw(out);
//                wh.write("</div>");
//            }
//
//            wh.write("</td>");
//
//            wh.write("<td><span style='padding-left:5px'/></td>");
//
//
//            // zkusím vypsat děti
//            writeChild(comp, wh, out);
//
//            wh.write("<td width='1%'>");
//            if(manager)
//            {
//                wh.write("<div style='position: relative; right: 25px; margin-left: 50px;'>");
//                managerComponent.redraw(out);
//                wh.write("</div>");
//            }
//
//            wh.write("</td>");
//
//            wh.write("</tr></table>");
//
//        }
//
//        wh.write("</div>");
//
//    }
//
//    /**
//     * Funkce se pokusí vypsat děti, pokud nějaké jsou.
//     */
//    private void writeChild(Component comp, SmartWriter wh, Writer out) throws IOException
//    {
//        Separator oddelovac =  new Separator();
//        oddelovac.setBar(true);
//        oddelovac.setOrient("vertical");
//
//        boolean prvni = true;
//
//        for (Component child : (List<Component>) comp.getChildren())
//        {
//            if((child instanceof DLQuickFilter) || (child instanceof DLListboxManager) || (child instanceof DLPaging))
//            {
//                continue;
//            }
//
//            if ((child instanceof HtmlBasedComponent) && ((HtmlBasedComponent)child).getWidth() != null)
//                wh.write("<td width=" + ((HtmlBasedComponent)child).getWidth() + ">");
//            else
//                wh.write("<td width='1%'>");
//
//            if(!prvni)
//            {
//                wh.write("<div style=\"position: relative; top: -3px;\">");
//                oddelovac.redraw(out);
//                wh.write("</div></td><td width='1%'>");
//            }
//            wh.write("<div style=\"position: relative; top: -3px;\">");
//            child.redraw(out);
//            wh.write("</div></td>");
//
//            prvni = false;
//        }
//    }
//
//}
