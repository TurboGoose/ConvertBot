package convertations.factory;

import convertations.conversions.Conversion;
import convertations.converters.Converter;

public interface AbstractConverterFactory {
    Converter getConverter(Conversion conversion);
}
