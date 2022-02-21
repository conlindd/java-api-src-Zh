/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2007, 2015, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 *  版权所有1999-2002,2004 Apache软件基金会。
 * 
 *  根据Apache许可证2.0版("许可证")授权;您不能使用此文件,除非符合许可证。您可以通过获取许可证的副本
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  除非适用法律要求或书面同意,否则根据许可证分发的软件按"原样"分发,不附带任何明示或暗示的担保或条件。请参阅管理许可证下的权限和限制的特定语言的许可证。
 * 
 */


/*
 * WARNING: because java doesn't support multi-inheritance some code is
 * duplicated. If you're changing this file you probably want to change
 * DeferredElementImpl.java at the same time.
 *
 * <p>
 *  警告：因为java不支持多继承,一些代码是重复的。如果你要更改这个文件,你可能想要同时更改DeferredElementImpl.java。
 * 
 */

package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import org.w3c.dom.NamedNodeMap;


/**
 * DeferredElementNSImpl is to ElementNSImpl, what DeferredElementImpl is to
 * ElementImpl.
 *
 * @xerces.internal
 *
 * <p>
 *  DeferredElementNSImpl是ElementNSImpl,DeferredElementImpl是什么到ElementImpl。
 * 
 *  @ xerces.internal
 * 
 * 
 * @see DeferredElementImpl
 */
public class DeferredElementNSImpl
    extends ElementNSImpl
    implements DeferredNode {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -5001885145370927385L;

    //
    // Data
    //

    /** Node index. */
    protected transient int fNodeIndex;

    //
    // Constructors
    //

    /**
     * This is the deferred constructor. Only the fNodeIndex is given here. All
     * other data, can be requested from the ownerDocument via the index.
     * <p>
     *  这是延迟构造函数。这里只给出fNodeIndex。所有其他数据,可以通过索引从ownerDocument请求。
     * 
     */
    DeferredElementNSImpl(DeferredDocumentImpl ownerDoc, int nodeIndex) {
        super(ownerDoc, null);

        fNodeIndex = nodeIndex;
        needsSyncChildren(true);

    } // <init>(DocumentImpl,int)

    //
    // DeferredNode methods
    //

    /** Returns the node index. */
    public final int getNodeIndex() {
        return fNodeIndex;
    }

    //
    // Protected methods
    //

    /** Synchronizes the data (name and value) for fast nodes. */
    protected final void synchronizeData() {

        // no need to sync in the future
        needsSyncData(false);

        // fluff data
        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) this.ownerDocument;

        // we don't want to generate any event for this so turn them off
        boolean orig = ownerDocument.mutationEvents;
        ownerDocument.mutationEvents = false;

        name = ownerDocument.getNodeName(fNodeIndex);

        // extract local part from QName
        int index = name.indexOf(':');
        if (index < 0) {
            localName = name;
        }
        else {
            localName = name.substring(index + 1);
        }

            namespaceURI = ownerDocument.getNodeURI(fNodeIndex);
        type = (XSTypeDefinition)ownerDocument.getTypeInfo(fNodeIndex);

        // attributes
        setupDefaultAttributes();
        int attrIndex = ownerDocument.getNodeExtra(fNodeIndex);
        if (attrIndex != -1) {
            NamedNodeMap attrs = getAttributes();
            boolean seenSchemaDefault = false;
            do {
                AttrImpl attr = (AttrImpl) ownerDocument.getNodeObject(attrIndex);
                // Take special care of schema defaulted attributes. Calling the
                // non-namespace aware setAttributeNode() method could overwrite
                // another attribute with the same local name.
                if (!attr.getSpecified() && (seenSchemaDefault ||
                    (attr.getNamespaceURI() != null &&
                    attr.getNamespaceURI() != NamespaceContext.XMLNS_URI &&
                    attr.getName().indexOf(':') < 0))) {
                    seenSchemaDefault = true;
                    attrs.setNamedItemNS(attr);
                }
                else {
                    attrs.setNamedItem(attr);
                }
                attrIndex = ownerDocument.getPrevSibling(attrIndex);
            } while (attrIndex != -1);
        }

        // set mutation events flag back to its original value
        ownerDocument.mutationEvents = orig;

    } // synchronizeData()

    /**
     * Synchronizes the node's children with the internal structure.
     * Fluffing the children at once solves a lot of work to keep
     * the two structures in sync. The problem gets worse when
     * editing the tree -- this makes it a lot easier.
     * <p>
     *  将节点的子代与内部结构同步。立即起毛儿童解决了大量的工作,保持两个结构同步。编辑树时问题变得更糟 - 这使得它更容易。
     */
    protected final void synchronizeChildren() {
        DeferredDocumentImpl ownerDocument =
            (DeferredDocumentImpl) ownerDocument();
        ownerDocument.synchronizeChildren(this, fNodeIndex);
    } // synchronizeChildren()

} // class DeferredElementImpl
