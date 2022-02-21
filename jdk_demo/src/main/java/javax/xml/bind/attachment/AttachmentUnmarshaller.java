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

package javax.xml.bind.attachment;

import javax.activation.DataHandler;

/**
 * <p>Enables JAXB unmarshalling of a root document containing optimized binary data formats.</p>
 *
 * <p>This API enables an efficient cooperative processing of optimized
 * binary data formats between a JAXB 2.0 implementation and MIME-based package
 * processor (MTOM/XOP and WS-I AP 1.0). JAXB unmarshals the body of a package, delegating the
 * understanding of the packaging format being used to a MIME-based
 * package processor that implements this abstract class.</p>
 *
 * <p>This abstract class identifies if a package requires XOP processing, {@link #isXOPPackage()} and provides retrieval of binary content stored as attachments by content-id.</p>
 *
 * <h2>Identifying the content-id, cid, to pass to <code>getAttachment*(String cid)</code></h2>
 * <ul>
 * <li>
 * For XOP processing, the infoset representation of the cid is described
 * in step 2a in
 * <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#interpreting_xop_packages">Section 3.2 Interpreting XOP Packages</a>
 * </li>
 * <li>
 * For WS-I AP 1.0, the cid is identified as an element or attribute of
 * type <code>ref:swaRef </code> specified in
 * <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html#Referencing_Attachments_from_the_SOAP_Envelope">Section 4.4 Referencing Attachments from the SOAP Envelope</a>
 * </li>
 * </ul>
 *
 * <p>
 *  <p>启用JAXB解组包含优化的二进制数据格式的根文档。</p>
 * 
 *  <p>此API可以在JAXB 2.0实现和基于MIME的包处理器(MTOM / XOP和WS-I AP 1.0)之间高效协作处理优化的二进制数据格式。
 *  JAXB解封装包的主体,将对使用的封装格式的理解委托给实现此抽象类的基于MIME的包处理器。</p>。
 * 
 *  <p>此抽象类标识包是否需要XOP处理{@link #isXOPPackage()},并提供通过content-id存储为附件的二进制内容的检索。</p>
 * 
 *  <h2>标识要传递给<code> getAttachment *(String cid)</code> </h2>的content-id,cid
 * <ul>
 * <li>
 *  对于XOP处理,cid的信息集表示在<a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#interpreting_xop_packages">
 * 第3.2节解释XOP包中的步骤2a中进行了说明</a>。
 * </li>
 * <li>
 *  对于WS-I AP 1.0,cid被标识为<a href ="http://www.ws-i.org/Profiles/AttachmentsProfile中指定的<code> ref：swaRef 
 * </code>类型的元素或属性-1.0-2004-08-24.html#Referencing_Attachments_from_the_SOAP_Envelope">第4.4节从SOAP包络引用附件</a>
 * 。
 * </li>
 * </ul>
 * 
 * 
 * @author Marc Hadley
 * @author Kohsuke Kawaguchi
 * @author Joseph Fialli
 *
 * @since JAXB 2.0
 *
 * @see javax.xml.bind.Unmarshaller#setAttachmentUnmarshaller(AttachmentUnmarshaller)
 *
 * @see <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/">XML-binary Optimized Packaging</a>
 * @see <a href="http://www.ws-i.org/Profiles/AttachmentsProfile-1.0-2004-08-24.html">WS-I Attachments Profile Version 1.0.</a>
 * @see <a href="http://www.w3.org/TR/xml-media-types/">Describing Media Content of Binary Data in XML</a>
 */
