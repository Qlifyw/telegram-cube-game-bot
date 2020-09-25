package org.cubegame.infrastructure.bot;

import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.handler.EventHandlerImpl;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.NavigationResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextualResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.cubegame.infrastructure.repository.round.RoundRepository;
import org.cubegame.infrastructure.repository.round.RoundRepositoryImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class CubeGameBot extends TelegramLongPollingBot {

    private final ApplicationProperties properties = ApplicationProperties.load();

    private final SpeechFactory speechFactory = new SpeechFactory(properties);

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final RoundRepository roundRepository = new RoundRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, properties);

    @Override
    public void onUpdateReceived(Update update) {

        final Message receivedMessage = getMessage(update);
        final List<ResponseMessage> responseMessages = eventHandler.handle(receivedMessage);

        for (ResponseMessage responseMessage : responseMessages) {
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

    }

    public Message getMessage(Update update) {

        if (update.hasMessage()) {
            final String receivedText = update.getMessage().hasText() ? update.getMessage().getText() : "";
            final ChatId chatId = new ChatId(update.getMessage().getChatId());
            final UserId userId = new UserId(update.getMessage().getFrom().getId());
            final String firstName = update.getMessage().getFrom().getFirstName();
            final Dice dice = update.getMessage().hasDice() ? new Dice(update.getMessage().getDice().getValue()) : null;

            final Speech speech = speechFactory.of(receivedText);
            return new Message(chatId, userId, firstName, speech, dice);
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            final String receivedText = callbackQuery.getData();
            final ChatId chatId = new ChatId(callbackQuery.getMessage().getChat().getId());
            final UserId userId = new UserId(callbackQuery.getFrom().getId());
            final String firstName = callbackQuery.getMessage().getFrom().getFirstName();

            final Speech speech = speechFactory.of(receivedText);
            return new Message(chatId, userId, firstName, speech, null);
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
