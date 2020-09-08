package org.cubegame.domain.events;

public class CommandValidator {

    public static ValidatedCommand validateOrThrow(UnvalidatedCommand unvalidatedCommand) {
        final Command command = Command.from(unvalidatedCommand.toString());
        return new ValidatedCommand(command);
    }

    public static class ValidatedCommand {
        final private Command value;

        private ValidatedCommand(final Command value) {
            this.value = value;
        }

        public Command getValue() {
            return value;
        }
    }



}
