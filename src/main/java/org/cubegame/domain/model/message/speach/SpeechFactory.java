package org.cubegame.domain.model.message.speach;

import org.cubegame.infrastructure.properties.ApplicationProperties;

public class SpeechFactory {

    private final ApplicationProperties applicationProperties;

    public SpeechFactory(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    // TODO SpeechFactory.EMPTY_SPEECH

    public Speech of(String text) {
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
            return new Comment(text);

        // TODO delete .trim()
        final String message = split[CommandPart.TEXT_PART.getOrder()].trim();
        final String botName = split[CommandPart.BOT_NAME_PART.getOrder()];

        if (!botName.equals("@" + applicationProperties.getBotName()))
            return new Comment(text);

        return new Appeal(message);
    }

    private enum CommandPart {

        BOT_NAME_PART(0),
        TEXT_PART(1);

        private final int order;

        CommandPart(final int order) {
            this.order = order;
        }

        public int getOrder() {
            return order;
        }
    }
}
