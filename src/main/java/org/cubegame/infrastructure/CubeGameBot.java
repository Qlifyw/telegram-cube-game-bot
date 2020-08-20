package org.cubegame.infrastructure;

import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
import org.cubegame.domain.model.ChatId;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CubeGameBot
        extends TelegramLongPollingBot
        implements TelegramBotView {

    private final ApplicationProperties properties = ApplicationProperties.load();

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, properties);

    @Override
    public void onUpdateReceived(Update update) {

//
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            final String receivedMessage = update.getMessage().getText();
//
//            System.out.println(update.getMessage().getChatId());
//        }
//
//        if (update.hasCallbackQuery()) {
//            CallbackQuery callbackQuery = update.getCallbackQuery();
//            String data = callbackQuery.getData();
//
//            System.out.println(callbackQuery.getFrom().getId());
//
//        }

        eventHandler.handle(update, this, properties);

    }

    @Override
    public String getBotUsername() {
        return properties.getBotName();
    }

    @Override
    public String getBotToken() {
        return properties.getBotToken();
    }

    @Override
    public void showMenu(InlineKeyboardMarkup menu, ChatId chatId) {
        SendMessage message = new SendMessage()
                .setChatId(chatId.getValue())
                .setText("Choose the game")
                .setReplyMarkup(menu);
        respond(message);
    }

    @Override
    public void respond(final ResponseMessage message) {
        respond(new SendMessage(message.getChatId().getValue(), message.getValue()));
    }

    private void respond(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
