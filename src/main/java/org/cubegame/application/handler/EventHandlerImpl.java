package org.cubegame.application.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.cubegame.domain.events.CommandValidator;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.events.UnvalidatedCommand;
import org.cubegame.domain.exceptions.EnumException;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.game.Player;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.ApplicationProperties;
import org.cubegame.infrastructure.TelegramBotView;
import org.cubegame.infrastructure.model.message.ErrorResponseMessage;
import org.cubegame.infrastructure.model.message.NavigationResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;
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
    public ResponseMessage handle(Message receivedMessage, ApplicationProperties properties) {

        final Optional<Game> game = gameRepository.get(receivedMessage.getChatId());
        if (game.isPresent()) {
            final Game storedGame = game.get();
            final Phase phase = storedGame.getPhase();
            return processPhase(phase, receivedMessage);
        } else {
            return processPhase(Phase.EMPTY, receivedMessage);
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

    public ResponseMessage processPhase(Phase phase, Message message) {

        ResponseMessage responseMessage = null;
        switch (phase) {
            case EMPTY: {
                final Optional<UnvalidatedCommand> maybeCommand = UnvalidatedCommand.from(message.getMessage());

                if (maybeCommand.isPresent()) {

                    final UnvalidatedCommand unvalidatedCommand = maybeCommand.get();

                    final CommandValidator.ValidatedCommand validatedCommand;
                    try {
                        validatedCommand = CommandValidator.validateOrThrow(unvalidatedCommand);
                    } catch (EnumException exception) {
                        // TODO log it
                        System.out.println(exception.toString());
                        responseMessage = invalidCommand(message);
                        break;
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

                            responseMessage = new NavigationResponseMessage(buildMenu(), message.getChatId());
                            break;
                        case STOP:
                            break;
                    }
                };
                break;
            }
            case CHOOSE_GAME: {
                final Game storedGame = gameRepository
                        .get(message.getChatId())
                        .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

                final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                final Phase nextPhase = Phase.fromValue(nextphaseValue);

                final Game updatedGame = GameBuilder.from(storedGame)
                        .setPhase(nextPhase)
                        .setGameName(message.getMessage())
                        .build();
                gameRepository.save(updatedGame);

                responseMessage = new TextResponseMessage("Specify players amount", message.getChatId());
                break;
            }
            case NUMBER_OF_PLAYERS: {
                final int numberOfPlayers;
                try {
                    numberOfPlayers = Integer.parseInt(message.getMessage());
                } catch (NumberFormatException exception) {
                    responseMessage = new ErrorResponseMessage(
                            "Invalid number of players. Please enter integer value.",
                            message.getChatId()
                    );
                    break;
                }
                if (numberOfPlayers <= 0) {
                    responseMessage = new ErrorResponseMessage(
                            "Invalid number of players. Number must be greater than 0.",
                            message.getChatId()
                    );
                    break;
                }

                final Game storedGame = gameRepository
                        .get(message.getChatId())
                        .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

                final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                final Phase nextPhase = Phase.fromValue(nextphaseValue);

                final Game updatedGame = GameBuilder.from(storedGame)
                        .setPhase(nextPhase)
                        .setNumerOfPlayers(numberOfPlayers)
                        .build();
                gameRepository.save(updatedGame);

                responseMessage = new TextResponseMessage(
                        String.format("Await for %d players", updatedGame.getNumerOfPlayers()),
                        message.getChatId()
                );

                break;
            }
            case AWAIT_PLAYERS: {
                final Game storedGame = gameRepository
                        .get(message.getChatId())
                        .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

                final ArrayList<Player> currentPlayers = new ArrayList<>(storedGame.getPlayers());
                final List<Player> newPlayer = Collections.singletonList(new Player(message.getUserId()));
                final List<Player> updatedPlayers = new ArrayList<>(CollectionUtils.union(currentPlayers, newPlayer));

                final GameBuilder currentGameBuilder = GameBuilder.from(storedGame)
                        .setPlayers(updatedPlayers);

                if (updatedPlayers.size() == storedGame.getNumerOfPlayers()) {
                    final String nextphaseValue = properties.getNextStateFor(phase.getValue());
                    final Phase nextPhase = Phase.fromValue(nextphaseValue);
                    currentGameBuilder.setPhase(nextPhase);

                } else {
                    responseMessage = new TextResponseMessage(
                            String.format("Await for %d players", storedGame.getNumerOfPlayers() - updatedPlayers.size()),
                            message.getChatId()
                    );
                }

                gameRepository.save(currentGameBuilder.build());


                break;
            }
            case STARTED: {
                responseMessage = new TextResponseMessage(
                        "Confradulation! Game is started",
                        message.getChatId()
                );
                break;
            }
        }
        return responseMessage;
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

    public ResponseMessage invalidCommand(Message message) {
        return new ErrorResponseMessage(
                String.format("Invalid command: '%s'", message.getMessage()),
                message.getChatId()
        );
    }
}
