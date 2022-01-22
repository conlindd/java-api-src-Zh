/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2007, 2015, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 *  版权所有1999-2004 Apache软件基金会。
 * 
 *  根据Apache许可证2.0版("许可证")授权;您不能使用此文件,除非符合许可证。您可以通过获取许可证的副本
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  除非适用法律要求或书面同意,否则根据许可证分发的软件按"原样"分发,不附带任何明示或暗示的担保或条件。请参阅管理许可证下的权限和限制的特定语言的许可证。
 * 
 */
/*
 * $Id: StylesheetPIHandler.java,v 1.2.4.1 2005/09/15 08:15:57 suresh_emailid Exp $
 * <p>
 *  $ Id：StylesheetPIHandler.java,v 1.2.4.1 2005/09/15 08:15:57 suresh_emailid Exp $
 * 
 */
package com.sun.org.apache.xml.internal.utils;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import com.sun.org.apache.xml.internal.utils.SystemIDResolver;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Search for the xml-stylesheet processing instructions in an XML document.
 * <p>
 *  在XML文档中搜索xml样式表处理指令。
 * 
 * 
 * @see <a href="http://www.w3.org/TR/xml-stylesheet/">Associating Style Sheets with XML documents, Version 1.0</a>
 */
public class StylesheetPIHandler extends DefaultHandler
{
  /** The baseID of the document being processed.  */
  String m_baseID;

  /** The desired media criteria. */
  String m_media;

  /** The desired title criteria.  */
  String m_title;

  /** The desired character set criteria.   */
  String m_charset;

  /** A list of SAXSource objects that match the criteria.  */
  Vector m_stylesheets = new Vector();

  // Add code to use a URIResolver. Patch from Dmitri Ilyin.

  /**
   * The object that implements the URIResolver interface,
   * or null.
   * <p>
   *  实现URIResolver接口的对象,或null。
   * 
   */
  URIResolver m_uriResolver;

  /**
   * Get the object that will be used to resolve URIs in href
   * in xml-stylesheet processing instruction.
   *
   * <p>
   *  获取将用于解析xml样式表处理指令中的href中的URI的对象。
   * 
   * 
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }

  /**
   * Get the object that will be used to resolve URIs in href
   * in xml-stylesheet processing instruction.
   *
   * <p>
   *  获取将用于解析xml样式表处理指令中的href中的URI的对象。
   * 
   * 
   * @return The URIResolver that was set with setURIResolver.
   */
  public URIResolver getURIResolver()
  {
    return m_uriResolver;
  }

  /**
   * Construct a StylesheetPIHandler instance that will search
   * for xml-stylesheet PIs based on the given criteria.
   *
   * <p>
   *  构造一个StylesheetPIHandler实例,该实例将根据给定的条件搜索xml样式表PI。
   * 
   * 
   * @param baseID The base ID of the XML document, needed to resolve
   *               relative IDs.
   * @param media The desired media criteria.
   * @param title The desired title criteria.
   * @param charset The desired character set criteria.
   */
  public StylesheetPIHandler(String baseID, String media, String title,
                             String charset)
  {

    m_baseID = baseID;
    m_media = media;
    m_title = title;
    m_charset = charset;
  }

  /**
   * Return the last stylesheet found that match the constraints.
   *
   * <p>
   *  返回找到的匹配约束的最后一个样式表。
   * 
   * 
   * @return Source object that references the last stylesheet reference
   *         that matches the constraints.
   */
  public Source getAssociatedStylesheet()
  {

    int sz = m_stylesheets.size();

    if (sz > 0)
    {
      Source source = (Source) m_stylesheets.elementAt(sz-1);
      return source;
    }
    else
      return null;
  }

