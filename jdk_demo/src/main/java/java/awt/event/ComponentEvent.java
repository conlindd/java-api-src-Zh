/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Rectangle;
import java.lang.annotation.Native;

/**
 * A low-level event which indicates that a component moved, changed
 * size, or changed visibility (also, the root class for the other
 * component-level events).
 * <P>
 * Component events are provided for notification purposes ONLY;
 * The AWT will automatically handle component moves and resizes
 * internally so that GUI layout works properly regardless of
 * whether a program is receiving these events or not.
 * <P>
 * In addition to serving as the base class for other component-related
 * events (InputEvent, FocusEvent, WindowEvent, ContainerEvent),
 * this class defines the events that indicate changes in
 * a component's size, position, or visibility.
 * <P>
 * This low-level event is generated by a component object (such as a
 * List) when the component is moved, resized, rendered invisible, or made
 * visible again. The event is passed to every <code>ComponentListener</code>
 * or <code>ComponentAdapter</code> object which registered to receive such
 * events using the component's <code>addComponentListener</code> method.
 * (<code>ComponentAdapter</code> objects implement the
 * <code>ComponentListener</code> interface.) Each such listener object
 * gets this <code>ComponentEvent</code> when the event occurs.
 * <p>
 * An unspecified behavior will be caused if the {@code id} parameter
 * of any particular {@code ComponentEvent} instance is not
 * in the range from {@code COMPONENT_FIRST} to {@code COMPONENT_LAST}.
 *
 * <p>
 *  一个低级事件,指示组件移动,更改大小或更改的可见性(也是其他组件级事件的根类)。
 * <P>
 *  组件事件仅用于通知目的; AWT将自动处理组件移动和内部调整大小,使GUI布局正常工作,而不管程序是否正在接收这些事件。
 * <P>
 *  除了用作其他组件相关事件(InputEvent,FocusEvent,WindowEvent,ContainerEvent)的基类之外,此类还定义了指示组件大小,位置或可见性更改的事件。
 * <P>
 *  此低级事件由组件对象(例如List)在组件移动,调整大小,呈现不可见或再次可见时生成。
 * 该事件被传递到使用组件的<code> addComponentListener </code>方法注册接收这些事件的每个<code> ComponentListener </code>或<code> C
 * omponentAdapter </code>对象。
 *  此低级事件由组件对象(例如List)在组件移动,调整大小,呈现不可见或再次可见时生成。
 *  (<code> ComponentAdapter </code>对象实现<code> ComponentListener </code>接口。
 * )当事件发生时,每个这样的监听器对象都会获得这个<code> ComponentEvent </code>。
 * <p>
 * 如果任何特定{@code ComponentEvent}实例的{@code id}参数不在{@code COMPONENT_FIRST}到{@code COMPONENT_LAST}的范围内,则会导致未
 * 指定的行为。
 * 
 * 
 * @see ComponentAdapter
 * @see ComponentListener
 * @see <a href="https://docs.oracle.com/javase/tutorial/uiswing/events/componentlistener.html">Tutorial: Writing a Component Listener</a>
 *
 * @author Carl Quinn
 * @since 1.1
 */
public class ComponentEvent extends AWTEvent {

    /**
     * The first number in the range of ids used for component events.
     * <p>
     *  用于组件事件的ids范围中的第一个数字。
     * 
     */
    public static final int COMPONENT_FIRST             = 100;

    /**
     * The last number in the range of ids used for component events.
     * <p>
     *  用于组件事件的ids范围中的最后一个数字。
     * 
     */
    public static final int COMPONENT_LAST              = 103;

   /**
     * This event indicates that the component's position changed.
     * <p>
     *  此事件表示组件的位置已更改。
     * 
     */
    @Native public static final int COMPONENT_MOVED     = COMPONENT_FIRST;

    /**
     * This event indicates that the component's size changed.
     * <p>
     *  此事件指示组件的大小已更改。
     * 
     */
    @Native public static final int COMPONENT_RESIZED   = 1 + COMPONENT_FIRST;

    /**
     * This event indicates that the component was made visible.
     * <p>
     *  此事件表示组件可见。
     * 
     */
    @Native public static final int COMPONENT_SHOWN     = 2 + COMPONENT_FIRST;

    /**
     * This event indicates that the component was rendered invisible.
     * <p>
     *  此事件表示组件被隐藏。
     * 
     */
    @Native public static final int COMPONENT_HIDDEN    = 3 + COMPONENT_FIRST;

    /*
     * JDK 1.1 serialVersionUID
     * <p>
     *  JDK 1.1 serialVersionUID
     * 
     */
    private static final long serialVersionUID = 8101406823902992965L;

    /**
     * Constructs a <code>ComponentEvent</code> object.
     * <p> This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * <p>
     *  构造一个<code> ComponentEvent </code>对象。
     *  <p>如果<code> source </code>是<code> null </code>,此方法会抛出<code> IllegalArgumentException </code>。
     * 
     * 
     * @param source The <code>Component</code> that originated the event
     * @param id     An integer indicating the type of event.
     *                     For information on allowable values, see
     *                     the class description for {@link ComponentEvent}
     * @throws IllegalArgumentException if <code>source</code> is null
     * @see #getComponent()
     * @see #getID()
     */
    public ComponentEvent(Component source, int id) {
        super(source, id);
    }

    /**
     * Returns the originator of the event.
     *
     * <p>
     *  返回事件的发起者。
     * 
     * 
     * @return the <code>Component</code> object that originated
     * the event, or <code>null</code> if the object is not a
     * <code>Component</code>.
     */
    public Component getComponent() {
        return (source instanceof Component) ? (Component)source : null;
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * <p>
     *  返回标识此事件的参数字符串。此方法对事件记录和调试非常有用。
     * 
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        String typeStr;
        Rectangle b = (source !=null
                       ? ((Component)source).getBounds()
                       : null);

        switch(id) {
          case COMPONENT_SHOWN:
              typeStr = "COMPONENT_SHOWN";
              break;
          case COMPONENT_HIDDEN:
              typeStr = "COMPONENT_HIDDEN";
              break;
          case COMPONENT_MOVED:
              typeStr = "COMPONENT_MOVED ("+
                         b.x+","+b.y+" "+b.width+"x"+b.height+")";
              break;
          case COMPONENT_RESIZED:
              typeStr = "COMPONENT_RESIZED ("+
                         b.x+","+b.y+" "+b.width+"x"+b.height+")";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr;
    }
}
