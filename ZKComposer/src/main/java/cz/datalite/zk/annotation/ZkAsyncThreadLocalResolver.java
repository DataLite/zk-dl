package cz.datalite.zk.annotation;

/**
 * Copy thread local state from request thread to the async thread.
 * <p/>
 * Implementing class  should be registered as library property
 * 'zk-dl.annotation.ZkAsync.ZkAsyncThreadLocalResolver'  in zk.xml.
 * <p/>
 * This is used by ZkAsyncHandler.
 */
public interface ZkAsyncThreadLocalResolver {

    /**
     * Return thread local state from application (request) thread.
     *
     * @return any state obejct that will be passed to setCurrentThreadLocalState in async thread.
     */
    Object getCurrentThreadLocalState();

    /**
     * Setup thread local state to the new thread.
     *
     * @param state state stored in application thread by getCurrentThreadLocalState
     */
    void setCurrentThreadLocalState(Object state);
}
