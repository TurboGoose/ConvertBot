package bot.handlers.scripts.helperclasses;

public class ScriptStage {
    private enum Stage {CHOOSING_CONVERSION, LOADING_FILE}

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
}
