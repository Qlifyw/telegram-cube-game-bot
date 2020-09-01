package org.cubegame.infrastructure.bot;

import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.model.message.NavigationResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextualResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CubeGameBot extends TelegramLongPollingBot {

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


        final Message receivedMessage = getMessage(update);
        final ResponseMessage responseMessage = eventHandler.handle(receivedMessage, properties);

        switch (responseMessage.getType()) {
            case NAVIAGTION: {
                final NavigationResponseMessage response = (NavigationResponseMessage) responseMessage;
                showMenu(response.getMenu(), response.getChatId());
                break;
            }
            case TEXT: {
                final TextualResponseMessage response = (TextualResponseMessage) responseMessage;
                respond(response);
                break;
            }
        }

    }

    public Message getMessage(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            final String receivedText = update.getMessage().getText();
            final ChatId chatId = new ChatId(update.getMessage().getChatId());
            final UserId userId = new UserId(update.getMessage().getFrom().getId());

            return new Message(chatId, userId, receivedText);
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            final String receivedText = callbackQuery.getData();
            final ChatId chatId = new ChatId(callbackQuery.getFrom().getId().longValue());
            final UserId userId = new UserId(callbackQuery.getMessage().getFrom().getId());

            return new Message(chatId, userId, receivedText);
        }

        return null;
    }

    @Override
    public String getBotUsername() {
        return properties.getBotName();
    }

    @Override
    public String getBotToken() {
        return properties.getBotToken();
    }

    public void showMenu(InlineKeyboardMarkup menu, ChatId chatId) {
        SendMessage message = new SendMessage()
                .setChatId(chatId.getValue())
                .setText("Choose the game")
                .setReplyMarkup(menu);
        respondToClient(message);
    }

    public <T extends TextualResponseMessage> void respond(final T message) {
        final SendMessage responseMessage = new SendMessage()
                .setChatId(message.getChatId().getValue())
                .setText(message.getMessage());
        respondToClient(responseMessage);
    }

    private void respondToClient(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
