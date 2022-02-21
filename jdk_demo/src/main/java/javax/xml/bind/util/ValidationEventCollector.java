/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package javax.xml.bind.util;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link javax.xml.bind.ValidationEventHandler ValidationEventHandler}
 * implementation that collects all events.
 *
 * <p>
 * To use this class, create a new instance and pass it to the setEventHandler
 * method of the Validator, Unmarshaller, Marshaller class.  After the call to
 * validate or unmarshal completes, call the getEvents method to retrieve all
 * the reported errors and warnings.
 *
 * <p>
 *  {@link javax.xml.bind.ValidationEventHandler ValidationEventHandler}实现,收集所有事件。
 * 
 * <p>
 *  要使用此类,请创建一个新实例,并将其传递给Validator,Unmarshaller,Marshaller类的setEventHandler方法。
 * 调用validate或unmarshal完成后,调用getEvents方法检索所有报告的错误和警告。
 * 
 * 
 * @author <ul><li>Kohsuke Kawaguchi, Sun Microsystems, Inc.</li><li>Ryan Shoemaker, Sun Microsystems, Inc.</li><li>Joe Fialli, Sun Microsystems, Inc.</li></ul>
 * @see javax.xml.bind.Validator
 * @see javax.xml.bind.ValidationEventHandler
 * @see javax.xml.bind.ValidationEvent
 * @see javax.xml.bind.ValidationEventLocator
 * @since JAXB1.0
 */
public class ValidationEventCollector implements ValidationEventHandler
{
    private final List<ValidationEvent> events = new ArrayList<ValidationEvent>();

    /**
     * Return an array of ValidationEvent objects containing a copy of each of
     * the collected errors and warnings.
     *
     * <p>
     *  返回一个ValidationEvent对象数组,其中包含每个收集的错误和警告的副本。
     * 
     * 
     * @return
     *      a copy of all the collected errors and warnings or an empty array
     *      if there weren't any
     */
    public ValidationEvent[] getEvents() {
        return events.toArray(new ValidationEvent[events.size()]);
    }

    /**
     * Clear all collected errors and warnings.
     * <p>
     *  清除所有收集的错误和警告。
     * 
     */
    public void reset() {
        events.clear();
    }

    /**
     * Returns true if this event collector contains at least one
     * ValidationEvent.
     *
     * <p>
     *  如果此事件收集器至少包含一个ValidationEvent,则返回true。
     * 
     * @return true if this event collector contains at least one
     *         ValidationEvent, false otherwise
     */
    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public boolean handleEvent( ValidationEvent event ) {
        events.add(event);

        boolean retVal = true;
        switch( event.getSeverity() ) {
            case ValidationEvent.WARNING:
                retVal = true; // continue validation
                break;
            case ValidationEvent.ERROR:
                retVal = true; // continue validation
                break;
            case ValidationEvent.FATAL_ERROR:
                retVal = false; // halt validation
                break;
            default:
                _assert( false,
                         Messages.format( Messages.UNRECOGNIZED_SEVERITY,
                                 event.getSeverity() ) );
                break;
        }

        return retVal;
    }

    private static void _assert( boolean b, String msg ) {
        if( !b ) {
            throw new InternalError( msg );
        }
    }
}
