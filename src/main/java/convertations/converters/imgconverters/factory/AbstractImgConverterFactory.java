package convertations.converters.imgconverters.factory;

import convertations.conversions.Conversion;
import convertations.converters.imgconverters.ImgConverter;

public interface AbstractImgConverterFactory {
    ImgConverter getConverter(Conversion conversion);
}
