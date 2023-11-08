package testBot.testBot.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import testBot.testBot.config.BotConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig config;
    private final JSONObject json = new JSONObject(new String(Files.readAllBytes(Paths.get("src/main/resources/data.json")), StandardCharsets.UTF_8));

    public TelegramBot(BotConfig config) throws IOException {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();

        if(message.hasText())
        {
            if (message.getText().equals("/start"))
            {
                firstMessage(update);
            }
            else
            {
                deleteMessage(chatId, messageId);
            }
        }
    }

    private void firstMessage(Update update)  {

        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();

        String text = json.getJSONObject("firstMessage").getString("text");

        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        }
        catch (TelegramApiException ignored) {}
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
