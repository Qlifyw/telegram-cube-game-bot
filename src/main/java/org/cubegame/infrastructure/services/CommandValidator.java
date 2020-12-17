package org.cubegame.infrastructure.services;

import org.cubegame.domain.events.Command;
import org.cubegame.infrastructure.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class CommandValidator {
    private static final Logger LOG = LoggerFactory.getLogger(CommandValidator.class);

    private final ApplicationProperties properties;

    public CommandValidator(final ApplicationProperties properties) {
        this.properties = properties;
    }

    public Optional<ValidatedCommand> validate(String unvalidatedCommand) {
        final String[] parts = unvalidatedCommand.split("@");

        if (parts.length != CommandPart.values().length)
            return Optional.empty();

        final String commandPart = parts[CommandPart.COMMAND_PART.getOrder()];
        final String botName = parts[CommandPart.BOT_NAME_PART.getOrder()];

        if (!botName.equals(properties.getBotName()))
            return Optional.empty();


        final Optional<ValidatedCommand> validateCommand = Command.from(commandPart)
                .map(ValidatedCommand::new);

        if (!validateCommand.isPresent()) {
            LOG.error("Cannot find command '{}'", commandPart);
            return Optional.empty();
        }

        return validateCommand;
    }

    public static final class ValidatedCommand {
        private final Command value;

        private ValidatedCommand(final Command value) {
            this.value = value;
        }

        public Command getValue() {
            return value;
        }
    }

    private enum CommandPart {

        COMMAND_PART(0),
        BOT_NAME_PART(1);

        private final int order;

        CommandPart(final int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }

}
