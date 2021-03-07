package bot.filemanager;

import java.time.LocalDateTime;

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
}


