/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.source.tree;

import java.util.List;
import javax.tools.JavaFileObject;

/**
 * Represents the abstract syntax tree for compilation units (source
 * files) and package declarations (package-info.java).
 *
 * @jls sections 7.3, and 7.4
 *
 * <p>
 *  表示编译单元(源文件)和包声明(package-info.java)的抽象语法树。
 * 
 *  @jls section 7.3和7.4
 * 
 * 
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
@jdk.Exported
public interface CompilationUnitTree extends Tree {
    List<? extends AnnotationTree> getPackageAnnotations();
    ExpressionTree getPackageName();
    List<? extends ImportTree> getImports();
    List<? extends Tree> getTypeDecls();
    JavaFileObject getSourceFile();

    /**
     * Gets the line map for this compilation unit, if available.
     * Returns null if the line map is not available.
     * <p>
     * 
     * @return the line map for this compilation unit
     */
    LineMap getLineMap();
}
