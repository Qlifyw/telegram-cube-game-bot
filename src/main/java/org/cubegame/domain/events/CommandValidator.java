package org.cubegame.domain.events;

import org.cubegame.domain.exceptions.EnumException;
import org.cubegame.infrastructure.properties.ApplicationProperties;

import java.util.Optional;

public final class CommandValidator {

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

        final Command command;
        try {
            command = Command.from(commandPart);
            ;
        } catch (EnumException exception) {
            // TODO log it
            System.out.println(exception.toString());
            return Optional.empty();
        }
        return Optional.of(new ValidatedCommand(command));
    }

    public static final class ValidatedCommand {
        final private Command value;

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
