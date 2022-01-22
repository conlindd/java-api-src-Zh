/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2002, 2013, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jmx.mbeanserver;


import static com.sun.jmx.defaults.JmxProperties.MBEANSERVER_LOGGER;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.management.MBeanPermission;

import javax.management.ObjectName;
import javax.management.loading.PrivateClassLoader;
import sun.reflect.misc.ReflectUtil;

/**
 * This class keeps the list of Class Loaders registered in the MBean Server.
 * It provides the necessary methods to load classes using the
 * registered Class Loaders.
 *
 * <p>
 *  此类保留在MBean Server中注册的类加载器的列表。它提供了使用注册的类装载器加载类的必要方法。
 * 
 * 
 * @since 1.5
 */
final class ClassLoaderRepositorySupport
    implements ModifiableClassLoaderRepository {

    /* We associate an optional ObjectName with each entry so that
       we can remove the correct entry when unregistering an MBean
       that is a ClassLoader.  The same object could be registered
       under two different names (even though this is not recommended)
       so if we did not do this we could disturb the defined
    /* <p>
    /*  我们可以在注销作为ClassLoader的MBean时删除正确的条目。同一个对象可以注册两个不同的名称(即使这不是推荐),所以如果我们没有这样做,我们可以打扰定义
    /* 
    /* 
       semantics for the order of ClassLoaders in the repository.  */
    private static class LoaderEntry {
        ObjectName name; // can be null
        ClassLoader loader;

        LoaderEntry(ObjectName name,  ClassLoader loader) {
            this.name = name;
            this.loader = loader;
        }
    }

    private static final LoaderEntry[] EMPTY_LOADER_ARRAY = new LoaderEntry[0];

    /**
     * List of class loaders
     * Only read-only actions should be performed on this object.
     *
     * We do O(n) operations on this array, e.g. when removing
     * a ClassLoader.  The assumption is that the number of elements
     * is small, probably less than ten, and that the vast majority
     * of operations are searches (loadClass) which are by definition
     * linear.
     * <p>
     *  类装载器列表只应对此对象执行只读操作。
     * 
     *  我们对这个数组进行O(n)运算,例如。当删除ClassLoader时。假设元素的数量很小,可能小于10,并且绝大多数操作是搜索(loadClass),根据定义它们是线性的。
     * 
     */
    private LoaderEntry[] loaders = EMPTY_LOADER_ARRAY;

    /**
     * Same behavior as add(Object o) in {@link java.util.List}.
     * Replace the loader list with a new one in which the new
     * loader has been added.
     * <p>
     *  与{@link java.util.List}中的add(Object o)行为相同。将一个新的加载器列表添加到新加载器中。
     * 
     * 
     **/
    private synchronized boolean add(ObjectName name, ClassLoader cl) {
        List<LoaderEntry> l =
            new ArrayList<LoaderEntry>(Arrays.asList(loaders));
        l.add(new LoaderEntry(name, cl));
        loaders = l.toArray(EMPTY_LOADER_ARRAY);
        return true;
    }

    /**
     * Same behavior as remove(Object o) in {@link java.util.List}.
     * Replace the loader list with a new one in which the old loader
     * has been removed.
     *
     * The ObjectName may be null, in which case the entry to
     * be removed must also have a null ObjectName and the ClassLoader
     * values must match.  If the ObjectName is not null, then
     * the first entry with a matching ObjectName is removed,
     * regardless of whether ClassLoader values match.  (In fact,
     * the ClassLoader parameter will usually be null in this case.)
     * <p>
     *  与{@link java.util.List}中的remove(Object o)行为相同。使用新的装载器已被删除的新装载器列表替换。
     * 
     * ObjectName可以为null,在这种情况下,要删除的条目也必须具有空ObjectName,并且ClassLoader值必须匹配。
     * 如果ObjectName不为null,那么将删除具有匹配ObjectName的第一个条目,而不管ClassLoader值是否匹配。 (实际上,在这种情况下,ClassLoader参数通常为null。
     * )。
     * 
     * 
     **/
    private synchronized boolean remove(ObjectName name, ClassLoader cl) {
        final int size = loaders.length;
        for (int i = 0; i < size; i++) {
            LoaderEntry entry = loaders[i];
            boolean match =
                (name == null) ?
                cl == entry.loader :
                name.equals(entry.name);
            if (match) {
                LoaderEntry[] newloaders = new LoaderEntry[size - 1];
                System.arraycopy(loaders, 0, newloaders, 0, i);
                System.arraycopy(loaders, i + 1, newloaders, i,
                                 size - 1 - i);
                loaders = newloaders;
                return true;
            }
        }
        return false;
    }


    /**
     * List of valid search
     * <p>
     *  有效搜索列表
     * 
     */
    private final Map<String,List<ClassLoader>> search =
        new Hashtable<String,List<ClassLoader>>(10);

    /**
     * List of named class loaders.
     * <p>
     *  命名类加载器列表。
     * 
     */
    private final Map<ObjectName,ClassLoader> loadersWithNames =
        new Hashtable<ObjectName,ClassLoader>(10);

    // from javax.management.loading.DefaultLoaderRepository
    public final Class<?> loadClass(String className)
        throws ClassNotFoundException {
        return  loadClass(loaders, className, null, null);
    }


    // from javax.management.loading.DefaultLoaderRepository
    public final Class<?> loadClassWithout(ClassLoader without, String className)
            throws ClassNotFoundException {
        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER,
                    ClassLoaderRepositorySupport.class.getName(),
                    "loadClassWithout", className + " without " + without);
        }

        // without is null => just behave as loadClass
        //
        if (without == null)
            return loadClass(loaders, className, null, null);

        // We must try to load the class without the given loader.
        //
        startValidSearch(without, className);
        try {
            return loadClass(loaders, className, without, null);
        } finally {
            stopValidSearch(without, className);
        }
    }


    public final Class<?> loadClassBefore(ClassLoader stop, String className)
            throws ClassNotFoundException {
        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER,
                    ClassLoaderRepositorySupport.class.getName(),
                    "loadClassBefore", className + " before " + stop);
        }

        if (stop == null)
            return loadClass(loaders, className, null, null);

        startValidSearch(stop, className);
        try {
            return loadClass(loaders, className, null, stop);
        } finally {
            stopValidSearch(stop, className);
        }
    }


    private Class<?> loadClass(final LoaderEntry list[],
                               final String className,
                               final ClassLoader without,
                               final ClassLoader stop)
            throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(className);
        final int size = list.length;
        for(int i=0; i<size; i++) {
            try {
                final ClassLoader cl = list[i].loader;
                if (cl == null) // bootstrap class loader
                    return Class.forName(className, false, null);
                if (cl == without)
                    continue;
                if (cl == stop)
                    break;
                if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                    MBEANSERVER_LOGGER.logp(Level.FINER,
                            ClassLoaderRepositorySupport.class.getName(),
                            "loadClass", "Trying loader = " + cl);
                }
                /* We used to have a special case for "instanceof
                   MLet" here, where we invoked the method
                   loadClass(className, null) to prevent infinite
                   recursion.  But the rule whereby the MLet only
                   consults loaders that precede it in the CLR (via
                   loadClassBefore) means that the recursion can't
                   happen, and the test here caused some legitimate
                   classloading to fail.  For example, if you have
                   dependencies C->D->E with loaders {E D C} in the
                   CLR in that order, you would expect to be able to
                   load C.  The problem is that while resolving D, CLR
                /* <p>
                /*  MLet"在这里,我们调用了loadClass(className,null)方法来防止无限递归,但是MLet只在CLR中查询它之前的加载器的规则(通过loadClassBefore)意味着递归不会发
                /* 生,测试在这里造成一些合法的类加载失败例如,如果你有依赖关系C-> D-> E加载器{EDC}在CLR中的顺序,你会希望能够加载C.问题是,解析D,CLR。
                /* 
                   delegation is disabled, so it can't find E.  */
                return Class.forName(className, false, cl);
            } catch (ClassNotFoundException e) {
                // OK: continue with next class
            }
        }

        throw new ClassNotFoundException(className);
    }

    private synchronized void startValidSearch(ClassLoader aloader,
                                               String className)
        throws ClassNotFoundException {
        // Check if we have such a current search
        //
        List<ClassLoader> excluded = search.get(className);
        if ((excluded!= null) && (excluded.contains(aloader))) {
            if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                MBEANSERVER_LOGGER.logp(Level.FINER,
                        ClassLoaderRepositorySupport.class.getName(),
                        "startValidSearch", "Already requested loader = " +
                        aloader + " class = " + className);
            }
            throw new ClassNotFoundException(className);
        }

        // Add an entry
        //
        if (excluded == null) {
            excluded = new ArrayList<ClassLoader>(1);
            search.put(className, excluded);
        }
        excluded.add(aloader);
        if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            MBEANSERVER_LOGGER.logp(Level.FINER,
                    ClassLoaderRepositorySupport.class.getName(),
                    "startValidSearch",
                    "loader = " + aloader + " class = " + className);
        }
    }

    private synchronized void stopValidSearch(ClassLoader aloader,
                                              String className) {

        // Retrieve the search.
        //
        List<ClassLoader> excluded = search.get(className);
        if (excluded != null) {
            excluded.remove(aloader);
            if (MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                MBEANSERVER_LOGGER.logp(Level.FINER,
                        ClassLoaderRepositorySupport.class.getName(),
                        "stopValidSearch",
                        "loader = " + aloader + " class = " + className);
            }
        }
    }

    public final void addClassLoader(ClassLoader loader) {
        add(null, loader);
    }

    public final void removeClassLoader(ClassLoader loader) {
        remove(null, loader);
    }

    public final synchronized void addClassLoader(ObjectName name,
                                                  ClassLoader loader) {
        loadersWithNames.put(name, loader);
        if (!(loader instanceof PrivateClassLoader))
            add(name, loader);
    }

    public final synchronized void removeClassLoader(ObjectName name) {
        ClassLoader loader = loadersWithNames.remove(name);
        if (!(loader instanceof PrivateClassLoader))
            remove(name, loader);
    }

    public final ClassLoader getClassLoader(ObjectName name) {
        ClassLoader instance = loadersWithNames.get(name);
        if (instance != null) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                Permission perm =
                        new MBeanPermission(instance.getClass().getName(),
                        null,
                        name,
                        "getClassLoader");
                sm.checkPermission(perm);
            }
        }
        return instance;
    }

}
