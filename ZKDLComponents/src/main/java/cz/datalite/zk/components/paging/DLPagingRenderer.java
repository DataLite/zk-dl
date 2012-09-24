//package cz.datalite.zk.components.paging;
//
//import java.io.IOException;
//import java.io.Writer;
//import org.zkoss.zk.ui.Component;
//import org.zkoss.zk.ui.render.ComponentRenderer;
//import org.zkoss.zk.ui.render.SmartWriter;
//
///**
// * {@link org.zkoss.zul.Paging}'s default mold.
// *
// * @author Jeff Liu
// * @author Karel Cemus created  10.8.2009, last edit 28.8.2009
// * @since 3.0.0
// */
//public class DLPagingRenderer implements ComponentRenderer{
//
//	public void render( final Component comp, final  Writer out ) throws IOException {
//		final SmartWriter wh = new SmartWriter(out);
//		final DLPaging self = (DLPaging) comp;
//		final String zcls = self.getZclass();
//		final String uuid = self.getUuid();
//		wh.write("<div id=\"").write(uuid).write("\" name=\"")
//			.write(uuid).write("\" z.type=\"zul.pg.Pg\"");
//		wh.write(self.getOuterAttrs()).write(self.getInnerAttrs()).write(">");
//
//		wh.write("<table cellspacing=\"0\"><tbody><tr>");
//		wh.write("<td><table id=\"").write(uuid+"!tb_f")
//			.write("\" name=\"").write(uuid+"!tb_f")
//			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
//			.write(zcls).write("-btn\"><tbody><tr><td><div><button type=\"button\" class=\"")
//			.write(zcls).write("-first\"> </button></div></td></tr></tbody></table></td>");
//		wh.write("<td><table id=\"").write(uuid+"!tb_p")
//			.write("\" name=\"").write(uuid+"!tb_p")
//			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"").write(zcls)
//			.write("-btn\"><tbody><tr><td><div><button type=\"button\" class=\"")
//			.write(zcls).write("-prev\"> </button></div></td></tr></tbody></table></td>");
//		wh.write("<td><span class=\"").write(zcls).write("-sep\"/></td>");
//		wh.write("<td><span class=\"").write(zcls).write("-text\"></span></td>");
//		wh.write("<td>").write( self.getPageNumber()).write("</td>");
//		wh.write("<td><span class=\"").write(zcls).write("-text\">/ ").write(self.isKnownPageCount() ? String.valueOf( self.getPageCount()) : "?" )
//			.write("</span></td>");
//		wh.write("<td><span class=\"").write(zcls).write("-sep\"/></td>");
//		wh.write("<td><table id=\"").write(uuid+"!tb_n")
//			.write("\" name=\"").write(uuid+"!tb_n")
//			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
//			.write(zcls).write("-btn\"><tbody><tr><td><div><button type=\"button\" class=\"")
//			.write(zcls).write("-next\"> </button></div></td></tr></tbody></table></td>");
//		wh.write("<td><table id=\"").write(uuid+"!tb_l")
//			.write("\" name=\"").write(uuid+"!tb_l")
//			.write("\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"")
//			.write(zcls).write("-btn\"><tbody><tr><td><div><button type=\"button\" class=\"")
//			.write(zcls).write("-last\"> </button></div></td></tr></tbody></table></td>");
//		wh.write("</tr></tbody></table>");
//
//		if (self.isDetailed())
//			wh.write(self.getInfoTags());
//		wh.write("</div>");
//	}
//}
