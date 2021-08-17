package bot.fileloadingmanagers;

import convertations.conversions.Conversion;

import java.util.Objects;

public class ConversionInfo {
    private final String fileUniqueId;
    private final Conversion conversion;

    public ConversionInfo(String fileUniqueId, Conversion conversion) {
        this.fileUniqueId = fileUniqueId;
        this.conversion = conversion;
    }

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public Conversion getConversion() {
        return conversion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConversionInfo that = (ConversionInfo) o;
        return Objects.equals(conversion, that.conversion) && Objects.equals(fileUniqueId, that.fileUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversion, fileUniqueId);
    }

    @Override
    public String toString() {
        return "ConversionInfo{" +
                "FileUniqueId='" + fileUniqueId + '\'' +
                ", conversion=" + conversion +
                '}';
    }
}
