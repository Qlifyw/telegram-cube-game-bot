package org.cubegame.domain.events;

import org.cubegame.infrastructure.properties.ApplicationProperties;

import java.util.Optional;

public class UnvalidatedCommand {

    private final String value;

    private UnvalidatedCommand(final String value) {
        this.value = value;
    }

    public static Optional<UnvalidatedCommand> from(String unvalidatedCommand) {
        if (!unvalidatedCommand.startsWith("/"))
            return Optional.empty();

        /* /start@BotName */
        final String[] split = unvalidatedCommand.split("@");

        if (split.length != CommandPart.values().length)
            return Optional.empty();

        // TODO delete .trim()
        final String command = split[CommandPart.COMMAND_PART.getOrder()].trim();
        final String botName = split[CommandPart.BOT_NAME_PART.getOrder()];

        final ApplicationProperties applicationProperties = ApplicationProperties.load();
        if (!botName.equals(applicationProperties.getBotName()))
            return Optional.empty();

        return Optional.of(new UnvalidatedCommand(command));
    }

    @Override
    public String toString() {
        return value;
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
