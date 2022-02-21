/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1997, 2003, Oracle and/or its affiliates. All rights reserved.
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


/* Generated By:JJTree: Do not edit this line. Node.java */

package com.sun.jmx.snmp.IPAcl;

/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
/* <p>
/*  建立父母和孩子关系的机制
/* 
/* 
   between nodes. */

interface Node {

  /** This method is called after the node has been made the current
  /* <p>
  /* 
    node.  It indicates that child nodes can now be added to it. */
  public void jjtOpen();

  /** This method is called after all the child nodes have been
  /* <p>
  /* 
    added. */
  public void jjtClose();

  /** This pair of methods are used to inform the node of its
  /* <p>
  /* 
    parent. */
  public void jjtSetParent(Node n);
  public Node jjtGetParent();

  /** This method tells the node to add its argument to the node's
  /* <p>
  /* 
    list of children.  */
  public void jjtAddChild(Node n, int i);

  /** This method returns a child node.  The children are numbered
  /* <p>
  /* 
     from zero, left to right. */
  public Node jjtGetChild(int i);

  /** Return the number of children the node has. */
  public int jjtGetNumChildren();
}
