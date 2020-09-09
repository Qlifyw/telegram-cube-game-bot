package org.cubegame.domain.events;

import org.cubegame.infrastructure.properties.ApplicationProperties;

import java.util.Optional;

public class Appeal {

    private final String value;

    private Appeal(final String value) {
        this.value = value;
    }

    public static Optional<Appeal> from(String text) {
        final ApplicationProperties applicationProperties = ApplicationProperties.load();

//        if (!text.startsWith("@"+applicationProperties.getBotName()))
//            return Optional.empty();

        /**
         * TODO handle cases:
         * '  @Bot 123'
         * '@Bot 123 qwe'
         */


        /* @BotName 123 */
        final String[] split = text.split(" ");

        if (split.length != CommandPart.values().length)
            return Optional.empty();

        // TODO delete .trim()
        final String message = split[CommandPart.COMMAND_PART.getOrder()].trim();
        final String botName = split[CommandPart.BOT_NAME_PART.getOrder()];

        if (!botName.equals("@"+applicationProperties.getBotName()))
            return Optional.empty();

        return Optional.of(new Appeal(message));
    }

    @Override
    public String toString() {
        return value;
    }

    private enum CommandPart {

        BOT_NAME_PART(0),
        COMMAND_PART(1);

        private final int order;

        CommandPart(final int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }


}
