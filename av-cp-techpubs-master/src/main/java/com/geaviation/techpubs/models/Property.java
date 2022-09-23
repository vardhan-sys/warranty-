package com.geaviation.techpubs.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"propName", "propValue"})
@XmlRootElement(name = "property")
public class Property {

    @XmlElement(required = true)
    protected String propName;
    @XmlElement(required = true)
    protected String propValue;

    public Property() {

    }

    public Property(String strPropName, String strPropValue) {
        this.propName = strPropName;
        this.propValue = strPropValue;
    }

    /**
     * Gets the value of the propName property.
     *
     * @return possible object is {@link String }
     */
    public String getPropName() {
        return propName;
    }

    /**
     * Gets the value of the propValue property.
     *
     * @return possible object is {@link String }
     */
    public String getPropValue() {
        return propValue;
    }

    /**
     * Sets the value of the propName property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPropName(final String value) {
        this.propName = value;
    }

    /**
     * Sets the value of the propValue property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPropValue(final String value) {
        this.propValue = value;
    }
}