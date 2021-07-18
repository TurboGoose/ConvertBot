package convertations.factory;

import convertations.conversions.Conversion;
import convertations.conversions.FileType;
import convertations.converters.Converter;
import convertations.converters.JpgToPdfConverter;
import convertations.converters.PdfToTxtConverter;
import convertations.converters.TxtToPdfConverter;

public class ConverterFactory implements AbstractConverterFactory {
    @Override
    public Converter getConverter(Conversion conversion) {
        if (conversion.getFrom().equals(FileType.PDF) && conversion.getTo().equals(FileType.TXT)) {
            return new PdfToTxtConverter();
        }
        if (conversion.getFrom().equals(FileType.TXT) && conversion.getTo().equals(FileType.PDF)) {
            return new TxtToPdfConverter();
        }
        if (conversion.getFrom().equals(FileType.JPG) && conversion.getTo().equals(FileType.PDF)) {
            return new JpgToPdfConverter();
        }
        return null;
    }
}
