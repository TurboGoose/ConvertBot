package convertations.converters.docconverters.factory;

import convertations.conversions.Conversion;
import convertations.converters.docconverters.DocConverter;

public interface AbstractDocConverterFactory {
    DocConverter getConverter(Conversion conversion);
}
