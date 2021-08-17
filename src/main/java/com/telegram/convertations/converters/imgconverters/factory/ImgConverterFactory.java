package com.telegram.convertations.converters.imgconverters.factory;

import com.telegram.convertations.conversions.Conversion;
import com.telegram.convertations.conversions.FileType;
import com.telegram.convertations.converters.imgconverters.ImgConverter;
import com.telegram.convertations.converters.imgconverters.JpgToPdfImgConverter;

public class ImgConverterFactory implements AbstractImgConverterFactory {
    @Override
    public ImgConverter getConverter(Conversion conversion) {
        if (conversion.getFrom().equals(FileType.JPG) && conversion.getTo().equals(FileType.PDF)) {
            return new JpgToPdfImgConverter();
        }
        return null;
    }
}
