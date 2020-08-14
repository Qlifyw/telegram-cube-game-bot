package org.cubegame.application.handler;

import org.cubegame.domain.events.Command;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.ChatId;
import org.cubegame.domain.model.Message;
import org.cubegame.domain.model.UserId;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.infrastructure.ApplicationProperties;
import org.cubegame.infrastructure.GameRepository;
import org.cubegame.infrastructure.TelegramBotView;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EventHandlerImpl implements EventHandler {

    private final GameRepository gameRepository;
    private final ApplicationProperties properties;

    public EventHandlerImpl(GameRepository gameRepository, ApplicationProperties properties) {
        this.gameRepository = gameRepository;
        this.properties = properties;
    }

    @Override
    public void handle(Update update, TelegramBotView view, ApplicationProperties properties) {

        final Message receivedMessage = getMessage(update);
        final Optional<Game> game = gameRepository.get(receivedMessage.getChatId());
        if (game.isPresent()) {
            final Game storedGame = game.get();
            final Phase phase = storedGame.getPhase();
            processPhase(phase, view, receivedMessage);
        } else {
            processPhase(Phase.EMPTY, view, receivedMessage);
        }



//        if (update.hasMessage() && update.getMessage().hasDice()) {
//            final Dice dice = update.getMessage().getDice();
//            String firstName = update.getMessage().getFrom().getFirstName();
//            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
//                    .setChatId(update.getMessage().getChatId())
//                    .setText("User " + firstName + " has " + dice.getValue() + " point");
//            try {
//                execute(message); // Call method to send the message
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }


        // cube bot
//        if (update.hasMessage() && update.getMessage().hasDice()) {
//            final Dice dice = update.getMessage().getDice();
//            String firstName = update.getMessage().getFrom().getFirstName();
//            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
//                    .setChatId(update.getMessage().getChatId())
//                    .setText("User " + firstName + " has " + dice.getValue() + " point");
//            try {
//                execute(message); // Call method to send the message
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }


        /*
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add("/start");
        keyboardButtons.add("/stop");
        replyKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardButtons));
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
         */
    }

    public void processPhase(Phase phase, TelegramBotView view, Message message) {
        switch (phase) {
            case EMPTY:
                if (isCommand(message.getMessage())) {
                    Command command = Command.fromValue(message.getMessage());
                    switch (command) {
                        case START:
                            final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                            final Phase nextPhase = Phase.fromValue(nextphaseValue);

                            final ChatId chatId = message.getChatId();
                            final Game createdGame = new GameBuilder()
                                    .setChatId(chatId)
                                    .setPhase(nextPhase)
                                    .build();

                            gameRepository.save(createdGame);

                            final InlineKeyboardMarkup menu = buildMenu();
                            view.showMenu(menu, message.getChatId());

                            break;
                        case STOP:
                            break;
                    }
                } else {
                    final ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(
                            "Invalid command: '" + message.getMessage() + "'",
                            message.getChatId()
                    );
                    view.respond(errorResponseMessage);
                }
                break;
            case CHOOSE_GAME:
                final Optional<Game> storedGame = gameRepository.get(message.getChatId());
                if (storedGame.isPresent()) {

                    final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                    final Phase nextPhase = Phase.fromValue(nextphaseValue);

                    final Game game = storedGame.get();
                    final Game updatedGame = GameBuilder.from(game)
                            .setPhase(nextPhase)
                            .setGameName(message.getMessage())
                            .build();
                    gameRepository.save(updatedGame);

                    view.respond(new TextResponseMessage(
                            "Specify players amount",
                            message.getChatId()
                    ));
                } else {
                    view.respond(new ErrorResponseMessage(
                            "Invalid cannot find game session for this chat",
                            message.getChatId()
                    ));
                }

                break;
            case NUMBER_OF_PLAYERS:
                final Optional<Game> storedGame1 = gameRepository.get(message.getChatId());
                if (storedGame1.isPresent()) {

                    final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                    final Phase nextPhase = Phase.fromValue(nextphaseValue);

                    final Game game = storedGame1.get();
                    final Game updatedGame = GameBuilder.from(game)
                            .setPhase(nextPhase)
                            .setNumerOfPlayers(Integer.parseInt(message.getMessage()))
                            .build();
                    gameRepository.save(updatedGame);

                    view.respond(new TextResponseMessage(
                            "Specify players amount",
                            message.getChatId()
                    ));
                } else {
                    view.respond(new ErrorResponseMessage(
                            "Invalid cannot find game session for this chat",
                            message.getChatId()
                    ));
                }

                break;
            case AWAIT_PLAYERS:
                final Optional<Game> storedGame2 = gameRepository.get(message.getChatId());
                if (storedGame2.isPresent()) {

                    final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                    final Phase nextPhase = Phase.fromValue(nextphaseValue);

                    final Game game = storedGame2.get();
                    final Game updatedGame = GameBuilder.from(game)
                            .setPhase(nextPhase)
                            .setNumerOfPlayers(Integer.parseInt(message.getMessage()))
                            .build();
                    gameRepository.save(updatedGame);

                    view.respond(new TextResponseMessage(
                            "Specify players amount",
                            message.getChatId()
                    ));
                } else {
                    view.respond(new ErrorResponseMessage(
                            "Invalid cannot find game session for this chat",
                            message.getChatId()
                    ));
                }

                break;
        }
    }


    public InlineKeyboardMarkup buildMenu() {
        final InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton("cube");
        inlineKeyboardButton1.setCallbackData("cube-game");
        final InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton("darts");
        inlineKeyboardButton2.setUrl("https://google.com");

        final List<InlineKeyboardButton> buttons = Arrays.asList(inlineKeyboardButton1, inlineKeyboardButton2);
        InlineKeyboardMarkup menu = new InlineKeyboardMarkup(Arrays.asList(buttons));
        return menu;
    }


    public boolean isCommand(String message) {
        return message.startsWith("/");
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
}
