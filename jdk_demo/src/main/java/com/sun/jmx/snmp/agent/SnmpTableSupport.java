/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jmx.snmp.agent;



// java imports
//
import java.io.Serializable;
import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

// jmx imports
//
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.NotificationBroadcaster;
import javax.management.MBeanNotificationInfo;
import javax.management.ListenerNotFoundException;
import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.SnmpVarBind;
import com.sun.jmx.snmp.SnmpStatusException;

/**
 * This class is an abstraction for an SNMP table.
 * It is the base class for implementing SNMP tables in the
 * MBean world.
 *
 * <p>
 * Its responsibility is to synchronize the MBean view of the table
 * (Table of entries) with the MIB view (array of OID indexes). Each
 * object of this class will be bound to the Metadata object which
 * manages the same SNMP Table within the MIB.
 * </p>
 *
 * <p>
 * For each table defined in a MIB, mibgen will generate a specific
 * class called Table<i>TableName</i> that will subclass this class, and
 * a corresponding <i>TableName</i>Meta class extending SnmpMibTable
 * and corresponding to the MIB view of the same table.
 * </p>
 *
 * <p>
 * Objects of this class are instantiated by MBeans representing
 * the SNMP Group to which the table belong.
 * </p>
 *
 * <p><b>This API is a Sun Microsystems internal API  and is subject
 * to change without notice.</b></p>
 * <p>
 *  此类是SNMP表的抽象。它是在MBean世界中实现SNMP表的基类。
 * 
 * <p>
 *  它的职责是将表(表条目)的MBean视图与MIB视图(OID索引数组)同步。此类的每个对象将绑定到元数据对象,该元数据对象管理MIB中的相同SNMP表。
 * </p>
 * 
 * <p>
 *  对于在MIB中定义的每个表,mibgen将生成将对该类进行子类化的称为Table </i> TableName </i>的特定类以及扩展SnmpMibTable并且对应于MIB的相应<视图相同的表。
 * </p>
 * 
 * <p>
 *  此类的对象由表示该表所属的SNMP组的MBeans实例化。
 * </p>
 * 
 *  <p> <b>此API是Sun Microsystems的内部API,如有更改,恕不另行通知。</b> </p>
 * 
 * 
 * @see com.sun.jmx.snmp.agent.SnmpTableEntryFactory
 * @see com.sun.jmx.snmp.agent.SnmpMibTable
 *
 */
