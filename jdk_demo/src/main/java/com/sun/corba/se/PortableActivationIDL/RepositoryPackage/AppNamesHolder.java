/***** Lobxxx Translate Finished ******/
package com.sun.corba.se.PortableActivationIDL.RepositoryPackage;


/**
* com/sun/corba/se/PortableActivationIDL/RepositoryPackage/AppNamesHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from c:/re/workspace/8-2-build-windows-amd64-cygwin/jdk8u45/3627/corba/src/share/classes/com/sun/corba/se/PortableActivationIDL/activation.idl
* Thursday, April 30, 2015 12:42:08 PM PDT
*/


/** Type used for a list of application names
/* <p>
/* com / sun / corba / se / PortableActivationIDL / RepositoryPackage / AppNamesHolder.java。
/* 
	*/
public final class AppNamesHolder implements org.omg.CORBA.portable.Streamable
{
  public String value[] = null;

  public AppNamesHolder ()
  {
  }

  public AppNamesHolder (String[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.sun.corba.se.PortableActivationIDL.RepositoryPackage.AppNamesHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.sun.corba.se.PortableActivationIDL.RepositoryPackage.AppNamesHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.sun.corba.se.PortableActivationIDL.RepositoryPackage.AppNamesHelper.type ();
  }

}
