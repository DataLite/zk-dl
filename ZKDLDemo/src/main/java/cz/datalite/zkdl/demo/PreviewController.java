package cz.datalite.zkdl.demo;

import cz.datalite.helpers.StringHelper;
import cz.datalite.stereotype.Controller;
import cz.datalite.zk.annotation.ZkComponent;
import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkEvents;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.annotation.ZkParameter;
import cz.datalite.zk.composer.DLComposer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.SortedMap;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;

/**
 *
 * @author Jiri Bubnik
 */
@Controller
public class PreviewController extends DLComposer
{
    // current type of the demo (set by parameter)
    @ZkModel DemoType demoType;

    // selected source file to show
    @ZkModel String sourceFile;

    // where to put the source code
    @ZkComponent Label sourceLabel;

    @ZkParameter(createIfNull=true)
    public void setDemoType(String type) throws IOException
    {
        this.demoType = DemoType.valueOf(StringHelper.isNull(type) ? "ZK_DL_COMPONENTS" : type);
        
        // set the source file to the first item in a map
        SortedMap<String, String> sourceMap = this.demoType.getSourceMap();
        if (!sourceMap.isEmpty())
            sourceFile = sourceMap.firstKey();
    }

    @ZkEvents(events={
        @ZkEvent(id="combo", event=Events.ON_SELECT),
        @ZkEvent(id="sourceTab", event=Events.ON_SELECT)
    })
    public void setSourceHtml() throws IOException
    {
        String resource = this.demoType.getSourceMap().get(sourceFile);
        if (resource.endsWith("java"))
            resource = "/src/main/java/" + resource;
        else
            resource = "/src/main/webapp/" + resource;

        InputStream is = this.getClass().getResourceAsStream(resource);
        sourceLabel.invalidate();
        sourceLabel.setValue(convertStreamToString(is));
    }

    public String convertStreamToString(InputStream is)
            throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }


}
