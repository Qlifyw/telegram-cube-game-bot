package org.cubegame.application.handler;

import org.cubegame.application.handler.stepper.CascadePhaseStepper;
import org.cubegame.application.model.Reply;
import org.cubegame.domain.events.Command;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.dice.Dice;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseType;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChooseGamePhaseExecutorIT {

    private static final ApplicationProperties applicationProperties = ApplicationProperties.load();
    private static final String BOT_NAME = applicationProperties.getBotName();

    private final GameRepository gameRepository = new GameRepositoryImpl();
    private final EventHandler eventHandler = new EventHandlerImpl(gameRepository, applicationProperties);

    private final PhaseExecutorFactory phaseExecutorFactory = new PhaseExecutorFactory(gameRepository);

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    private static final String GAME_NAME = "cube-game";

    @Test
    @DisplayName("Success when choose game")
    void suceessWhenChooseGame() {
        final Speech comment = SpeechFactory.of(GAME_NAME);
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);

        CascadePhaseStepper.moveUp(eventHandler, message, commands);

        final List<ResponseMessage> responses = eventHandler.handle(message);

        final PhaseExecutor phaseExecutor = phaseExecutorFactory.newInstance(
                Phase.getNextFor(Phase.CHOOSE_GAME),
                message.getChatId()
        );

        assertFalse(responses.isEmpty());
        assertEquals(2, responses.size());

        final ResponseMessage chooseGameResponse = responses.get(0);
        assertEquals(message.getChatId(), chooseGameResponse.getChatId());
        assertEquals(ResponseType.TEXT, chooseGameResponse.getType());
        assertTrue(chooseGameResponse.getMessage().contains(message.getSpeech().getText()));

        final ResponseMessage nextPhaseIntro = responses.get(1);
        assertEquals(message.getChatId(), nextPhaseIntro.getChatId());
        assertEquals(ResponseType.TEXT, nextPhaseIntro.getType());

        phaseExecutor.initiation(message.getChatId())
                .ifPresent(responseMessage ->
                        assertEquals(responseMessage.getMessage(), nextPhaseIntro.getMessage())
                );
    }

    @Test
    @DisplayName("Skip if user is not owner")
    void skipMessageIfNotTagged() {
        final UserId anotherUserId = new UserId(ThreadLocalRandom.current().nextLong());

        final Message msgTemplate = new Message(CHAT_ID, USER_ID, FIRST_NAME, SpeechFactory.of(GAME_NAME), DICE);

        final Speech speech = SpeechFactory.of(String.format("@%s %s", applicationProperties.getBotName(), GAME_NAME));
        final Message message = new Message(CHAT_ID, anotherUserId, FIRST_NAME, speech, DICE);

        CascadePhaseStepper.moveUp(eventHandler, msgTemplate, commands);

        final List<ResponseMessage> responses = eventHandler.handle(message);

        assertTrue(responses.isEmpty());
    }

    final List<Reply> commands = Collections.singletonList(
            new Reply(USER_ID, SpeechFactory.of(String.format("%s@%s", Command.START.getValue(), BOT_NAME)))
    );

}