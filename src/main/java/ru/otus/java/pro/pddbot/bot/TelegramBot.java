package ru.otus.java.pro.pddbot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.otus.java.pro.pddbot.bot.handlers.callback.CallBackHandler;
import ru.otus.java.pro.pddbot.bot.handlers.callback.EditMessageWrapper;
import ru.otus.java.pro.pddbot.bot.handlers.message.MessageHandler;
import ru.otus.java.pro.pddbot.configuration.BotConfiguration;
import ru.otus.java.pro.pddbot.model.ExamSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private static final Pattern CALLBACK_PATTERN = Pattern.compile(
            "(?<command>[a-z]*)?_?(?<questionCode>[0-9]*)?_?(?<answerCode>[0-9]*)?");

    private final Map<String, ExamSession> activeSessions;

    private final BotConfiguration configuration;

    private final Map<String, MessageHandler> messageHandlers;

    private final Map<String, CallBackHandler> callBackHandlers;

    @Autowired
    public TelegramBot(BotConfiguration configuration,
                       Map<String, MessageHandler> messageHandlers,
                       Map<String, CallBackHandler> callBackHandlers) {
        super(configuration.getToken());
        this.activeSessions = new ConcurrentHashMap<>();
        this.configuration = configuration;
        this.messageHandlers = messageHandlers;
        this.callBackHandlers = callBackHandlers;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handleCallback(update.getCallbackQuery());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки обновления", e);
        }
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();
        Long telegramId = message.getFrom().getId();
        String username = message.getFrom().getUserName();
        String firstName = message.getFrom().getFirstName();

        log.debug("Сообщение от {} ({}): {}", telegramId, username, text);

        if (text.startsWith("/")) {
            handleCommand(chatId, telegramId, text, username, firstName);
        } else {
            handleText(chatId, telegramId, text);
        }
    }

    private void handleCallback(CallbackQuery callback) {
        Long chatId = callback.getMessage().getChatId();
        Long telegramId = callback.getFrom().getId();
        String userName = callback.getFrom().getUserName();
        String firstName = callback.getFrom().getFirstName();
        String callbackData = callback.getData();
        Integer messageId = callback.getMessage().getMessageId();

        log.debug("Callback от {}: {}", telegramId, callbackData);
        Matcher matcher = CALLBACK_PATTERN.matcher(callbackData);

        if (!matcher.matches()) {
            SendMessage unhandledCallbackMessage = new SendMessage();
            unhandledCallbackMessage.setText("Ой... кажется мы не придумали зачем нам эта кнопка :(");
            unhandledCallbackMessage.setChatId(chatId);
            sendMessage(unhandledCallbackMessage);
        } else {
            CallBackHandler handler = callBackHandlers.get(matcher.group("command"));
            EditMessageWrapper editMessageText = handler.acceptAnswer(chatId, telegramId, callbackData, messageId);

            editMessage(editMessageText.getEditMessageText());
            if (!editMessageText.isFinalMessage()) {
                SendMessage newQuestion = handler.sendQuestion(chatId, telegramId, userName, firstName);
                sendMessage(newQuestion);
            }

        }

    }

    private void handleCommand(Long chatId, Long telegramId, String command,
                               String username, String firstName) {

        MessageHandler handler = messageHandlers.get(command);
        if (handler == null) {
            SendMessage unhandledMessage = new SendMessage();
            unhandledMessage.setText("Неизвестная команда. Используйте /help для списка команд.");
            unhandledMessage.setChatId(chatId);
            sendMessage(unhandledMessage);
        } else {
            SendMessage message = handler.handle(chatId, telegramId, username, firstName);
            sendMessage(message);
        }
    }


    private void handleText(Long chatId, Long telegramId, String text) {

        SendMessage message = new SendMessage();
        String msgText;
        if (text.equalsIgnoreCase("привет") || text.equalsIgnoreCase("hi")) {
            msgText = "Привет! Используйте /start для начала работы.";
        } else {
            msgText = "Я не понял ваш запрос. Используйте /help для списка команд.";
        }
        message.setChatId(chatId);
        message.setText(msgText);
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в чат {}", message.getChatId(), e);
        }
    }

    private void editMessage(EditMessageText editMessage) {
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения в чат {}", editMessage.getChatId(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return configuration.getUsername();
    }
}