package cz.datalite.zkdl.demo;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Jiri Bubnik
 */
public enum DemoType
{
    ZUL_ONLY(
        new String[] {"/zulOnly/dlListbox.zul"}
    ),
    ZK_DL_COMPONENTS(
        new String[] {
            "/zkDlComponents/dataDriven.zul",
            "cz.datalite.zkdl.demo.zkDlComponents.DataDrivenController",
            "cz.datalite.zkdl.demo.zkDlComponents.SimpleTodo"
        }
    ),
    ZK_COMPOSER(
        new String[] {
            "/zkComposer/zkComposer.zul",
            "cz.datalite.zkdl.demo.zkComposer.ZkComposerController"
        }
    ),
    HIBERNATE_DAO(
        new String[] {
            "/hibernateDao/hibernateDao.zul",
            "cz.datalite.zkdl.demo.hibernateDao.HibernateDaoController",
            "cz.datalite.zkdl.demo.hibernateDao.HibernateDAO",
            "cz.datalite.zkdl.demo.hibernateDao.HibernateTodo"
        }
    ),
    SPRING(
        new String[] {
            "/spring/todoOverview.zul",
            "/spring/todoDetail.zul",
            "cz.datalite.zkdl.demo.spring.model.Todo",
            "cz.datalite.zkdl.demo.spring.dao.TodoDAO",
            "cz.datalite.zkdl.demo.spring.dao.impl.TodoDAOImpl",
            "cz.datalite.zkdl.demo.spring.service.TodoService",
            "cz.datalite.zkdl.demo.spring.service.impl.TodoServiceImpl",
            "cz.datalite.zkdl.demo.spring.web.TodoOverviewController",
            "cz.datalite.zkdl.demo.spring.web.TodoDetailController"
        }
    );

    String[] sourceFiles;
    SortedMap<String, String> sourceMap;

    DemoType(String[] sourceFiles)
    {
        this.sourceFiles = sourceFiles;
        this.sourceMap = createSourceMap();
    }

    protected SortedMap<String, String> createSourceMap()
    {
        SortedMap<String, String> map = new TreeMap<String, String>();
        for (String sourceFile : sourceFiles)
        {
            // ZUL
            if (sourceFile.endsWith("zul"))
            {
                String name = sourceFile.replaceAll(".*\\/", "");
                map.put(name, sourceFile);
            }
            else
            {
                String name = sourceFile.replaceAll(".*\\.", "") + ".java";
                String location = sourceFile.replaceAll("\\.", "/") + ".java";
                map.put(name, location);
            }
        }

        return map;
    }

    public SortedMap<String, String> getSourceMap()
    {
        return sourceMap;
    }

    public String getZul()
    {
        return sourceFiles[0];
    }
    
}
