package com.telegram.convertations.converters.docconverters.factory;

import com.telegram.convertations.conversions.Conversion;
import com.telegram.convertations.converters.docconverters.DocConverter;

public interface AbstractDocConverterFactory {
    DocConverter getConverter(Conversion conversion);
}
