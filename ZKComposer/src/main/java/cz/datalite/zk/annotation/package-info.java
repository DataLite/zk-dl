/**
 * Annotations like @ZkEvent, @ZkModel, etc. are used to declaratively bind model-view-composer architecture.
 *
 * These annotations can be used only within DLComposer controller class. They are scanned in doBeforeComposeChildren() or doAfterCompose()
 * and bound to ZK internals as to the annotation meaning.
 *
 * @see cz.datalite.zk.composer.DLComposer
 */
package cz.datalite.zk.annotation;