public abstract class SnmpTableSupport implements SnmpTableEntryFactory,
// NPCTE fix for bugId 4499265, esc 0, MR 04 sept 2001
//  SnmpTableCallbackHandler {
    SnmpTableCallbackHandler, Serializable {
// end of NPCTE fix for bugId 4499265

    //-----------------------------------------------------------------
    //
    //  Protected Variables
    //
    //-----------------------------------------------------------------

    /**
     * The list of entries
     * <p>
     *  条目列表
     * 
     * 
     **/
    protected List<Object> entries;

    /**
     * The associated metadata object
     * <p>
     *  关联的元数据对象
     * 
     * 
     **/
    protected SnmpMibTable meta;

    /**
     * The MIB to which this table belongs
     * <p>
     *  此表所属的MIB
     * 
     * 
     **/
    protected SnmpMib      theMib;

    //-----------------------------------------------------------------
    //
    //  Private Variables
    //
    //-----------------------------------------------------------------

    /**
     * This variable is initialized while binding this object to its
     * corresponding meta object.
     * <p>
     *  当将此对象绑定到其对应的元对象时,将初始化此变量。
     * 
     * 
     **/
    private boolean registrationRequired = false;



    //-----------------------------------------------------------------
    //
    //  Constructor
    //
    //-----------------------------------------------------------------

    /**
     * Initializes the table.
     * The steps are these:
     * <ul><li> allocate an array for storing entry object,</li>
     *     <li> retrieve the corresponding metadata object
     *          from the MIB,
     *     <li> bind this object to the corresponding metadata object
     *          from the MIB.</li>
     * </ul>
     *
     * <p>
     *  初始化表。步骤如下：<ul> <li>分配用于存储条目对象的数组,</li> <li>从MIB检索相应的元数据对象,<li>将此对象绑定到来自MIB的对应元数据对象。 </li>
     * </ul>
     * 
     * 
     * @param mib The MIB to which this table belong.
     *
     **/
    protected SnmpTableSupport(SnmpMib mib) {
        theMib  = mib;
        meta    = getRegisteredTableMeta(mib);
        bindWithTableMeta();
        entries = allocateTable();
    }


    //-----------------------------------------------------------------
    //
    //  Implementation of the SnmpTableEntryFactory interface
    //
    //-----------------------------------------------------------------

    /**
     * Creates a new entry in the table.
     *
     * This factory method is generated by mibgen and used internally.
     * It is part of the
     * {@link com.sun.jmx.snmp.agent.SnmpTableEntryFactory} interface.
     * You may subclass this method to implement any specific behaviour
     * your application requires.
     *
     * <p>
     *  在表中创建一个新条目。
     * 
     * 此工厂方法由mibgen生成并在内部使用。它是{@link com.sun.jmx.snmp.agent.SnmpTableEntryFactory}接口的一部分。
     * 您可以将此方法子类化以实现应用程序需要的任何特定行为。
     * 
     * 
     * @exception SnmpStatusException if the entry cannot be created.
     **/
    public abstract void createNewEntry(SnmpMibSubRequest request,
                                        SnmpOid rowOid, int depth,
                                        SnmpMibTable meta)
        throws SnmpStatusException;


    //-----------------------------------------------------------------
    //
    //  Public methods
    //
    //-----------------------------------------------------------------

    /**
     * Returns the entry located at the given position in the table.
     *
     * <p>
     *  返回位于表中给定位置的条目。
     * 
     * 
     * @return The entry located at the given position, <code>null</code>
     *         if no entry can be found at this position.
     **/
    // XXXX xxxx zzz ZZZZ => public? or protected?
    public Object getEntry(int pos) {
        if (entries == null) return null;
        return entries.get(pos);
    }

    /**
     * Returns the number of entries registered in the table.
     *
     * <p>
     *  返回在表中注册的条目数。
     * 
     * 
     * @return The number of entries registered in the table.
     **/
    public int getSize() {
        return meta.getSize();
    }

    /**
     * This method lets you dynamically switch the creation policy.
     *
     * <CODE>setCreationEnabled()</CODE> will switch the policy of
     *      remote entry creation via SET operations, by calling
     *      <code>setCreationEnabled()</code> on the metadata object
     *      associated with this table.
     * <BR> By default remote entry creation via SET operation is disabled.
     *
     * <p>
     *  此方法允许您动态切换创建策略。
     * 
     *  <CODE> setCreationEnabled()</CODE>将通过在与此表关联的元数据对象上调用<code> setCreationEnabled()</code>来切换通过SET操作创建远程
     * 条目的策略。
     *  <BR>默认情况下,通过SET操作创建远程条目被禁用。
     * 
     * 
     * @param remoteCreationFlag Tells whether remote entry creation must
     *        be enabled or disabled.
     * <li>
     * <CODE>setCreationEnabled(true)</CODE> will enable remote entry
     *      creation via SET operations.</li>
     * <li>
     * <CODE>setCreationEnabled(false)</CODE> will disable remote entry
     *      creation via SET operations.</li>
     * <p> By default remote entry creation via SET operation is disabled.
     * </p>
     *
     * @see com.sun.jmx.snmp.agent.SnmpMibTable
     *
     **/
    public void setCreationEnabled(boolean remoteCreationFlag) {
        meta.setCreationEnabled(remoteCreationFlag);
    }

    /**
     * Tells whether a new entry should be created when a SET operation
     * is received for an entry that does not exist yet.
     * This method calls <code>isCreationEnabled()</code> on the metadata
     * object associated with this table.
     *
     * <p>
     *  告知当接收到尚不存在的条目的SET操作时是否应创建新条目。此方法在与此表关联的元数据对象上调用<code> isCreationEnabled()</code>。
     * 
     * 
     * @return true if a new entry must be created, false otherwise.<br>
     *         [default: returns <CODE>false</CODE>]
     *
     * @see com.sun.jmx.snmp.agent.SnmpMibTable
     **/
    public boolean isCreationEnabled() {
        return meta.isCreationEnabled();
    }

    /**
     * Tells whether the metadata object to which this table is linked
     * requires entries to be registered. In this case passing an
     * ObjectName when registering entries will be mandatory.
     *
     * <p>
     *  指出此表链接到的元数据对象是否需要注册条目。在这种情况下,在注册条目时传递ObjectName将是必需的。
     * 
     * 
     * @return <code>true</code> if the associated metadata requires entries
     *         to be registered (mibgen generated generic metadata).
     **/
    public boolean isRegistrationRequired() {
        return registrationRequired;
    }

    /**
     * Builds an entry SnmpIndex from its row OID.
     *
     * This method is generated by mibgen and used internally.
     *
     * <p>
     *  从其行OID构建一个条目SnmpIndex。
     * 
     *  此方法由mibgen生成并在内部使用。
     * 
     * 
     * @param rowOid The SnmpOid object identifying a table entry.
     *
     * @return The SnmpIndex of the entry identified by <code>rowOid</code>.
     *
     * @exception SnmpStatusException if the index cannot be built from the
     *            given OID.
     **/
    public SnmpIndex buildSnmpIndex(SnmpOid rowOid)
        throws SnmpStatusException {
        return buildSnmpIndex(rowOid.longValue(false), 0);
    }

    /**
     * Builds an SnmpOid from an SnmpIndex object.
     *
     * This method is generated by mibgen and used internally.
     *
     * <p>
     *  从SnmpIndex对象构建SnmpOid。
     * 
     *  此方法由mibgen生成并在内部使用。
     * 
     * 
     * @param index An SnmpIndex object identifying a table entry.
     *
     * @return The SnmpOid form of the given entry index.
     *
     * @exception SnmpStatusException if the given index is not valid.
     **/
    public abstract SnmpOid buildOidFromIndex(SnmpIndex index)
        throws SnmpStatusException;

    /**
     * Builds the default ObjectName of an entry from the SnmpIndex
     * identifying this entry. No access is made on the entry itself.
     *
     * This method is generated by mibgen and used internally.
     * You can subclass this method if you want to change the default
     * ObjectName policy. This is only meaningfull when entries
     * are registered MBeans.
     *
     * <p>
     *  从标识此条目的SnmpIndex构建条目的默认ObjectName。不能对条目本身进行访问。
     * 
     * 此方法由mibgen生成并在内部使用。如果要更改默认ObjectName策略,可以对此方法进行子类化。这只有在条目注册MBean时才有意义。
     * 
     * 
     * @param index The SnmpIndex identifying the entry from which we
     *              want to build the default ObjectName.
     *
     * @return The default ObjectName for the entry identified by
     *         the given index.
     *
     * @exception SnmpStatusException if the given index is not valid.
     **/
    public abstract ObjectName buildNameFromIndex(SnmpIndex index)
        throws SnmpStatusException;


    //-----------------------------------------------------------------
    //
    //  Implementation of the SnmpTableEntryFactory interface
    //
    //-----------------------------------------------------------------

    /**
     * This callback is called by  the associated metadata object
     * when a new table entry has been registered in the
     * table metadata.
     *
     * This method will update the <code>entries</code> list.
     *
     * <p>
     *  当在表元数据中注册了新的表条目时,该回调由关联的元数据对象调用。
     * 
     *  此方法将更新<code>条目</code>列表。
     * 
     * 
     * @param pos   The position at which the new entry was inserted
     *              in the table.
     * @param row   The row OID of the new entry
     * @param name  The ObjectName of the new entry (as specified by the
     *              factory)
     * @param entry The new entry (as returned by the factory)
     * @param meta  The table metadata object.
     *
     **/
    public void addEntryCb(int pos, SnmpOid row, ObjectName name,
                           Object entry, SnmpMibTable meta)
        throws SnmpStatusException {
        try {
            if (entries != null) entries.add(pos,entry);
        } catch (Exception e) {
            throw new SnmpStatusException(SnmpStatusException.noSuchName);
        }
    }

    /**
     * This callback is called by  the associated metadata object
     * when a new table entry has been removed from the
     * table metadata.
     *
     * This method will update the <code>entries</code> list.
     *
     * <p>
     *  当已从表元数据中删除新的表条目时,由相关联的元数据对象调用此回调。
     * 
     *  此方法将更新<code>条目</code>列表。
     * 
     * 
     * @param pos   The position from which the entry was deleted
     * @param row   The row OID of the deleted entry
     * @param name  The ObjectName of the deleted entry (may be null if
     *              ObjectName's were not required)
     * @param entry The deleted entry (may be null if only ObjectName's
     *              were required)
     * @param meta  The table metadata object.
     *
     **/
    public void removeEntryCb(int pos, SnmpOid row, ObjectName name,
                              Object entry, SnmpMibTable meta)
        throws SnmpStatusException {
        try {
            if (entries != null) entries.remove(pos);
        } catch (Exception e) {
        }
    }



    /**
     * Enables to add an SNMP entry listener to this
     * <CODE>SnmpMibTable</CODE>.
     *
     * <p>
     *  允许向此<CODE> SnmpMibTable </CODE>添加SNMP条目侦听器。
     * 
     * 
     * @param listener The listener object which will handle the
     *    notifications emitted by the registered MBean.
     *
     * @param filter The filter object. If filter is null, no filtering
     *    will be performed before handling notifications.
     *
     * @param handback The context to be sent to the listener when a
     *    notification is emitted.
     *
     * @exception IllegalArgumentException Listener parameter is null.
     */
    public void
        addNotificationListener(NotificationListener listener,
                                NotificationFilter filter, Object handback) {
        meta.addNotificationListener(listener,filter,handback);
    }

    /**
     * Enables to remove an SNMP entry listener from this
     * <CODE>SnmpMibTable</CODE>.
     *
     * <p>
     *  允许从此<CODE> SnmpMibTable </CODE>中删除SNMP条目侦听器。
     * 
     * 
     * @param listener The listener object which will handle the
     *    notifications emitted by the registered MBean.
     *    This method will remove all the information related to this
     *    listener.
     *
     * @exception ListenerNotFoundException The listener is not registered
     *    in the MBean.
     */
    public synchronized void
        removeNotificationListener(NotificationListener listener)
        throws ListenerNotFoundException {
        meta.removeNotificationListener(listener);
    }

    /**
     * Returns a <CODE>NotificationInfo</CODE> object containing the
     * notification class and the notification type sent by the
     * <CODE>SnmpMibTable</CODE>.
     * <p>
     *  返回一个包含由<CODE> SnmpMibTable </CODE>发送的通知类和通知类型的<CODE> NotificationInfo </CODE>对象。
     * 
     */
    public MBeanNotificationInfo[] getNotificationInfo() {
        return meta.getNotificationInfo();
    }

    //-----------------------------------------------------------------
    //
    //  Protected Abstract methods
    //
    //-----------------------------------------------------------------

    /**
     * Builds an SnmpIndex object from the index part of an OID.
     *
     * This method is generated by mibgen and used internally.
     *
     * <p>
     *  从OID的索引部分构建SnmpIndex对象。
     * 
     *  此方法由mibgen生成并在内部使用。
     * 
     * 
     * @param oid The OID from which to build the index, represented
     *        as an array of long.
     * @param start The position where to start from in the OID array.
     *
     * @return The SnmpOid form of the given entry index.
     *
     * @exception SnmpStatusException if the given index is not valid.
     **/
    protected abstract SnmpIndex buildSnmpIndex(long oid[], int start )
        throws SnmpStatusException;

    /**
     * Returns the metadata object associated with this table.
     *
     * This method is generated by mibgen and used internally.
     *
     * <p>
     *  返回与此表关联的元数据对象。
     * 
     *  此方法由mibgen生成并在内部使用。
     * 
     * 
     * @param mib The SnmpMib object holding the Metadata corresponding
     *            to this table.
     *
     * @return The metadata object associated with this table.
     *         Returns <code>null</code> if this implementation of the
     *         MIB doesn't support this table.
     **/
    protected abstract SnmpMibTable getRegisteredTableMeta(SnmpMib mib);


    //-----------------------------------------------------------------
    //
    //  Protected methods
    //
    //-----------------------------------------------------------------

    /**
     * Allocates an ArrayList for storing table entries.
     *
     * This method is called within the constructor at object creation.
     * Any object implementing the {@link java.util.List} interface can
     * be used.
     *
     * <p>
     *  分配用于存储表条目的ArrayList。
     * 
     *  此方法在对象创建时在构造函数中调用。可以使用实现{@link java.util.List}接口的任何对象。
     * 
     * 
     * @return A new list in which to store entries. If <code>null</code>
     *         is returned then no entry will be stored in the list
     *         and getEntry() will always return null.
     **/
    protected List<Object> allocateTable() {
        return new ArrayList<Object>();
    }

    /**
     * Add an entry in this table.
     *
     * This method registers an entry in the table and perform
     * synchronization with the associated table metadata object.
     *
     * This method assumes that the given entry will not be registered,
     * or will be registered with its default ObjectName built from the
     * associated  SnmpIndex.
     * <p>
     * If the entry is going to be registered, then
     * {@link com.sun.jmx.snmp.agent.SnmpTableSupport#addEntry(SnmpIndex, ObjectName, Object)} should be preferred.
     * <br> This function is mainly provided for backward compatibility.
     *
     * <p>
     *  在此表中添加条目。
     * 
     *  此方法在表中注册一个条目,并与关联的表元数据对象执行同步。
     * 
     * 此方法假定给定条目不会被注册,或者将使用从关联的SnmpIndex构建的默认ObjectName注册。
     * <p>
     *  如果该条目要注册,则应优先使用{@link com.sun.jmx.snmp.agent.SnmpTableSupport#addEntry(SnmpIndex,ObjectName,Object)}
     * 。
     *  <br>此功能主要用于向后兼容。
     * 
     * 
     * @param index The SnmpIndex built from the given entry.
     * @param entry The entry that should be added in the table.
     *
     * @exception SnmpStatusException if the entry cannot be registered with
     *            the given index.
     **/
    protected void addEntry(SnmpIndex index, Object entry)
        throws SnmpStatusException {
        SnmpOid oid = buildOidFromIndex(index);
        ObjectName name = null;
        if (isRegistrationRequired()) {
            name = buildNameFromIndex(index);
        }
        meta.addEntry(oid,name,entry);
    }

    /**
     * Add an entry in this table.
     *
     * This method registers an entry in the table and performs
     * synchronization with the associated table metadata object.
     *
     * <p>
     *  在此表中添加条目。
     * 
     *  此方法在表中注册一个条目,并执行与关联的表元数据对象的同步。
     * 
     * 
     * @param index The SnmpIndex built from the given entry.
     * @param name  The ObjectName with which this entry will be registered.
     * @param entry The entry that should be added in the table.
     *
     * @exception SnmpStatusException if the entry cannot be registered with
     *            the given index.
     **/
    protected void addEntry(SnmpIndex index, ObjectName name, Object entry)
        throws SnmpStatusException {
        SnmpOid oid = buildOidFromIndex(index);
        meta.addEntry(oid,name,entry);
    }

    /**
     * Remove an entry from this table.
     *
     * This method unregisters an entry from the table and performs
     * synchronization with the associated table metadata object.
     *
     * <p>
     *  从此表中删除条目。
     * 
     *  此方法从表中注销一个条目,并执行与关联的表元数据对象的同步。
     * 
     * 
     * @param index The SnmpIndex identifying the entry.
     * @param entry The entry that should be removed in the table. This
     *              parameter is optional and can be omitted if it doesn't
     *              need to be passed along to the
     *              <code>removeEntryCb()</code> callback defined in the
     *              {@link com.sun.jmx.snmp.agent.SnmpTableCallbackHandler}
     *              interface.
     *
     * @exception SnmpStatusException if the entry cannot be unregistered.
     **/
    protected void removeEntry(SnmpIndex index, Object entry)
        throws SnmpStatusException {
        SnmpOid oid = buildOidFromIndex(index);
        meta.removeEntry(oid,entry);
    }

    // protected void removeEntry(ObjectName name, Object entry)
    //  throws SnmpStatusException {
    //  meta.removeEntry(name,entry);
    // }

    /**
     * Returns the entries in the table.
     *
     * <p>
     *  返回表中的条目。
     * 
     * 
     * @return An Object[] array containing the entries registered in the
     *         table.
     **/
    protected Object[] getBasicEntries() {
        if (entries == null) return null;
        Object[] array= new Object[entries.size()];
        entries.toArray(array);
        return array;
    }

    /**
     * Binds this table with its associated metadata, registering itself
     * as an SnmpTableEntryFactory.
     * <p>
     *  将此表与其关联的元数据绑定,将其注册为SnmpTableEntryFactory。
     * 
     **/
    protected void bindWithTableMeta() {
        if (meta == null) return;
        registrationRequired = meta.isRegistrationRequired();
        meta.registerEntryFactory(this);
    }

}
