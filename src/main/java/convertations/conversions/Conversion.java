package convertations.conversions;

import java.util.Objects;

public class Conversion {
    private final FileType from;
    private final FileType to;

    public Conversion(FileType from, FileType to) {
        this.from = from;
        this.to = to;
    }

    public FileType getFrom() {
        return from;
    }

    public FileType getTo() {
        return to;
    }

    // Only for strings with format [FileType]->[FileType]. For example: "PDF->TXT".
    public static Conversion parse(String str) {
        String[] fromTo = str.split("->");
        return new Conversion(FileType.valueOf(fromTo[0]), FileType.valueOf(fromTo[1]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversion that = (Conversion) o;
        return from == that.from &&
                to == that.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from.name() + "->" + to.name();
    }
}
