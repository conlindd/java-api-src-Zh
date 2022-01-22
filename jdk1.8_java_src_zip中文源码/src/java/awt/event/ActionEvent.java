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
import java.awt.Event;
import java.lang.annotation.Native;

/**
 * A semantic event which indicates that a component-defined action occurred.
 * This high-level event is generated by a component (such as a
 * <code>Button</code>) when
 * the component-specific action occurs (such as being pressed).
 * The event is passed to every <code>ActionListener</code> object
 * that registered to receive such events using the component's
 * <code>addActionListener</code> method.
 * <p>
 * <b>Note:</b> To invoke an <code>ActionEvent</code> on a
 * <code>Button</code> using the keyboard, use the Space bar.
 * <P>
 * The object that implements the <code>ActionListener</code> interface
 * gets this <code>ActionEvent</code> when the event occurs. The listener
 * is therefore spared the details of processing individual mouse movements
 * and mouse clicks, and can instead process a "meaningful" (semantic)
 * event like "button pressed".
 * <p>
 * An unspecified behavior will be caused if the {@code id} parameter
 * of any particular {@code ActionEvent} instance is not
 * in the range from {@code ACTION_FIRST} to {@code ACTION_LAST}.
 *
 * <p>
 *  指示组件定义的操作发生的语义事件。当特定于组件的动作发生(例如被按下)时,该高级事件由组件(例如<code> Button </code>)生成。
 * 事件被传递到使用组件的<code> addActionListener </code>方法注册接收此类事件的每个<code> ActionListener </code>对象。
 * <p>
 *  <b>注意：</b>要使用键盘在<code> Button </code>上调用<code> ActionEvent </code>,请使用空格键。
 * <P>
 *  实现<code> ActionListener </code>接口的对象在事件发生时获取此<code> ActionEvent </code>。
 * 因此,监听器不需要处理单个鼠标移动和鼠标点击的细节,而是可以处理像"按下按钮"那样的"有意义"(语义)事件。
 * <p>
 *  如果任何特定{@code ActionEvent}实例的{@code id}参数不在{@code ACTION_FIRST}到{@code ACTION_LAST}的范围内,则会导致未指定的行为。
 * 
 * 
 * @see ActionListener
 * @see <a href="https://docs.oracle.com/javase/tutorial/uiswing/events/actionlistener.html">Tutorial: How to Write an Action Listener</a>
 *
 * @author Carl Quinn
 * @since 1.1
 */
public class ActionEvent extends AWTEvent {

    /**
     * The shift modifier. An indicator that the shift key was held
     * down during the event.
     * <p>
     *  换档修饰符。在事件期间按下shift键的指示符。
     * 
     */
    public static final int SHIFT_MASK          = Event.SHIFT_MASK;

    /**
     * The control modifier. An indicator that the control key was held
     * down during the event.
     * <p>
     *  控制修改器。在事件期间控制键被按下的指示器。
     * 
     */
    public static final int CTRL_MASK           = Event.CTRL_MASK;

    /**
     * The meta modifier. An indicator that the meta key was held
     * down during the event.
     * <p>
     *  元修饰符。事件期间元键被按住的指示符。
     * 
     */
    public static final int META_MASK           = Event.META_MASK;

    /**
     * The alt modifier. An indicator that the alt key was held
     * down during the event.
     * <p>
     *  alt修饰符。事件期间Alt键被按下的指示符。
     * 
     */
    public static final int ALT_MASK            = Event.ALT_MASK;


    /**
     * The first number in the range of ids used for action events.
     * <p>
     * 用于操作事件的ids范围中的第一个数字。
     * 
     */
    public static final int ACTION_FIRST                = 1001;

    /**
     * The last number in the range of ids used for action events.
     * <p>
     *  用于操作事件的ids范围中的最后一个数字。
     * 
     */
    public static final int ACTION_LAST                 = 1001;

    /**
     * This event id indicates that a meaningful action occurred.
     * <p>
     *  此事件标识表示发生了有意义的操作。
     * 
     */
    @Native public static final int ACTION_PERFORMED    = ACTION_FIRST; //Event.ACTION_EVENT

    /**
     * The nonlocalized string that gives more details
     * of what actually caused the event.
     * This information is very specific to the component
     * that fired it.

     * <p>
     *  非本地化字符串,提供更多详细信息实际引起的事件。此信息非常特定于启动它的组件。
     * 
     * 
     * @serial
     * @see #getActionCommand
     */
    String actionCommand;

    /**
     * Timestamp of when this event occurred. Because an ActionEvent is a high-
     * level, semantic event, the timestamp is typically the same as an
     * underlying InputEvent.
     *
     * <p>
     *  此事件发生时的时间戳。因为ActionEvent是高级语义事件,所以时间戳通常与底层InputEvent相同。
     * 
     * 
     * @serial
     * @see #getWhen
     */
    long when;

    /**
     * This represents the key modifier that was selected,
     * and is used to determine the state of the selected key.
     * If no modifier has been selected it will default to
     * zero.
     *
     * <p>
     *  这表示所选的键修改器,并用于确定所选键的状态。如果没有选择修饰符,它将默认为零。
     * 
     * 
     * @serial
     * @see #getModifiers
     */
    int modifiers;

    /*
     * JDK 1.1 serialVersionUID
     * <p>
     *  JDK 1.1 serialVersionUID
     * 
     */
    private static final long serialVersionUID = -7671078796273832149L;

