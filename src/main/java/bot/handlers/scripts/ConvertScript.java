package bot.handlers.scripts;

import bot.fileloadingmanagers.ConversionInfo;
import bot.fileloadingmanagers.FileLoadingManager;
import bot.fileloadingmanagers.TelegramFileLoadingManager;
import convertations.conversions.AvailableConversions;
import convertations.conversions.Conversion;
import convertations.converters.Converter;
import convertations.factory.AbstractConverterFactory;
import convertations.factory.ConverterFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tools.files.FileNameTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ConvertScript extends AbstractScript {
    private final AbstractConverterFactory factory = new ConverterFactory();
    private final FileLoadingManager<ConversionInfo, String> loadingManager = new TelegramFileLoadingManager();
    private final Map<String, State> chatStates = new HashMap<>();

    public ConvertScript(TelegramLongPollingBot bot) {
        super(bot);
    }

    @Override
    public void start(String chatId) {
        chatStates.putIfAbsent(chatId, new State());
        chatStates.get(chatId).setChoosingConversion();
        sendTextReply(chatId, "What type of conversion do you want to do?", getKeyboardWithAvailableConversions());
    }

    private ReplyKeyboard getKeyboardWithAvailableConversions() {
        final int MAX_BUTTONS_IN_ROW = 2;
        List<Conversion> conversions = AvailableConversions.getAvailable();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int count = 0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Conversion conversion : conversions) {
            if (count == MAX_BUTTONS_IN_ROW) {
                buttons.add(row);
                row = new ArrayList<>();
                count = 0;
            }
            InlineKeyboardButton button = new InlineKeyboardButton(conversion.toString());
            button.setCallbackData(conversion.toString());
            row.add(button);
            count++;
        }
        if (!row.isEmpty()) {
            buttons.add(row);
        }
        return new InlineKeyboardMarkup(buttons);
    }

    @Override
    public void update(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            answerCallbackQuery(callbackQuery);
            if (chatStates.containsKey(chatId)) {
                State state = chatStates.get(chatId);
                if (state.isChoosingConversion()) {
                    Conversion chosenConversion = Conversion.parse(callbackQuery.getData());
                    sendTextReply(chatId, "Load your " + chosenConversion.getFrom().toString() + " file");
                    state.setLoadingFile(chosenConversion);
                }
            }
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            if (chatStates.containsKey(chatId)) {
                State state = chatStates.get(chatId);
                if (state.isLoadingFile()) {
                    if (message.hasDocument()) {
                        Document document = message.getDocument();
                        String extension = FileNameTools.extractExtension(document.getFileName()).toLowerCase();
                        Conversion conversion = state.getConversion();
                        if (extension.equals(conversion.getFrom().name().toLowerCase())) {
                            ConversionInfo info = new ConversionInfo(document.getFileUniqueId(), conversion);
                            if (!(loadingManager.contains(info) && sendDocumentReply(chatId, loadingManager.get(info)) != null)) {
                                Document uploadedDocument = convertAndSendDocument(chatId, document, conversion);
                                if (uploadedDocument != null) {
                                    saveDocumentInLoadingManager(uploadedDocument, info);
                                }
                            }
                        } else {
                            sendTextReply(chatId, "Wrong file extension");
                        }

                    } else {
                        sendTextReply(chatId, "Document required");
                    }


                    if (message.hasPhoto()) {
                        List<PhotoSize> photos = message.getPhoto();
                        Conversion conversion = state.getConversion();
                        ConversionInfo info = new ConversionInfo(combinePhotoIds(photos), conversion);
                        if (!(loadingManager.contains(info) && sendDocumentReply(chatId, loadingManager.get(info)) != null)) {
                            Document uploadedDocument = convertAndSendImages(chatId, photos, conversion);
                            if (uploadedDocument != null) {
                                saveDocumentInLoadingManager(uploadedDocument, info);
                            }
                        }
                    } else {
                        sendTextReply(chatId, "Photos required");
                    }
                    stop(chatId);
                }
            }
        }
    }

    private String combinePhotoIds(List<PhotoSize> photos) {
        StringBuilder result = new StringBuilder();
        photos.forEach(p -> result.append(p.getFileUniqueId()).append(";"));
        return result.toString();
    }

    private Document convertAndSendDocument(String chatId, Document document, Conversion conversion) {
        File inputFile = downloadDocument(document);
        Converter converter = factory.getConverter(conversion);
        File outputFile = converter.convert(inputFile);
        Document uploadedDocument = sendDocumentReply(chatId, outputFile,
                FileNameTools.extractFilenameWithoutExtension(
                        document.getFileName()) + "." + conversion.getTo().name().toLowerCase());
        inputFile.delete();
        outputFile.delete();
        return uploadedDocument;
    }

    private File downloadDocument(Document document) {
        String fileName = document.getFileName();
        GetFile getFile = new GetFile(document.getFileId());
        try {
            File outputFile = File.createTempFile(
                    FileNameTools.extractFilenameWithoutExtension(fileName),
                    "." + FileNameTools.extractExtension(fileName));
            outputFile.deleteOnExit();
            return bot.downloadFile(bot.execute(getFile), outputFile);
        } catch (TelegramApiException exc) {
            throw new IllegalStateException(exc);
        } catch (IOException exc) {
            throw new IllegalStateException("Unable to create temporary file for " + fileName);
        }
    }

    private void saveDocumentInLoadingManager(Document document, ConversionInfo conversionInfo) {
        loadingManager.put(conversionInfo, document.getFileId());
    }

    private Document convertAndSendImages(String chatId, List<PhotoSize> photos, Conversion conversion) {
        File inputDir = downloadPhotos(photos);
        Converter converter = factory.getConverter(conversion);
        File outputFile = converter.convert(inputDir);
        return sendDocumentReply(chatId, outputFile, "images." + conversion.getTo().name().toLowerCase());
    }

    private File downloadPhotos(List<PhotoSize> photos) {
        try {
            File tempDir = Files.createTempDirectory("images").toFile();
            for (PhotoSize photo : photos) {
                String fileName = photo.getFileUniqueId();
                GetFile getFile = new GetFile(photo.getFileId());
                File outputFile = File.createTempFile(fileName, ".jpg", tempDir);
                bot.downloadFile(bot.execute(getFile), outputFile);
            }
            tempDir.deleteOnExit();
            return tempDir;
        } catch (TelegramApiException exc) {
            throw new IllegalStateException(exc);
        } catch (IOException exc) {
            throw new IllegalStateException("Unable to create temporary file or directory");
        }
    }


    @Override
    public void stop(String chatId) {
        if (chatStates.containsKey(chatId)) {
            chatStates.get(chatId).setStop();
        }
    }

    private static class State {
        private enum DialogStage {CHOOSING_CONVERSION, LOADING_FILE, NONE}

        private boolean running = false;
        private DialogStage currentDialogStage = DialogStage.NONE;
        private Conversion conversion = null;

        Conversion getConversion() {
            return conversion;
        }

        void setChoosingConversion() {
            running = true;
            currentDialogStage = DialogStage.CHOOSING_CONVERSION;
            conversion = null;
        }

        boolean isChoosingConversion() {
            return running && currentDialogStage == DialogStage.CHOOSING_CONVERSION;
        }

        void setLoadingFile(Conversion conversion) {
            running = true;
            this.conversion = conversion;
            currentDialogStage = DialogStage.LOADING_FILE;
        }

        boolean isLoadingFile() {
            return running && currentDialogStage == DialogStage.LOADING_FILE;
        }

        void setStop() {
            running = false;
            currentDialogStage = DialogStage.NONE;
            conversion = null;
        }
    }
}
