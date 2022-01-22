/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1997, 1999, Oracle and/or its affiliates. All rights reserved.
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
 * File: ./org/omg/CORBA/IDLType.java
 * From: ./ir.idl
 * Date: Fri Aug 28 16:03:31 1998
 *   By: idltojava Java IDL 1.2 Aug 11 1998 02:00:18
 * <p>
 *  文件：./org/omg/CORBA/IDLType.java从：./ir.idl日期：Fri Aug 28 16:03:31 1998发布者：idltojava Java IDL 1.2 Aug 1
 * 1 1998 02:00:18。
 * 
 */

package org.omg.CORBA;
/**
* tempout/org/omg/CORBA/IDLType.java
* Generated by the IBM IDL-to-Java compiler, version 1.0
* from ../../Lib/ir.idl
* Thursday, February 25, 1999 2:11:23 o'clock PM PST
* <p>
*  tempout / org / omg / CORBA / IDLType.java由IBM IDL到Java编译器生成的版本1.0从../../Lib/ir.idl 1999年2月25日星期四2:1
* 1:23 PM PST。
* 
*/

/**
  * An abstract interface inherited by all Interface Repository
  * (IR) objects that represent OMG IDL types. It provides access
  * to the <code>TypeCode</code> object describing the type and is used in defining the
  * other interfaces wherever definitions of <code>IDLType</code> must be referenced.
  * <p>
  *  由表示OMG IDL类型的所有Interface Repository(IR)对象继承的抽象接口。
  * 它提供对描述类型的<code> TypeCode </code>对象的访问,并且用于定义必须引用<code> IDLType </code>的定义的其他接口。
  */

public interface IDLType extends IDLTypeOperations, org.omg.CORBA.IRObject, org.omg.CORBA.portable.IDLEntity
{
} // interface IDLType
