package bot.fileloadingmanagers;

import java.time.LocalDateTime;
import java.util.Objects;

public class LoadingInfo {
    private String loadingId;
    private LocalDateTime loadingIdExpired;

    public LoadingInfo(String loadingId, LocalDateTime loadingIdExpired) {
        this.loadingId = loadingId;
        this.loadingIdExpired = loadingIdExpired;
    }

    public String getLoadingId() {
        return loadingId;
    }

    public void setLoadingId(String loadingId) {
        this.loadingId = loadingId;
    }

    public LocalDateTime getLoadingIdExpired() {
        return loadingIdExpired;
    }

    public void setLoadingIdExpired(LocalDateTime loadingIdExpired) {
        this.loadingIdExpired = loadingIdExpired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadingInfo that = (LoadingInfo) o;
        return Objects.equals(loadingId, that.loadingId) && Objects.equals(loadingIdExpired, that.loadingIdExpired);
    }

    @Override
    public int hashCode() {
        return Objects.hash(loadingId, loadingIdExpired);
    }
}


