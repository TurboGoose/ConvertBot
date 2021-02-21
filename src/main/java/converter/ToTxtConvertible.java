package converter;

import java.io.IOException;

public interface ToTxtConvertible {
    TxtFile toTxt() throws IOException;
}
