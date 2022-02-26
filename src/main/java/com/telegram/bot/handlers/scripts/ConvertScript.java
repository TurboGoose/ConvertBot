package com.telegram.bot.handlers.scripts;

import com.telegram.bot.chatservices.downloaders.DownloaderChatService;
import com.telegram.bot.chatservices.senders.SenderChatService;
import com.telegram.bot.chatstates.ChatStates;
import com.telegram.bot.fileloadingmanagers.FileLoadingManager;
import com.telegram.bot.fileloadingmanagers.FileLoadingManagerImpl;
import com.telegram.bot.helperclasses.ReplyKeyboards;
import com.telegram.bot.storage.FixedSizeList;
import com.telegram.bot.storage.Storage;
import com.telegram.convertations.conversions.SupportedFileExtensions;
import com.telegram.convertations.converters.Converter;
import com.telegram.utils.FileNameTools;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


public class ConvertScript implements Script {
    private static final String STOP_WORD = "Done";
    public static final int CAPACITY = 10;
    private boolean running = false;

    private final Converter converter;
    private final FileLoadingManager<String, String> loadingManager = FileLoadingManagerImpl.getInstance();
    private final Storage<Document> images = new FixedSizeList<>(CAPACITY);
    private final SenderChatService senderChatService;
    private final DownloaderChatService downloaderChatService;
    private final ChatStates chatStates;

    public ConvertScript(Converter converter, SenderChatService senderChatService,
                         DownloaderChatService downloaderChatService, ChatStates chatStates) {
        this.converter = converter;
        this.senderChatService = senderChatService;
        this.downloaderChatService = downloaderChatService;
        this.chatStates = chatStates;
    }

    @Override
    public void start() {
        if (!running) {
            running = true;
            senderChatService.sendTextReply("Upload your photos", ReplyKeyboards.getButtonWithText(STOP_WORD));
        }
    }

    @Override
    public void update(Update update) {
        if (running) {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                if (message.hasText()) {
                    if (STOP_WORD.equals(message.getText())) {
                        if (images.isEmpty()) {
                            senderChatService.sendTextReply("You have to upload at least one photo", ReplyKeyboards.removeKeyboard());
                        } else {
                            convertAndUploadImages();
                        }
                        stop();
                    }
                } else if (message.hasDocument()) {
                    Document document = message.getDocument();
                    if (hasRightExtensions(document)) {
                        if (!addDocumentWithFailMessage(document)) {
                            convertAndUploadImages();
                            stop();
                        }
                    } else {
                        senderChatService.sendTextReply("Wrong file extension");
                    }
                } else if (message.hasPhoto()) {
                    List<PhotoSize> photos = message.getPhoto();
                    Document document = photoSizeToDocument(photos.get(photos.size() - 1));
                    if (!addDocumentWithFailMessage(document)) {
                        convertAndUploadImages();
                        stop();
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        if (running) {
            running = false;
            chatStates.put(null);
        }
    }

    private boolean hasRightExtensions(Document document) {
        String extension = FileNameTools.extractExtension(document.getFileName());
        return SupportedFileExtensions.get().contains(extension.toLowerCase());
    }

    private void convertAndUploadImages() {
        String ids = combineIds();
        if (!(loadingManager.contains(ids) && senderChatService.sendDocumentReply(loadingManager.get(ids)) != null)) {
            Document uploadedDocument = sendDocument(convertImages(downloadImages()));
            if (uploadedDocument != null) {
                saveDocumentInLoadingManager(ids, uploadedDocument);
            }
        }
    }

    private String combineIds() {
        StringBuilder result = new StringBuilder();
        images.forEach(p -> result.append(p.getFileUniqueId()).append(";"));
        return result.toString();
    }

    private List<File> downloadImages() {
        List<File> result = new LinkedList<>();
        for (Document img : images) {
            result.add(downloaderChatService.downloadDocument(img));
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
        Document uploadedDocument = senderChatService.sendDocumentReply(file,
                "images" + filename.substring(filename.lastIndexOf('.')), ReplyKeyboards.removeKeyboard());
        file.delete();
        return uploadedDocument;
    }

    private void saveDocumentInLoadingManager(String id, Document document) {
        loadingManager.put(id, document.getFileId());
    }

    private boolean addDocumentWithFailMessage(Document document) {
        if (!images.add(document)) {
            int capacity = images.getCapacity();
            senderChatService.sendTextReply(String.format("You have already loaded maximum number of photos (%d)", capacity));
            return false;
        }
        return true;
    }

    protected Document photoSizeToDocument(PhotoSize photoSize) {
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
