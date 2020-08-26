package org.cubegame.application.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.events.UnvalidatedCommand;
import org.cubegame.domain.exceptions.EnumException;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.Player;
import org.cubegame.infrastructure.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.TelegramBotView;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public void handle(Message receivedMessage, TelegramBotView view, ApplicationProperties properties) {

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
                final Optional<UnvalidatedCommand> maybeCommand = UnvalidatedCommand.from(message.getMessage());

                maybeCommand.ifPresent( unvalidatedCommand -> {

                    final CommandValidator.ValidatedCommand validatedCommand;
                    try {
                        validatedCommand = CommandValidator.validateOrThrow(unvalidatedCommand);
                    } catch (EnumException exception) {
                        // TODO log it
                        System.out.println(exception.toString());
                        view.respond(invalidCommand(message));
                        return;
                    }

                    switch (validatedCommand.getValue()) {
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
                });
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
                final int numberOfPlayers;
                try {
                    numberOfPlayers = Integer.parseInt(message.getMessage());
                } catch (NumberFormatException exception) {
                    view.respond(new ErrorResponseMessage(
                            "Invalid number of players. Please enter integer value.",
                            message.getChatId()
                    ));
                    break;
                }
                if (numberOfPlayers <= 0) {
                    view.respond(new ErrorResponseMessage(
                            "Invalid number of players. Number must be greater than 0.",
                            message.getChatId()
                    ));
                    break;
                }


                final Optional<Game> storedGame1 = gameRepository.get(message.getChatId());
                if (storedGame1.isPresent()) {

                    final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                    final Phase nextPhase = Phase.fromValue(nextphaseValue);

                    final Game game = storedGame1.get();
                    final Game updatedGame = GameBuilder.from(game)
                            .setPhase(nextPhase)
                            .setNumerOfPlayers(numberOfPlayers)
                            .build();
                    gameRepository.save(updatedGame);

                    view.respond(new TextResponseMessage(
                            String.format("Await for %d players", updatedGame.getNumerOfPlayers()),
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

                    final Game game = storedGame2.get();

                    final ArrayList<Player> currentPlayers = new ArrayList<>(game.getPlayers());
                    final List<Player> newPlayer = Collections.singletonList(new Player(message.getUserId()));
                    final List<Player> updatedPlayers = new ArrayList<>(CollectionUtils.union(currentPlayers, newPlayer));

                    final GameBuilder currentGameBuilder = GameBuilder.from(game)
                            .setPlayers(updatedPlayers);

                    if (updatedPlayers.size() == game.getNumerOfPlayers()) {
                        final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                        final Phase nextPhase = Phase.fromValue(nextphaseValue);
                        currentGameBuilder.setPhase(nextPhase);

                    } else {
                        view.respond(new TextResponseMessage(
                                String.format("Await for %d players", game.getNumerOfPlayers() - updatedPlayers.size()),
                                message.getChatId()
                        ));
                    }

                    gameRepository.save(currentGameBuilder.build());
                } else {
                    view.respond(new ErrorResponseMessage(
                            "Invalid cannot find game session for this chat",
                            message.getChatId()
                    ));
                }

                break;
            case STARTED:
                view.respond(new TextResponseMessage(
                        "Confradulation! Game is started",
                        message.getChatId()
                ));
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

    public String extractCommand(String message) {
        return message.split("@")[0];
    }

    public ResponseMessage invalidCommand(Message message) {
        return new ErrorResponseMessage(
                String.format("Invalid command: '%s'", message.getMessage()),
                message.getChatId()
        );
    }
}
