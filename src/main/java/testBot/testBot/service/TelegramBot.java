package testBot.testBot.service;

import jakarta.inject.Scope;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private Map<Long, List<Integer>> map = new HashMap<>();
    private JSONObject JSON;

    public TelegramBot(BotConfig config) throws IOException {
        this.config = config;
        JSON = new JSONObject(new String(Files.readAllBytes(Paths.get(config.getDataPath())), StandardCharsets.UTF_8));
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        Long chatId;
        List<Integer> numbers;
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
                map.remove(chatId);

                numbers = new ArrayList<>();
                numbers.add(0);
                numbers.add(0);
                map.put(chatId, numbers);

                test(chatId);
            }
            else if(map.get(chatId).get(0) >= 9)
            {

                showResult(chatId);
            }
            else
            {

                var getData = map.get(chatId);

                int result = getData.get(1);
                int counter = getData.get(0);

                result += Integer.parseInt(update.getCallbackQuery().getData());
                counter++;

                List<Integer> temp = new ArrayList<>();
                temp.add(counter);
                temp.add(result);

                map.put(chatId, temp);

                test(chatId);
            }
        }
    }

    private void showResult(Long chatId)
    {
        var getData = map.get(chatId).get(1);

        JSONObject obj = JSON.getJSONObject("result" + String.valueOf(getData % 6));
        SendPhoto sendPhoto = createMessage(obj, chatId);
        InlineKeyboardMarkup markupInline = startBut();

        sendPhoto.setReplyMarkup(markupInline);

        try {
            execute(sendPhoto);
        }
        catch (TelegramApiException ignored) {}
    }

    private void firstMessage(Long chatId) {

        JSONObject obj = JSON.getJSONObject("firstMessage");
        SendPhoto sendPhoto = createMessage(obj, chatId);
        InlineKeyboardMarkup markupInline = startBut();

        sendPhoto.setReplyMarkup(markupInline);

        try {
            execute(sendPhoto);
        }
        catch (TelegramApiException ignored) {}
    }

    private void test(Long chatId)
    {
        var getData = map.get(chatId);

        JSONObject obj = JSON.getJSONObject("task" + String.valueOf(getData.get(0)));
        SendPhoto sendPhoto = createMessage(obj, chatId);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
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

    private InlineKeyboardMarkup startBut()
    {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("Начать");
        inlineKeyboardButton1.setCallbackData("Начать");
        rowInline1.add(inlineKeyboardButton1);
        rowsInline.add(rowInline1);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private SendPhoto createMessage(JSONObject obj, Long chatId)
    {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(obj.getString("image")));
        sendPhoto.setCaption(obj.getString("text"));

        return sendPhoto;
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