  /**
   * Handle the xml-stylesheet processing instruction.
   *
   * <p>
   *  处理xml样式表处理指令。
   * 
   * 
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *             none is supplied.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   * @see <a href="http://www.w3.org/TR/xml-stylesheet/">Associating Style Sheets with XML documents, Version 1.0</a>
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {

    if (target.equals("xml-stylesheet"))
    {
      String href = null;  // CDATA #REQUIRED
      String type = null;  // CDATA #REQUIRED
      String title = null;  // CDATA #IMPLIED
      String media = null;  // CDATA #IMPLIED
      String charset = null;  // CDATA #IMPLIED
      boolean alternate = false;  // (yes|no) "no"
      StringTokenizer tokenizer = new StringTokenizer(data, " \t=\n", true);
      boolean lookedAhead = false;
      Source source = null;

      String token = "";
      while (tokenizer.hasMoreTokens())
      {
        if (!lookedAhead)
          token = tokenizer.nextToken();
        else
          lookedAhead = false;
        if (tokenizer.hasMoreTokens() &&
               (token.equals(" ") || token.equals("\t") || token.equals("=")))
          continue;

        String name = token;
        if (name.equals("type"))
        {
          token = tokenizer.nextToken();
          while (tokenizer.hasMoreTokens() &&
               (token.equals(" " ) || token.equals("\t") || token.equals("=")))
            token = tokenizer.nextToken();
          type = token.substring(1, token.length() - 1);

        }
        else if (name.equals("href"))
        {
          token = tokenizer.nextToken();
          while (tokenizer.hasMoreTokens() &&
               (token.equals(" " ) || token.equals("\t") || token.equals("=")))
            token = tokenizer.nextToken();
          href = token;
          if (tokenizer.hasMoreTokens())
          {
            token = tokenizer.nextToken();
            // If the href value has parameters to be passed to a
            // servlet(something like "foobar?id=12..."),
            // we want to make sure we get them added to
            // the href value. Without this check, we would move on
            // to try to process another attribute and that would be
            // wrong.
            // We need to set lookedAhead here to flag that we
            // already have the next token.
            while ( token.equals("=") && tokenizer.hasMoreTokens())
            {
              href = href + token + tokenizer.nextToken();
              if (tokenizer.hasMoreTokens())
              {
                token = tokenizer.nextToken();
                lookedAhead = true;
              }
              else
              {
                break;
              }
            }
          }
          href = href.substring(1, href.length() - 1);
          try
          {
            // Add code to use a URIResolver. Patch from Dmitri Ilyin.
            if (m_uriResolver != null)
            {
              source = m_uriResolver.resolve(href, m_baseID);
            }
           else
            {
              href = SystemIDResolver.getAbsoluteURI(href, m_baseID);
              source = new SAXSource(new InputSource(href));
            }
          }
          catch(TransformerException te)
          {
            throw new org.xml.sax.SAXException(te);
          }
        }
        else if (name.equals("title"))
        {
          token = tokenizer.nextToken();
          while (tokenizer.hasMoreTokens() &&
               (token.equals(" " ) || token.equals("\t") || token.equals("=")))
            token = tokenizer.nextToken();
          title = token.substring(1, token.length() - 1);
        }
        else if (name.equals("media"))
        {
          token = tokenizer.nextToken();
          while (tokenizer.hasMoreTokens() &&
               (token.equals(" " ) || token.equals("\t") || token.equals("=")))
            token = tokenizer.nextToken();
          media = token.substring(1, token.length() - 1);
        }
        else if (name.equals("charset"))
        {
          token = tokenizer.nextToken();
          while (tokenizer.hasMoreTokens() &&
              (token.equals(" " ) || token.equals("\t") || token.equals("=")))
            token = tokenizer.nextToken();
          charset = token.substring(1, token.length() - 1);
        }
        else if (name.equals("alternate"))
        {
          token = tokenizer.nextToken();
          while (tokenizer.hasMoreTokens() &&
               (token.equals(" " ) || token.equals("\t") || token.equals("=")))
            token = tokenizer.nextToken();
          alternate = token.substring(1, token.length()
                                             - 1).equals("yes");
        }

      }

      if ((null != type)
          && (type.equals("text/xsl") || type.equals("text/xml") || type.equals("application/xml+xslt"))
          && (null != href))
      {
        if (null != m_media)
        {
          if (null != media)
          {
            if (!media.equals(m_media))
              return;
          }
          else
            return;
        }

        if (null != m_charset)
        {
          if (null != charset)
          {
            if (!charset.equals(m_charset))
              return;
          }
          else
            return;
        }

        if (null != m_title)
        {
          if (null != title)
          {
            if (!title.equals(m_title))
              return;
          }
          else
            return;
        }

        m_stylesheets.addElement(source);
      }
    }
  }


  /**
   * The spec notes that "The xml-stylesheet processing instruction is allowed only in the prolog of an XML document.",
   * so, at least for right now, I'm going to go ahead an throw a TransformerException
   * in order to stop the parse.
   *
   * <p>
   * 规范指出"xml样式表处理指令只允许在XML文档的序言中",所以,至少现在,我要继续抛出TransformerException以停止解析。
   * 
   * 
   * @param namespaceURI The Namespace URI, or an empty string.
   * @param localName The local name (without prefix), or empty string if not namespace processing.
   * @param qName The qualified name (with prefix).
   * @param atts  The specified or defaulted attributes.
   *
   * @throws StopParseException since there can be no valid xml-stylesheet processing
   *                            instructions past the first element.
   */
  public void startElement(
          String namespaceURI, String localName, String qName, Attributes atts)
            throws org.xml.sax.SAXException
  {
    throw new StopParseException();
  }

  /**
    * Added additional getter and setter methods for the Base Id
    * to fix bugzilla bug 24187
    *
    * <p>
    *  为Base Id添加了额外的getter和setter方法来修复bugzilla错误24187
    */
   public void setBaseId(String baseId) {
       m_baseID = baseId;

   }
   public String  getBaseId() {
       return m_baseID ;
   }

}
