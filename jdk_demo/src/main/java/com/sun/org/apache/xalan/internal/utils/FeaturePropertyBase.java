/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.org.apache.xalan.internal.utils;

import com.sun.org.apache.xalan.internal.XalanConstants;

/**
 * This is the base class for features and properties
 *
 * <p>
 *  这是功能和属性的基类
 * 
 */
public abstract class FeaturePropertyBase {

    /**
     * States of the settings of a property, in the order: default value, value
     * set by FEATURE_SECURE_PROCESSING, jaxp.properties file, jaxp system
     * properties, and jaxp api properties
     * <p>
     *  状态设置的属性,按顺序：默认值,由FEATURE_SECURE_PROCESSING设置的值,jaxp.properties文件,jaxp系统属性和jaxp api属性
     * 
     */
    public static enum State {
        //this order reflects the overriding order
        DEFAULT, FSP, JAXPDOTPROPERTIES, SYSTEMPROPERTY, APIPROPERTY
    }


    /**
     * Values of the properties as defined in enum Properties
     * <p>
     *  枚举属性中定义的属性值
     * 
     */
    String[] values = null;
    /**
     * States of the settings for each property in Properties above
     * <p>
     *  上述属性中每个属性的设置状态
     * 
     */
    State[] states = {State.DEFAULT, State.DEFAULT};


    /**
     * Set the value for a specific property.
     *
     * <p>
     *  设置特定属性的值。
     * 
     * 
     * @param property the property
     * @param state the state of the property
     * @param value the value of the property
     */
    public void setValue(Enum property, State state, String value) {
        //only update if it shall override
        if (state.compareTo(states[property.ordinal()]) >= 0) {
            values[property.ordinal()] = value;
            states[property.ordinal()] = state;
        }
    }

    /**
     * Set the value of a property by its index
     * <p>
     *  通过其索引设置属性的值
     * 
     * 
     * @param index the index of the property
     * @param state the state of the property
     * @param value the value of the property
     */
    public void setValue(int index, State state, String value) {
        //only update if it shall override
        if (state.compareTo(states[index]) >= 0) {
            values[index] = value;
            states[index] = state;
        }
    }

     /**
     * Set value by property name and state
     * <p>
     *  按属性名称和状态设置值
     * 
     * 
     * @param propertyName property name
     * @param state the state of the property
     * @param value the value of the property
     * @return true if the property is managed by the security property manager;
     *         false if otherwise.
     */
    public boolean setValue(String propertyName, State state, Object value) {
        int index = getIndex(propertyName);
        if (index > -1) {
            setValue(index, state, (String)value);
            return true;
        }
        return false;
    }

     /**
     * Set value by property name and state
     * <p>
     *  按属性名称和状态设置值
     * 
     * 
     * @param propertyName property name
     * @param state the state of the property
     * @param value the value of the property
     * @return true if the property is managed by the security property manager;
     *         false if otherwise.
     */
    public boolean setValue(String propertyName, State state, boolean value) {
        int index = getIndex(propertyName);
        if (index > -1) {
            if (value) {
                setValue(index, state, XalanConstants.FEATURE_TRUE);
            } else {
                setValue(index, state, XalanConstants.FEATURE_FALSE);
            }
            return true;
        }
        return false;
    }

    /**
     * Return the value of the specified property
     *
     * <p>
     *  返回指定属性的值
     * 
     * 
     * @param property the property
     * @return the value of the property
     */
    public String getValue(Enum property) {
        return values[property.ordinal()];
    }

    /**
     * Return the value of the specified property
     *
     * <p>
     *  返回指定属性的值
     * 
     * 
     * @param property the property
     * @return the value of the property
     */
    public String getValue(String property) {
        int index = getIndex(property);
        if (index > -1) {
            return getValueByIndex(index);
        }
        return null;
    }

    /**
     * Return the value of the specified property.
     *
     * <p>
     *  返回指定属性的值。
     * 
     * 
     * @param propertyName the property name
     * @return the value of the property as a string. If a property is managed
     * by this manager, its value shall not be null.
     */
    public String getValueAsString(String propertyName) {
        int index = getIndex(propertyName);
        if (index > -1) {
            return getValueByIndex(index);
        }

        return null;
    }

    /**
     * Return the value of a property by its ordinal
     * <p>
     *  通过其序数返回属性的值
     * 
     * 
     * @param index the index of a property
     * @return value of a property
     */
    public String getValueByIndex(int index) {
        return values[index];
    }

    /**
     * Get the index by property name
     * <p>
     *  按属性名称获取索引
     * 
     * 
     * @param propertyName property name
     * @return the index of the property if found; return -1 if not
     */
    public abstract int getIndex(String propertyName);

    public <E extends Enum<E>> int getIndex(Class<E> property, String propertyName) {
        for (Enum<E> enumItem : property.getEnumConstants()) {
            if (enumItem.toString().equals(propertyName)) {
                //internally, ordinal is used as index
                return enumItem.ordinal();
            }
        }
        return -1;
    };


    /**
     * Read from system properties, or those in jaxp.properties
     *
     * <p>
     *  从系统属性或jaxp.properties中读取
     * 
     * @param property the property
     * @param systemProperty the name of the system property
     */
    void getSystemProperty(Enum property, String systemProperty) {
        try {
            String value = SecuritySupport.getSystemProperty(systemProperty);
            if (value != null) {
                values[property.ordinal()] = value;
                states[property.ordinal()] = State.SYSTEMPROPERTY;
                return;
            }

            value = SecuritySupport.readJAXPProperty(systemProperty);
            if (value != null) {
                values[property.ordinal()] = value;
                states[property.ordinal()] = State.JAXPDOTPROPERTIES;
            }
        } catch (NumberFormatException e) {
            //invalid setting ignored
        }
    }
}
