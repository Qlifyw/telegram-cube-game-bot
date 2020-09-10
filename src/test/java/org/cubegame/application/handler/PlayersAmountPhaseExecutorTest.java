package org.cubegame.application.handler;

import org.cubegame.application.model.FailedResult;
import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProcessingStatus;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.identifier.UserId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.domain.model.message.speach.Comment;
import org.cubegame.domain.model.message.speach.Speech;
import org.cubegame.domain.model.message.speach.SpeechFactory;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.cubegame.infrastructure.repository.game.GameRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.telegram.telegrambots.meta.api.objects.Dice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayersAmountPhaseExecutorTest {


    final PhaseExecutor phaseExecutor = PhaseExecutorFactory.of(Phase.NUMBER_OF_PLAYERS);
    final GameRepository gameRepository = new GameRepositoryImpl();

    final ApplicationProperties properties = ApplicationProperties.load();

    private static final ChatId CHAT_ID = new ChatId(123L);
    private static final UserId USER_ID = new UserId(456L);
    private static final String FIRST_NAME = "First name";
    private static final Dice DICE = null;

    @Test
    @DisplayName("Skip message if bot not tagged")
    void not_tagged_bot() {
        final Speech comment = new Comment("Hi averyone!");
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);
        final PhaseStatebleResponse response = phaseExecutor.execute(message, gameRepository);

        assertEquals(ProcessingStatus.SKIPPED, response.getStatus());
    }

    @Test
    @DisplayName("Skip message if bot tagged without command")
    void tag_bot_without_command() {
        final Speech comment = SpeechFactory.of(String.format("@%s ", properties.getBotName()));
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);
        final PhaseStatebleResponse response = phaseExecutor.execute(message, gameRepository);

        assertEquals(ProcessingStatus.SKIPPED, response.getStatus());
    }

    @DisplayName("Error message if bot tagged with invalid number")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "1.2"})
    void tag_bot_with_invalid_command(String number) {
        final Speech comment = SpeechFactory.of(String.format("@%s %s", properties.getBotName(), number));
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);
        final PhaseStatebleResponse response = phaseExecutor.execute(message, gameRepository);

        assertEquals(ProcessingStatus.FAILED, response.getStatus());
        final ResponseMessage failedResponse = ((FailedResult) response).getResponseMessage();
        assertTrue(failedResponse.getMessage().contains("Invalid command"));
    }

    @Test
    @DisplayName("Success if bot tagged with valid number")
    void tag_bot_with_valid_command() {
        final Speech comment = SpeechFactory.of(String.format("@%s %d", properties.getBotName(), 3));
        final Message message = new Message(CHAT_ID, USER_ID, FIRST_NAME, comment, DICE);
        final PhaseStatebleResponse response = phaseExecutor.execute(message, gameRepository);

        assertEquals(ProcessingStatus.PROCEDURAL, response.getStatus());
    }


}