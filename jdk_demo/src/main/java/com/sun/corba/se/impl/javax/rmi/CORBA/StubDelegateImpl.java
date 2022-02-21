/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * <p>
 *  许可的材料 -  IBM RMI-IIOP v1.0的属性Copyright IBM Corp. 1998 1999保留所有权利
 * 
 */

package com.sun.corba.se.impl.javax.rmi.CORBA;

import java.io.IOException;

import java.rmi.RemoteException;

import javax.rmi.CORBA.Tie;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_INV_ORDER;

import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;

import com.sun.corba.se.spi.presentation.rmi.StubAdapter;

import com.sun.corba.se.spi.logging.CORBALogDomains ;

import com.sun.corba.se.impl.util.Utility;

import com.sun.corba.se.impl.ior.StubIORImpl ;
import com.sun.corba.se.impl.presentation.rmi.StubConnectImpl ;

import com.sun.corba.se.impl.logging.UtilSystemException ;

/**
 * Base class from which all static RMI-IIOP stubs must inherit.
 * <p>
 *  所有静态RMI-IIOP存根必须从其继承的基类。
 * 
 */
public class StubDelegateImpl implements javax.rmi.CORBA.StubDelegate
{
    static UtilSystemException wrapper = UtilSystemException.get(
        CORBALogDomains.RMIIIOP ) ;

    private StubIORImpl ior ;

    public StubIORImpl getIOR()
    {
        return ior ;
    }

    public StubDelegateImpl()
    {
        ior = null ;
    }

    /**
     * Sets the IOR components if not already set.
     * <p>
     *  如果尚未设置,则设置IOR组件。
     * 
     */
    private void init (javax.rmi.CORBA.Stub self)
    {
        // If the Stub is not connected to an ORB, BAD_OPERATION exception
        // will be raised by the code below.
        if (ior == null)
            ior = new StubIORImpl( self ) ;
    }

    /**
     * Returns a hash code value for the object which is the same for all stubs
     * that represent the same remote object.
     * <p>
     *  返回对于表示同一远程对象的所有存根都相同的对象的哈希代码值。
     * 
     * 
     * @return the hash code value.
     */
    public int hashCode(javax.rmi.CORBA.Stub self)
    {
        init(self);
        return ior.hashCode() ;
    }

    /**
     * Compares two stubs for equality. Returns <code>true</code> when used to compare stubs
     * that represent the same remote object, and <code>false</code> otherwise.
     * <p>
     *  比较两个存根以实现相等。当用于比较表示相同远程对象的存根时,返回<code> true </code>,否则返回<code> false </code>。
     * 
     * 
     * @param obj the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the <code>obj</code>
     *          argument; <code>false</code> otherwise.
     */
    public boolean equals(javax.rmi.CORBA.Stub self, java.lang.Object obj)
    {
        if (self == obj) {
            return true;
        }

        if (!(obj instanceof javax.rmi.CORBA.Stub)) {
            return false;
        }

        // no need to call init() because of calls to hashCode() below

        javax.rmi.CORBA.Stub other = (javax.rmi.CORBA.Stub) obj;
        if (other.hashCode() != self.hashCode()) {
            return false;
        }

        // hashCodes being the same does not mean equality. The stubs still
        // could be pointing to different IORs. So, do a literal comparison.
        // Apparently the ONLY way to do this (other than using private
        // reflection)  toString, because it is not possible to directly
        // access the StubDelegateImpl from the Stub.
        return self.toString().equals( other.toString() ) ;
    }

    public boolean equals( Object obj )
    {
        if (this == obj)
            return true ;

        if (!(obj instanceof StubDelegateImpl))
            return false ;

        StubDelegateImpl other = (StubDelegateImpl)obj ;

        if (ior == null)
            return ior == other.ior ;
        else
            return ior.equals( other.ior ) ;
    }

    public int hashCode() {
        if (ior == null) {
            return 0;
        } else {
            return ior.hashCode();
        }
    }

    /**
     * Returns a string representation of this stub. Returns the same string
     * for all stubs that represent the same remote object.
     * <p>
     *  返回此存根的字符串表示形式。为表示同一远程对象的所有存根返回相同的字符串。
     * 
     * 
     * @return a string representation of this stub.
     */
    public String toString(javax.rmi.CORBA.Stub self)
    {
        if (ior == null)
            return null ;
        else
            return ior.toString() ;
    }

    /**
     * Connects this stub to an ORB. Required after the stub is deserialized
     * but not after it is demarshalled by an ORB stream. If an unconnected
     * stub is passed to an ORB stream for marshalling, it is implicitly
     * connected to that ORB. Application code should not call this method
     * directly, but should call the portable wrapper method
     * {@link javax.rmi.PortableRemoteObject#connect}.
     * <p>
     *  将此存根连接到ORB。在存根被反序列化之后但不是在被ORB流分解之后,是必需的。如果未连接的存根传递到ORB流进行编组,则它将隐式连接到该ORB。
     * 应用程序代码不应该直接调用此方法,而应调用便携式换行器方法{@link javax.rmi.PortableRemoteObject#connect}。
     * 
     * 
     * @param orb the ORB to connect to.
     * @exception RemoteException if the stub is already connected to a different
     * ORB, or if the stub does not represent an exported remote or local object.
     */
    public void connect(javax.rmi.CORBA.Stub self, ORB orb)
        throws RemoteException
    {
        ior = StubConnectImpl.connect( ior, self, self, orb ) ;
    }

    /**
     * Serialization method to restore the IOR state.
     * <p>
     *  序列化方法来恢复IOR状态。
     * 
     */
    public void readObject(javax.rmi.CORBA.Stub self,
        java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException
    {
        if (ior == null)
            ior = new StubIORImpl() ;

        ior.doRead( stream ) ;
    }

    /**
     * Serialization method to save the IOR state.
     * <p>
     *  序列化方法来保存IOR状态。
     * 
     * @serialData The length of the IOR type ID (int), followed by the IOR type ID
     * (byte array encoded using ISO8859-1), followed by the number of IOR profiles
     * (int), followed by the IOR profiles.  Each IOR profile is written as a
     * profile tag (int), followed by the length of the profile data (int), followed
     * by the profile data (byte array).
     */
    public void writeObject(javax.rmi.CORBA.Stub self,
        java.io.ObjectOutputStream stream) throws IOException
    {
        init(self);
        ior.doWrite( stream ) ;
    }
}
