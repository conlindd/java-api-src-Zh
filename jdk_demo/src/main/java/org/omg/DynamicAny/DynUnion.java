/***** Lobxxx Translate Finished ******/
package org.omg.DynamicAny;


/**
* org/omg/DynamicAny/DynUnion.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u45/3627/corba/src/share/classes/org/omg/DynamicAny/DynamicAny.idl
* Thursday, April 30, 2015 12:42:08 PM PDT
*/


/**
    * DynUnion objects support the manipulation of IDL unions.
    * A union can have only two valid current positions:
    * <UL>
    * <LI>zero, which denotes the discriminator
    * <LI>one, which denotes the active member
    * </UL>
    * The component_count value for a union depends on the current discriminator:
    * it is 2 for a union whose discriminator indicates a named member, and 1 otherwise.
    * <p>
    *  DynUnion对象支持操作IDL联合。联合只能有两个有效的当前位置：
    * <UL>
    *  <LI>零,其表示鉴别符<LI>一,其表示活动成员
    */
public interface DynUnion extends DynUnionOperations, org.omg.DynamicAny.DynAny, org.omg.CORBA.portable.IDLEntity 
{
} // interface DynUnion
