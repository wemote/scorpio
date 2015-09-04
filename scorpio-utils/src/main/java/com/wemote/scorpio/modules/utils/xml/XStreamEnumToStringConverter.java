package com.wemote.scorpio.modules.utils.xml;

import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

import java.util.Map;

public class XStreamEnumToStringConverter extends EnumToStringConverter {

    @SuppressWarnings("unchecked")
    public XStreamEnumToStringConverter(Class type) {
        super(type);
    }

    @SuppressWarnings("unchecked")
    public XStreamEnumToStringConverter(Class type, Map strings) {
        super(type, strings);
    }

    @Override
    public String toString(Object obj) {
        return "<![CDATA[" + super.toString(obj) + "]]>";
    }
}
