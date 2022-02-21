/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.naming.internal;

import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.NamingEnumeration;

/**
 * VersionHelper was used by JNDI to accommodate differences between
 * JDK 1.1.x and the Java 2 platform. As this is no longer necessary
 * since JNDI's inclusion in the platform, this class currently
 * serves as a set of utilities for performing system-level things,
 * such as class-loading and reading system properties.
 *
 * <p>
 *  JNDI使用VersionHelper来适应JDK 1.1.x和Java 2平台之间的差异。
 * 由于JNDI包含在平台中,因此不再需要这个类,因此该类目前用作一组用于执行系统级事务的实用程序,例如类加载和读取系统属性。
 * 
 * 
 * @author Rosanna Lee
 * @author Scott Seligman
 */

public abstract class VersionHelper {
    private static VersionHelper helper = null;

    final static String[] PROPS = new String[] {
        javax.naming.Context.INITIAL_CONTEXT_FACTORY,
        javax.naming.Context.OBJECT_FACTORIES,
        javax.naming.Context.URL_PKG_PREFIXES,
        javax.naming.Context.STATE_FACTORIES,
        javax.naming.Context.PROVIDER_URL,
        javax.naming.Context.DNS_URL,
        // The following shouldn't create a runtime dependence on ldap package.
        javax.naming.ldap.LdapContext.CONTROL_FACTORIES
    };

    public final static int INITIAL_CONTEXT_FACTORY = 0;
    public final static int OBJECT_FACTORIES = 1;
    public final static int URL_PKG_PREFIXES = 2;
    public final static int STATE_FACTORIES = 3;
    public final static int PROVIDER_URL = 4;
    public final static int DNS_URL = 5;
    public final static int CONTROL_FACTORIES = 6;

    VersionHelper() {} // Disallow anyone from creating one of these.

    static {
        helper = new VersionHelper12();
    }

    public static VersionHelper getVersionHelper() {
        return helper;
    }

    public abstract Class<?> loadClass(String className)
        throws ClassNotFoundException;

    abstract Class<?> loadClass(String className, ClassLoader cl)
        throws ClassNotFoundException;

    public abstract Class<?> loadClass(String className, String codebase)
        throws ClassNotFoundException, MalformedURLException;

    /*
     * Returns a JNDI property from the system properties.  Returns
     * null if the property is not set, or if there is no permission
     * to read it.
     * <p>
     *  从系统属性返回JNDI属性。如果未设置属性,或没有读取权限,则返回null。
     * 
     */
    abstract String getJndiProperty(int i);

    /*
     * Reads each property in PROPS from the system properties, and
     * returns their values -- in order -- in an array.  For each
     * unset property, the corresponding array element is set to null.
     * Returns null if there is no permission to call System.getProperties().
     * <p>
     *  从系统属性读取PROPS中的每个属性,并在数组中按顺序返回它们的值。对于每个unset属性,相应的数组元素设置为null。
     * 如果没有调用System.getProperties()的权限,则返回null。
     * 
     */
    abstract String[] getJndiProperties();

    /*
     * Returns the resource of a given name associated with a particular
     * class (never null), or null if none can be found.
     * <p>
     *  返回与特定类(从不为null)关联的给定名称的资源,如果找不到,则返回null。
     * 
     */
    abstract InputStream getResourceAsStream(Class<?> c, String name);

    /*
     * Returns an input stream for a file in <java.home>/lib,
     * or null if it cannot be located or opened.
     *
     * <p>
     *  返回<java.home> / lib中的文件的输入流,如果无法找到或打开,则返回null。
     * 
     * 
     * @param filename  The file name, sans directory.
     */
    abstract InputStream getJavaHomeLibStream(String filename);

    /*
     * Returns an enumeration (never null) of InputStreams of the
     * resources of a given name associated with a particular class
     * loader.  Null represents the bootstrap class loader in some
     * Java implementations.
     * <p>
     *  返回与特定类加载器相关联的给定名称的资源的InputStreams的枚举(从不为null)。 Null代表一些Java实现中的引导类加载器。
     * 
     */
    abstract NamingEnumeration<InputStream> getResources(
            ClassLoader cl, String name)
        throws IOException;

    /*
     * Returns the context class loader associated with the current thread.
     * Null indicates the bootstrap class loader in some Java implementations.
     *
     * <p>
     *  返回与当前线程相关联的上下文类加载器。 Null表示一些Java实现中的引导类加载器。
     * 
     * @throws SecurityException if the class loader is not accessible.
     */
    abstract ClassLoader getContextClassLoader();

    static protected URL[] getUrlArray(String codebase)
        throws MalformedURLException {
        // Parse codebase into separate URLs
        StringTokenizer parser = new StringTokenizer(codebase);
        Vector<String> vec = new Vector<>(10);
        while (parser.hasMoreTokens()) {
            vec.addElement(parser.nextToken());
        }
        String[] url = new String[vec.size()];
        for (int i = 0; i < url.length; i++) {
            url[i] = vec.elementAt(i);
        }

        URL[] urlArray = new URL[url.length];
        for (int i = 0; i < urlArray.length; i++) {
            urlArray[i] = new URL(url[i]);
        }
        return urlArray;
    }
}