public abstract class AttachmentUnmarshaller {
   /**
    * <p>Lookup MIME content by content-id, <code>cid</code>, and return as a {@link DataHandler}.</p>
    *
    * <p>The returned <code>DataHandler</code> instance must be configured
    * to meet the following required mapping constaint.
    * <table border="2" rules="all" cellpadding="4">
    *   <thead>
    *     <tr>
    *       <th align="center" colspan="2">
    *       Required Mappings between MIME and Java Types
    *       </tr>
    *     <tr>
    *       <th>MIME Type</th>
    *       <th>Java Type</th>
    *     </tr>
    *     <tr>
    *       <th><code>DataHandler.getContentType()</code></th>
    *       <th><code>instanceof DataHandler.getContent()</code></th>
    *     </tr>
    *   </thead>
    *   <tbody>
    *     <tr>
    *       <td>image/gif</td>
    *       <td>java.awt.Image</td>
    *     </tr>
    *     <tr>
    *       <td>image/jpeg</td>
    *       <td>java.awt.Image</td>
    *     </tr>
    *     <tr>
    *       <td>text/xml  or application/xml</td>
    *       <td>javax.xml.transform.Source</td>
    *     </tr>
    *   </tbody>
    *  </table>
    * Note that it is allowable to support additional mappings.</p>
    *
    * <p>
    *  <p>按content-id,<code> cid </code>查找MIME内容,并返回为{@link DataHandler}。</p>
    * 
    * <p>返回的<code> DataHandler </code>实例必须配置为满足以下必需的映射常量。
    * <table border="2" rules="all" cellpadding="4">
    * <thead>
    * <tr>
    * <th align="center" colspan="2">
    *  MIME和Java类型之间的必需映射
    * </tr>
    * <tr>
    *  <th> MIME类型</th> <th> Java类型</th>
    * </tr>
    * <tr>
    *  <th> <code> DataHandler.getContentType()</code> </th> <th> <code> instanceof DataHandler.getContent(
    * )</code>。
    * </tr>
    * </thead>
    * <tbody>
    * <tr>
    *  <td> image / gif </td> <td> java.awt.Image </td>
    * </tr>
    * 
    * @param cid It is expected to be a valid lexical form of the XML Schema
    * <code>xs:anyURI</code> datatype. If <code>{@link #isXOPPackage()}
    * ==true</code>, it must be a valid URI per the <code>cid:</code> URI scheme (see <a href="http://www.ietf.org/rfc/rfc2387.txt">RFC 2387</a>)
    *
    * @return
    *       a {@link DataHandler} that represents the MIME attachment.
    *
    * @throws IllegalArgumentException if the attachment for the given cid is not found.
    */
   public abstract DataHandler getAttachmentAsDataHandler(String cid);

    /**
     * <p>Retrieve the attachment identified by content-id, <code>cid</code>,  as a <tt>byte[]</tt></p>.
     *
     * <p>
     * <tr>
     *  <td> image / jpeg </td> <td> java.awt.Image </td>
     * </tr>
     * <tr>
     *  <td> text / xml或application / xml </td> <td> javax.xml.transform.Source </td>
     * </tr>
     * </tbody>
     * </table>
     *  请注意,允许支持其他映射。</p>
     * 
     * 
     * @param cid It is expected to be a valid lexical form of the XML Schema
     * <code>xs:anyURI</code> datatype. If <code>{@link #isXOPPackage()}
     * ==true</code>, it must be a valid URI per the <code>cid:</code> URI scheme (see <a href="http://www.ietf.org/rfc/rfc2387.txt">RFC 2387</a>)
     *
     * @return byte[] representation of attachment identified by cid.
     *
    * @throws IllegalArgumentException if the attachment for the given cid is not found.
     */
    public abstract byte[] getAttachmentAsByteArray(String cid);

    /**
     * <p>Read-only property that returns true if JAXB unmarshaller needs to perform XOP processing.</p>
     *
     * <p>This method returns <code>true</code> when the constraints specified
     * in  <a href="http://www.w3.org/TR/2005/REC-xop10-20050125/#identifying_xop_documents">Identifying XOP Documents</a> are met.
     * This value must not change during the unmarshalling process.</p>
     *
     * <p>
     * 
     * @return true when MIME context is a XOP Document.
     */
    public boolean isXOPPackage() { return false; }
}
