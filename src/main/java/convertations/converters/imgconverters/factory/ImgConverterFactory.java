package convertations.converters.imgconverters.factory;

import convertations.conversions.Conversion;
import convertations.conversions.FileType;
import convertations.converters.imgconverters.ImgConverter;
import convertations.converters.imgconverters.JpgToPdfImgConverter;

public class ImgConverterFactory implements AbstractImgConverterFactory {
    @Override
    public ImgConverter getConverter(Conversion conversion) {
        if (conversion.getFrom().equals(FileType.JPG) && conversion.getTo().equals(FileType.PDF)) {
            return new JpgToPdfImgConverter();
        }
        return null;
    }
}
