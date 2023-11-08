package testBot.testBot.service;


import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import testBot.testBot.config.BotConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();

        if(message != null && message.hasText())
        {
            if (message.getText().equals("/start")) {
                startCommand(update);
            } else {
                editMessage(update);
                deleteMessage(chatId, messageId);
            }
        }
    }

    private void startCommand(Update update)
    {
        sendMessage(update);
    }

    private void sendMessage(Update update) {

        Long chatId = update.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("Привет!");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton but = new InlineKeyboardButton();
        but.setText(LocalDateTime.now().toString());
        but.setCallbackData("test");
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        }
        catch (TelegramApiException ignored) {}
    }

    private void editMessage(Update update)
    {
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId()-1;

        EditMessageText editMessageText = new EditMessageText();
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton but = new InlineKeyboardButton();
        but.setText(LocalDateTime.now().toString());
        but.setCallbackData("test");

        row.add(but);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);

        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText(LocalDateTime.now().toString());
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(editMessageText);
            execute(editMessageReplyMarkup);
        }
        catch (TelegramApiException ignored)
        {

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
