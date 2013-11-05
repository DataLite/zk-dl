package cz.datalite.zkdl.testy;

import cz.datalite.dao.DLResponse;
import cz.datalite.dao.DLSort;
import cz.datalite.stereotype.Controller;
import cz.datalite.zk.annotation.ZkController;
import cz.datalite.zk.annotation.ZkModel;
import cz.datalite.zk.components.list.DLFilter;
import cz.datalite.zk.components.list.DLListboxSimpleController;
import cz.datalite.zk.components.list.filter.NormalFilterUnitModel;
import cz.datalite.zk.components.lovbox.DLLovboxController;
import cz.datalite.zk.components.lovbox.DLLovboxGeneralController;
import cz.datalite.zk.composer.DLBinder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.Arrays;
import java.util.List;

/**
 * Test
 */
@Controller
public class TestLovboxController extends DLBinder {

    @ZkModel
    String stringData = "petr";

    List<String> stringList = Arrays.asList("petr", "pavel", "martin martin martin martin martin martin martin martin martin martin", "michal", "jan", "vasek",
            "petr", "pavel", "martin", "michal", "jan", "vasek","petr", "pavel", "martin", "michal", "jan", "vasek");

    List<BindValue> valueList = Arrays.asList(new BindValue("petr"), new BindValue("pavel"), new BindValue("martin"), new BindValue("michal"));

    public static class BindValue {
        public String value;
        public BindValue(String value) { this.value = value; }

        public String getValue() { return value; }

        public void setValue(String value) { this.value = value; }
    }

    @ZkController
    DLLovboxController<String> lovbox = new DLLovboxGeneralController<String>(
            new DLListboxSimpleController<String>("Test") {
                @Override
                protected DLResponse<String> loadData(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts) {
                    return DLFilter.filterAndCount(filter, stringList, firstRow, rowCount, sorts);
                }
            }
    );

    @ZkController
    DLLovboxController<BindValue> bindValueLovbox = new DLLovboxGeneralController<BindValue>(
            new DLListboxSimpleController<BindValue>("Test") {
                @Override
                protected DLResponse<BindValue> loadData(List<NormalFilterUnitModel> filter, int firstRow, int rowCount, List<DLSort> sorts) {
                    return DLFilter.filterAndCount(filter, valueList, firstRow, rowCount, sorts);
                }
            }
    );


    @Command
    @NotifyChange("stringData")
    public void select() {
        System.out.println(stringData);
    }
}
