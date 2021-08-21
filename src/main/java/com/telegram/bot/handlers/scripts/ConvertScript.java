package com.telegram.bot.handlers.scripts;

import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.fileloadingmanagers.FileLoadingManager;
import com.telegram.bot.fileloadingmanagers.FileLoadingManagerImpl;
import com.telegram.bot.handlers.scripts.helperclasses.DataDownloader;
import com.telegram.bot.handlers.scripts.helperclasses.storage.FixedSizeList;
import com.telegram.bot.handlers.scripts.helperclasses.ReplyKeyboards;
import com.telegram.bot.handlers.scripts.helperclasses.storage.Storage;
import com.telegram.convertations.conversions.SupportedFileExtensions;
import com.telegram.convertations.converters.Converter;
import com.telegram.convertations.converters.Img2PdfConverter;
import com.telegram.utils.FileNameTools;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class ConvertScript extends AbstractScript {
    private static final String STOP_WORD = "Done";
    public static final int CAPACITY = 10;
    private boolean running = false;
    private final String chatId;
    private final Converter converter = new Img2PdfConverter();
    private final FileLoadingManager<String, String> loadingManager = FileLoadingManagerImpl.getInstance();
    private final Storage<Document> images = new FixedSizeList<>(CAPACITY);


    public ConvertScript(TelegramLongPollingBot bot, String chatId) {
        super(bot);
        this.chatId = chatId;
    }

    @Override
    public void start() {
        running = true;
        LOG.debug("[{}] Image converting script has been started.", chatId);
        sendTextReply(chatId, "Upload your photos", ReplyKeyboards.getButtonWithText(STOP_WORD));
    }

    @Override
    public void update(Update update) {
        if (running) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    if (STOP_WORD.equals(message.getText())) {
                        if (images.isEmpty()) {
                            sendTextReply(chatId, "You have to upload at least one photo", ReplyKeyboards.removeKeyboard());
                            LOG.debug("[{}] No photo has been uploaded.", chatId);
                        } else {
                            convertAndUploadImages();
                        }
                        stop();
                    }
                } else if (message.hasDocument()) {
                    Document document = message.getDocument();
                    if (hasRightExtensions(document)) {
                        if (!addDocumentWithFailMessage(document, chatId)) {
                            convertAndUploadImages();
                            stop();
                        }
                    } else {
                        sendTextReply(chatId, "Wrong file extension");
                        LOG.debug("[{}] Document {} has wrong extension.", chatId, document.getFileName());
                    }
                } else if (message.hasPhoto()) {
                    List<PhotoSize> photos = message.getPhoto();
                    Document document = photoSizeToDocument(photos.get(photos.size() - 1));
                    if (!addDocumentWithFailMessage(document, chatId)) {
                        convertAndUploadImages();
                        stop();
                    }
                    LOG.debug("[{}] Photo {} has been downloaded. Current fileId is {}.",
                            chatId, document.getFileUniqueId(), document.getFileId());
                }
            }
        }
    }

    private boolean hasRightExtensions(Document document) {
        String extension = FileNameTools.extractExtension(document.getFileName());
        return SupportedFileExtensions.get().contains(extension.toLowerCase());
    }

    @Override
    public void stop() {
        if (running) {
            ChatStates.getInstance().put(chatId, null);
            LOG.debug("[{}] Image converting script has been stopped.", chatId);
        }
    }

    private void convertAndUploadImages() {
        String ids = combineIds();
        if (!(loadingManager.contains(ids) && sendDocumentReply(chatId, loadingManager.get(ids)) != null)) {
            Document uploadedDocument = sendDocument(convertImages(downloadImages()));
            if (uploadedDocument != null) {
                saveDocumentInLoadingManager(ids, uploadedDocument);
                LOG.debug("[{}] Document {} has been saved in loading manager. Photo ids: {}. FileId: {}.",
                        chatId, uploadedDocument.getFileUniqueId(), ids, uploadedDocument.getFileId());
            }
        }
    }

    private String combineIds() {
        StringBuilder result = new StringBuilder();
        images.forEach(p -> result.append(p.getFileUniqueId()).append(";"));
        return result.toString();
    }

    private List<File> downloadImages() {
        DataDownloader downloader = new DataDownloader(bot);
        List<File> result = new LinkedList<>();
        for (Document img : images) {
            result.add(downloader.downloadDocument(img));
        }
        return result;
    }

    private File convertImages(List<File> inputFiles) {
        File outputFile = converter.convert(inputFiles);
        inputFiles.forEach(File::delete);
        return outputFile;
    }

    private Document sendDocument(File file) {
        String filename = file.getName();
        Document uploadedDocument = sendDocumentReply(chatId, file,
                "images" + filename.substring(filename.lastIndexOf('.')), ReplyKeyboards.removeKeyboard());
        file.delete();
        return uploadedDocument;
    }

    private void saveDocumentInLoadingManager(String id, Document document) {
        loadingManager.put(id, document.getFileId());
    }

    private boolean addDocumentWithFailMessage(Document document, String chatId) {
        if (!images.add(document)) {
            int capacity = images.getCapacity();
            sendTextReply(chatId, String.format("You have already loaded maximum number of photos (%d)", capacity));
            LOG.debug("[{}] Maximum number of photos ({}) has already been loaded.", chatId, capacity);
            return false;
        }
        return true;
    }

    private Document photoSizeToDocument(PhotoSize photoSize) {
        Document doc = new Document();
        doc.setFileId(photoSize.getFileId());
        doc.setFileUniqueId(photoSize.getFileUniqueId());
        doc.setThumb(photoSize);
        doc.setFileName(String.format("IMG%s.jpg", photoSize.getFileUniqueId()));
        doc.setMimeType("image/jpeg");
        doc.setFileSize(photoSize.getFileSize());
        return doc;
    }
}
