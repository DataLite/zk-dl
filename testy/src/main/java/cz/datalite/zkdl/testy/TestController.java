package cz.datalite.zkdl.testy;

import cz.datalite.zk.annotation.ZkEvent;
import cz.datalite.zk.annotation.ZkRole;
import cz.datalite.zk.composer.DLBinder;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.util.Clients;


public class TestController extends DLBinder {

    int progressCount;
    Thread threadToKill;

    @Command
    @ZkRole(value="AYx", disableComponents = {"btnEdit", "btnSave"})
    public void click() {
        Clients.showNotification("Ok");
    }

//    @Command
//    @ZkAsync(doAfter = "onAfter", doProgress= ZkEvents.ON_ASYNC_PROGRESS, progressInterval = 500, message = "Test", cancellable = true)
//    public void click() {
//        // tohle jsou tridy v DLHleper, udrzuje se v thread local a je tedy mozne volat i v Service/DAO
//        ZkCancellable.get().addCommand(new ZkCancelCommand() {
//            @Override
//            public void cancel() {
//                Clients.showNotification("ALTER SYSTEM KILL SESSION 'sid,serial#'; nebo lepe PreparedStatement.cancel();");
//                threadToKill.interrupt();
//            }
//        });
//
//        // simulace behu
//        threadToKill = Thread.currentThread();
//        progressCount = 1;
//        try {
//            Thread.sleep(5000);
//         } catch (InterruptedException e) {
//        }
//    }

    @ZkEvent(event = "onAfter")
    public void after() {
        Clients.showNotification("Ukonceno");
    }

    @ZkEvent(event = "onProgress")
    public void progress() {
        Clients.showNotification("Progress " + ++progressCount, self);
    }
}