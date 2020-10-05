package org.cubegame.application.handler;

import org.cubegame.application.model.PhaseStatebleResponse;
import org.cubegame.application.model.ProcessedResult;
import org.cubegame.application.model.SkipedResult;
import org.cubegame.domain.events.Phase;
import org.cubegame.domain.exceptions.GameNoFoundException;
import org.cubegame.domain.model.game.Game;
import org.cubegame.domain.model.game.GameBuilder;
import org.cubegame.domain.model.identifier.ChatId;
import org.cubegame.domain.model.message.Message;
import org.cubegame.infrastructure.model.message.NavigationResponseMessage;
import org.cubegame.infrastructure.model.message.ResponseMessage;
import org.cubegame.infrastructure.model.message.TextResponseMessage;
import org.cubegame.infrastructure.repository.game.GameRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ChooseGamePhaseExecutor implements PhaseExecutor {

    private final ChatId chatId;
    private final GameRepository gameRepository;

    public ChooseGamePhaseExecutor(final ChatId chatId, final GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.chatId = chatId;
    }

    @Override
    public Optional<ResponseMessage> initiation() {
        final NavigationResponseMessage initMessage = new NavigationResponseMessage(buildMenu(), chatId);
        return Optional.of(initMessage);
    }

    @Override
    public PhaseStatebleResponse execute(Message message) {
        final Game storedGame = gameRepository
                .get(message.getChatId())
                .orElseThrow(() -> new GameNoFoundException(message.getChatId()));

        if (!message.getAuthor().getUserId().equals(storedGame.getOwner()))
            return new SkipedResult();

        final Phase nextPhase = Phase.getNextFor(getPhase());

        final Game updatedGame = GameBuilder.from(storedGame)
                .setPhase(nextPhase)
                .setGameName(message.getSpeech().getText())
                .build();
        gameRepository.save(updatedGame);

        return new ProcessedResult(
                new TextResponseMessage(
                        String.format("%s is selected.", message.getSpeech().getText()),
                        message.getChatId()
                )
        );
    }

    @Override
    public Phase getPhase() {
        return Phase.CHOOSE_GAME;
    }

    private InlineKeyboardMarkup buildMenu() {
        final InlineKeyboardButton cubeGameButton = new InlineKeyboardButton("cube");
        cubeGameButton.setCallbackData("cube-game");
        final InlineKeyboardButton dartsGameButton = new InlineKeyboardButton("darts");
        dartsGameButton.setUrl("https://google.com");

        final List<InlineKeyboardButton> buttonsRaw = Arrays.asList(cubeGameButton, dartsGameButton);
        return new InlineKeyboardMarkup(Collections.singletonList(buttonsRaw));
    }

}
