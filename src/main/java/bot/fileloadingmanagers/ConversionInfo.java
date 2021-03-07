package bot.fileloadingmanagers;

import convertations.conversions.Conversion;

import java.util.Objects;

public class ConversionInfo {
    private final String inputFileUniqueId;
    private final Conversion conversion;

    public ConversionInfo(String inputFileUniqueId, Conversion conversion) {
        this.inputFileUniqueId = inputFileUniqueId;
        this.conversion = conversion;
    }

    public String getInputFileUniqueId() {
        return inputFileUniqueId;
    }

    public Conversion getConversion() {
        return conversion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversionInfo that = (ConversionInfo) o;
        return Objects.equals(conversion, that.conversion) && Objects.equals(inputFileUniqueId, that.inputFileUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversion, inputFileUniqueId);
    }
}
