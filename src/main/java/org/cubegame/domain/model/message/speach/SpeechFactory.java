package org.cubegame.domain.model.message.speach;

import org.cubegame.infrastructure.properties.ApplicationProperties;

public class SpeechFactory {

    private final ApplicationProperties applicationProperties;

    public SpeechFactory(final ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public static final Speech EMPTY_SPEECH = new Comment("");

    public Speech of(String text) {

        /* @BotName 123 */
        final String[] split = text.split(" ");

        if (split.length != CommandPart.values().length)
            return new Comment(text);

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
