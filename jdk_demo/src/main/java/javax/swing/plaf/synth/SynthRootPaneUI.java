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

package javax.swing.plaf.synth;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicRootPaneUI;

/**
 * Provides the Synth L&amp;F UI delegate for
 * {@link javax.swing.JRootPane}.
 *
 * <p>
 *  为{@link javax.swing.JRootPane}提供Synth L&amp; F UI委托。
 * 
 * 
 * @author Scott Violet
 * @since 1.7
 */
public class SynthRootPaneUI extends BasicRootPaneUI implements SynthUI {
    private SynthStyle style;

    /**
     * Creates a new UI object for the given component.
     *
     * <p>
     *  为给定组件创建一个新的UI对象。
     * 
     * 
     * @param c component to create UI object for
     * @return the UI object
     */
    public static ComponentUI createUI(JComponent c) {
        return new SynthRootPaneUI();
    }

    /**
     * {@inheritDoc}
     * <p>
     *  {@inheritDoc}
     * 
     */
    @Override
    protected void installDefaults(JRootPane c){
        updateStyle(c);
    }

    /**
     * {@inheritDoc}
     * <p>
     *  {@inheritDoc}
     * 
     */
    @Override
    protected void uninstallDefaults(JRootPane root) {
        SynthContext context = getContext(root, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    /**
     * {@inheritDoc}
     * <p>
     *  {@inheritDoc}
     * 
     */
    @Override
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(c, style, state);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private void updateStyle(JComponent  c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            if (oldStyle != null) {
                uninstallKeyboardActions((JRootPane)c);
                installKeyboardActions((JRootPane)c);
            }
        }
        context.dispose();
    }

    /**
     * Notifies this UI delegate to repaint the specified component.
     * This method paints the component background, then calls
     * the {@link #paint(SynthContext,Graphics)} method.
     *
     * <p>In general, this method does not need to be overridden by subclasses.
     * All Look and Feel rendering code should reside in the {@code paint} method.
     *
     * <p>
     *  通知此UI代理重新绘制指定的组件。此方法绘制组件背景,然后调用{@link #paint(SynthContext,Graphics)}方法。
     * 
     *  <p>通常,此方法不需要被子类覆盖。所有Look and Feel渲染代码应该驻留在{@code paint}方法中。
     * 
     * 
     * @param g the {@code Graphics} object used for painting
     * @param c the component being painted
     * @see #paint(SynthContext,Graphics)
     */
    @Override
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintRootPaneBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    /**
     * Paints the specified component according to the Look and Feel.
     * <p>This method is not used by Synth Look and Feel.
     * Painting is handled by the {@link #paint(SynthContext,Graphics)} method.
     *
     * <p>
     *  根据外观来绘制指定的组件。 <p>此方法不被Synth Look and Feel使用。绘画由{@link #paint(SynthContext,Graphics)}方法处理。
     * 
     * 
     * @param g the {@code Graphics} object used for painting
     * @param c the component being painted
     * @see #paint(SynthContext,Graphics)
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    /**
     * Paints the specified component. This implementation does nothing.
     *
     * <p>
     *  绘制指定的组件。这个实现什么也不做。
     * 
     * 
     * @param context context for the component being painted
     * @param g the {@code Graphics} object used for painting
     * @see #update(Graphics,JComponent)
     */
    protected void paint(SynthContext context, Graphics g) {
    }

    /**
     * {@inheritDoc}
     * <p>
     *  {@inheritDoc}
     * 
     */
    @Override
    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintRootPaneBorder(context, g, x, y, w, h);
    }

    /**
     * Invoked when a property changes on the root pane. If the event
     * indicates the <code>defaultButton</code> has changed, this will
     * reinstall the keyboard actions.
     * <p>
     *  在根窗格上的属性更改时调用。如果事件指示<code> defaultButton </code>已更改,则将重新安装键盘操作。
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JRootPane)e.getSource());
        }
        super.propertyChange(e);
    }
}
