package com.telegram.convertations.converters.imgconverters.factory;

import com.telegram.convertations.conversions.Conversion;
import com.telegram.convertations.converters.imgconverters.ImgConverter;

public interface AbstractImgConverterFactory {
    ImgConverter getConverter(Conversion conversion);
}
