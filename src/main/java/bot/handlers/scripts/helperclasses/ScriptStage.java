package bot.handlers.scripts.helperclasses;

public class ScriptStage {
    private enum Stage {CHOOSING_CONVERSION, LOADING_FILE, COMPLETED}

    private Stage stage;

    public boolean isChoosingConversion() {
        return stage == Stage.CHOOSING_CONVERSION;
    }

    public void setChoosingConversion() {
        stage = Stage.CHOOSING_CONVERSION;
    }

    public boolean isLoadingFile() {
        return stage == Stage.LOADING_FILE;
    }

    public void setLoadingFile() {
        stage = Stage.LOADING_FILE;
    }

    public boolean isCompleted() {
        return stage == Stage.COMPLETED;
    }

    public void setCompleted() {
        stage = Stage.COMPLETED;
    }
}