    /**
     * Constructs an <code>ActionEvent</code> object.
     * <p>
     * This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     * A <code>null</code> <code>command</code> string is legal,
     * but not recommended.
     *
     * <p>
     *  构造一个<code> ActionEvent </code>对象。
     * <p>
     *  如果<code> source </code>是<code> null </code>,此方法会抛出<code> IllegalArgumentException </code>。
     *  <code> null </code> <code>命令</code>字符串是合法的,但不推荐。
     * 
     * 
     * @param source  The object that originated the event
     * @param id      An integer that identifies the event.
     *                     For information on allowable values, see
     *                     the class description for {@link ActionEvent}
     * @param command A string that may specify a command (possibly one
     *                of several) associated with the event
     * @throws IllegalArgumentException if <code>source</code> is null
     * @see #getSource()
     * @see #getID()
     * @see #getActionCommand()
     */
    public ActionEvent(Object source, int id, String command) {
        this(source, id, command, 0);
    }

    /**
     * Constructs an <code>ActionEvent</code> object with modifier keys.
     * <p>
     * This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     * A <code>null</code> <code>command</code> string is legal,
     * but not recommended.
     *
     * <p>
     *  用修饰键构造一个<code> ActionEvent </code>对象。
     * <p>
     *  如果<code> source </code>是<code> null </code>,此方法会抛出<code> IllegalArgumentException </code>。
     *  <code> null </code> <code>命令</code>字符串是合法的,但不推荐。
     * 
     * 
     * @param source  The object that originated the event
     * @param id      An integer that identifies the event.
     *                     For information on allowable values, see
     *                     the class description for {@link ActionEvent}
     * @param command A string that may specify a command (possibly one
     *                of several) associated with the event
     * @param modifiers The modifier keys down during event
     *                  (shift, ctrl, alt, meta).
     *                  Passing negative parameter is not recommended.
     *                  Zero value means that no modifiers were passed
     * @throws IllegalArgumentException if <code>source</code> is null
     * @see #getSource()
     * @see #getID()
     * @see #getActionCommand()
     * @see #getModifiers()
     */
    public ActionEvent(Object source, int id, String command, int modifiers) {
        this(source, id, command, 0, modifiers);
    }

    /**
     * Constructs an <code>ActionEvent</code> object with the specified
     * modifier keys and timestamp.
     * <p>
     * This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     * A <code>null</code> <code>command</code> string is legal,
     * but not recommended.
     *
     * <p>
     *  构造具有指定的修饰键和时间戳的<code> ActionEvent </code>对象。
     * <p>
     *  如果<code> source </code>是<code> null </code>,此方法会抛出<code> IllegalArgumentException </code>。
     *  <code> null </code> <code>命令</code>字符串是合法的,但不推荐。
     * 
     * 
     * @param source    The object that originated the event
     * @param id      An integer that identifies the event.
     *                     For information on allowable values, see
     *                     the class description for {@link ActionEvent}
     * @param command A string that may specify a command (possibly one
     *                of several) associated with the event
     * @param modifiers The modifier keys down during event
     *                  (shift, ctrl, alt, meta).
     *                  Passing negative parameter is not recommended.
     *                  Zero value means that no modifiers were passed
     * @param when   A long that gives the time the event occurred.
     *               Passing negative or zero value
     *               is not recommended
     * @throws IllegalArgumentException if <code>source</code> is null
     * @see #getSource()
     * @see #getID()
     * @see #getActionCommand()
     * @see #getModifiers()
     * @see #getWhen()
     *
     * @since 1.4
     */
    public ActionEvent(Object source, int id, String command, long when,
                       int modifiers) {
        super(source, id);
        this.actionCommand = command;
        this.when = when;
        this.modifiers = modifiers;
    }

    /**
     * Returns the command string associated with this action.
     * This string allows a "modal" component to specify one of several
     * commands, depending on its state. For example, a single button might
     * toggle between "show details" and "hide details". The source object
     * and the event would be the same in each case, but the command string
     * would identify the intended action.
     * <p>
     * Note that if a <code>null</code> command string was passed
     * to the constructor for this <code>ActionEvent</code>, this
     * this method returns <code>null</code>.
     *
     * <p>
     * 返回与此操作关联的命令字符串。此字符串允许"模态"组件根据其状态指定几个命令之一。例如,单个按钮可以在"显示详细信息"和"隐藏详细信息"之间切换。
     * 源对象和事件在每种情况下都是相同的,但命令字符串将标识预期的操作。
     * <p>
     *  注意,如果<code> null </code>命令字符串被传递给这个<code> ActionEvent </code>的构造函数,这个方法返回<code> null </code>。
     * 
     * 
     * @return the string identifying the command for this event
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Returns the timestamp of when this event occurred. Because an
     * ActionEvent is a high-level, semantic event, the timestamp is typically
     * the same as an underlying InputEvent.
     *
     * <p>
     *  返回此事件发生时的时间戳。因为ActionEvent是高级语义事件,所以时间戳通常与底层InputEvent相同。
     * 
     * 
     * @return this event's timestamp
     * @since 1.4
     */
    public long getWhen() {
        return when;
    }

    /**
     * Returns the modifier keys held down during this action event.
     *
     * <p>
     *  返回此操作事件期间按住的修改键。
     * 
     * 
     * @return the bitwise-or of the modifier constants
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns a parameter string identifying this action event.
     * This method is useful for event-logging and for debugging.
     *
     * <p>
     *  返回标识此操作事件的参数字符串。此方法对事件记录和调试非常有用。
     * 
     * @return a string identifying the event and its associated command
     */
    public String paramString() {
        String typeStr;
        switch(id) {
          case ACTION_PERFORMED:
              typeStr = "ACTION_PERFORMED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + ",cmd="+actionCommand+",when="+when+",modifiers="+
            KeyEvent.getKeyModifiersText(modifiers);
    }
}
