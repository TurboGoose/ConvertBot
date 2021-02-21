package converter;

import java.io.IOException;

public interface ToDocxConvertible {
    DocxFile toDocx() throws IOException;
}
