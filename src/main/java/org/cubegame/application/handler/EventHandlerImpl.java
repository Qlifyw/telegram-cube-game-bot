package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.IterableResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;

import java.util.ArrayList;
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
    public List<ResponseMessage> handle(Message receivedMessage, ApplicationProperties properties) {

        final List<ResponseMessage> responses = new ArrayList<>();

        final Optional<Game> game = gameRepository.get(receivedMessage.getChatId());

        final Phase phase = game
                .flatMap(storedGame -> Optional.of(storedGame.getPhase()))
                .orElse(Phase.EMPTY);

        final PhaseStatebleResponse processingResult = PhaseExecutorFactory
                .of(phase)
                .execute(receivedMessage, gameRepository);

        switch (processingResult.getStatus()) {
            case PROCESSED:
                final ResponseMessage responseMessage = ((ProcessedResult) processingResult).getResponseMessage();
                responses.add(responseMessage);

                PhaseExecutorFactory
                        .of(Phase.getNextFor(phase))
                        .initiation(receivedMessage.getChatId())
                        .ifPresent(responses::add);
                break;
            case FAILED:
                final ResponseMessage fail = ((FailedResult) processingResult).getResponseMessage();
                responses.add(fail);
                break;
            case PROCEDURAL:
                PhaseExecutorFactory
                        .of(Phase.getNextFor(phase))
                        .initiation(receivedMessage.getChatId())
                        .ifPresent(responses::add);
                break;
            case ITERABLE:
                final ResponseMessage iteration = ((IterableResult) processingResult).getResponseMessage();
                responses.add(iteration);
                break;
            case SKIPPED:
                break;
        }

        return responses;


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

    private boolean isBotMentioned(Message message) {
        return message.getSpeech()
                .getText()
                .trim()
                .startsWith("@" + properties.getBotName());
    }

}
