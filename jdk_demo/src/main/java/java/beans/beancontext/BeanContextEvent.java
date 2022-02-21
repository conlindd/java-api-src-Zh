/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1997, 2009, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.beans.beancontext;

import java.util.EventObject;

import java.beans.beancontext.BeanContext;

/**
 * <p>
 * <code>BeanContextEvent</code> is the abstract root event class
 * for all events emitted
 * from, and pertaining to the semantics of, a <code>BeanContext</code>.
 * This class introduces a mechanism to allow the propagation of
 * <code>BeanContextEvent</code> subclasses through a hierarchy of
 * <code>BeanContext</code>s. The <code>setPropagatedFrom()</code>
 * and <code>getPropagatedFrom()</code> methods allow a
 * <code>BeanContext</code> to identify itself as the source
 * of a propagated event.
 * </p>
 *
 * <p>
 * <p>
 *  <code> BeanContextEvent </code>是从<code> BeanContext </code>发出并与其语义相关的所有事件的抽象根事件类。
 * 这个类引入了一种机制,允许<code> BeanContextEvent </code>子类通过<code> BeanContext </code>的层次结构的传播。
 *  <code> setPropagatedFrom()</code>和<code> getPropagatedFrom()</code>方法允许<code> BeanContext </code>将自身
 * 标识为传播事件的源。
 * 这个类引入了一种机制,允许<code> BeanContextEvent </code>子类通过<code> BeanContext </code>的层次结构的传播。
 * </p>
 * 
 * 
 * @author      Laurence P. G. Cable
 * @since       1.2
 * @see         java.beans.beancontext.BeanContext
 */

public abstract class BeanContextEvent extends EventObject {
    private static final long serialVersionUID = 7267998073569045052L;

    /**
     * Contruct a BeanContextEvent
     *
     * <p>
     *  Contruct BeanContextEvent
     * 
     * 
     * @param bc        The BeanContext source
     */
    protected BeanContextEvent(BeanContext bc) {
        super(bc);
    }

    /**
     * Gets the <code>BeanContext</code> associated with this event.
     * <p>
     *  获取与此事件关联的<code> BeanContext </code>。
     * 
     * 
     * @return the <code>BeanContext</code> associated with this event.
     */
    public BeanContext getBeanContext() { return (BeanContext)getSource(); }

    /**
     * Sets the <code>BeanContext</code> from which this event was propagated.
     * <p>
     *  设置传播此事件的<code> BeanContext </code>。
     * 
     * 
     * @param bc the <code>BeanContext</code> from which this event
     * was propagated
     */
    public synchronized void setPropagatedFrom(BeanContext bc) {
        propagatedFrom = bc;
    }

    /**
     * Gets the <code>BeanContext</code> from which this event was propagated.
     * <p>
     *  获取传播此事件的<code> BeanContext </code>。
     * 
     * 
     * @return the <code>BeanContext</code> from which this
     * event was propagated
     */
    public synchronized BeanContext getPropagatedFrom() {
        return propagatedFrom;
    }

    /**
     * Reports whether or not this event is
     * propagated from some other <code>BeanContext</code>.
     * <p>
     *  报告此事件是否从某些其他<code> BeanContext </code>传播。
     * 
     * 
     * @return <code>true</code> if propagated, <code>false</code>
     * if not
     */
    public synchronized boolean isPropagated() {
        return propagatedFrom != null;
    }

    /*
     * fields
     * <p>
     *  字段
     * 
     */

    /**
     * The <code>BeanContext</code> from which this event was propagated
     * <p>
     *  传播此事件的<code> BeanContext </code>
     */
    protected BeanContext propagatedFrom;
}
