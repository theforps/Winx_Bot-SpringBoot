package testBot.testBot.service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import testBot.testBot.config.BotConfig;
import testBot.testBot.scripts.Consts;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private int counter = 0, result = 0;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        Long chatId;
        Integer messageId;

        if(message != null) {
            chatId = message.getChatId();
            messageId = message.getMessageId();
        }
        else {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
        }

        deleteMessage(chatId, messageId);

        if (message != null && message.getText().equals("/start"))
        {
            firstMessage(chatId);
        }
        else if (update.hasCallbackQuery())
        {

            if(update.getCallbackQuery().getData().equals("Начать"))
            {
                counter = 0;
                result = 0;
                test(chatId);
            }
            else if(counter >= 9)
            {
                showResult(chatId);
            }
            else
            {
                result += Integer.parseInt(update.getCallbackQuery().getData());
                counter++;
                test(chatId);
            }
        }
    }

    private void showResult(Long chatId)
    {
        JSONObject obj = Consts.JSON.getJSONObject("result" + String.valueOf(result % 6));
        SendPhoto sendPhoto = new SendPhoto();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(obj.getString("image")));
        sendPhoto.setCaption(obj.getString("text"));

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Начать заново");
        inlineKeyboardButton1.setCallbackData("Начать");
        rowInline1.add(inlineKeyboardButton1);
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);

        sendPhoto.setReplyMarkup(markupInline);

        try {
            execute(sendPhoto);
        }
        catch (TelegramApiException ignored) {}
    }

    private void firstMessage(Long chatId) {

        JSONObject obj = Consts.JSON.getJSONObject("firstMessage");
        SendPhoto sendPhoto = new SendPhoto();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(obj.getString("image")));
        sendPhoto.setCaption(obj.getString("text"));

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Начать");
        inlineKeyboardButton1.setCallbackData("Начать");
        rowInline1.add(inlineKeyboardButton1);
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);

        sendPhoto.setReplyMarkup(markupInline);

        try {
            execute(sendPhoto);
        }
        catch (TelegramApiException ignored) {}
    }

    private void test(long chatId)
    {
        JSONObject obj = Consts.JSON.getJSONObject("task" + String.valueOf(counter));
        SendPhoto sendPhoto = new SendPhoto();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(obj.getString("image")));
        sendPhoto.setCaption(obj.getString("text"));

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();

        for(int i = 1; i <= 4; i++)
        {
            InlineKeyboardButton but = new InlineKeyboardButton(obj.getString(String.valueOf(i)));
            but.setCallbackData(String.valueOf(i));

            rowInline1.add(but);

            if(i % 2 == 0)
            {
                rowsInline.add(rowInline1);
                rowInline1 = new ArrayList<>();
            }
        }

        markupInline.setKeyboard(rowsInline);
        sendPhoto.setReplyMarkup(markupInline);

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }


    private void deleteMessage(long chatId, Integer messageId)
    {
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);

        try {
            execute(deleteMessage);
        }
        catch (TelegramApiException ignored) {}
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
}