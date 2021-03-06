package convertations.factory;

import convertations.conversions.Conversion;
import convertations.conversions.FileType;
import convertations.converters.Converter;
import convertations.converters.PdfToTxtConverter;
import convertations.converters.TxtToPdfConverter;

public class ConverterFactory implements AbstractConverterFactory {
    @Override
    public Converter getConverter(Conversion conversion) {
        Converter result = null;
        if (conversion.getFrom().equals(FileType.PDF) &&
                conversion.getTo().equals(FileType.TXT)) {
            result = new PdfToTxtConverter();
        } else if (conversion.getFrom().equals(FileType.TXT) &&
                conversion.getTo().equals(FileType.PDF)) {
            result = new TxtToPdfConverter();
        }
        return result;
    }
}
