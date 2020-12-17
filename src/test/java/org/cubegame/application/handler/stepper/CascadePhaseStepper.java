package org.cubegame.application.handler.stepper;

import org.cubegame.application.handler.EventHandler;
import org.cubegame.application.model.Reply;
import org.cubegame.domain.model.message.Message;

import java.util.List;

public class CascadePhaseStepper {

    private CascadePhaseStepper() {
    }

    public static void moveUp(EventHandler eventHandler, Message message, List<Reply> speeches) {
        speeches.forEach(reply -> {
            Message newMessage = new Message(
                    message.getChatId(),
                    reply.getUserId(),
                    message.getAuthor().getFirstName() + reply.getUserId(),
                    reply.getSpeech(),
                    message.getDice(),
                    message.getForwardedMessageId()
            );
            eventHandler.handle(newMessage);
        });
    }

}
