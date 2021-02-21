package converter;

import java.io.IOException;

public interface ToPdfConvertible {
    PdfFile toPdf() throws IOException;
}
