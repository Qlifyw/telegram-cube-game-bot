package org.cubegame.domain.events;

public final class CommandValidator {

    public static ValidatedCommand validateOrThrow(String unvalidatedCommand) {
        final Command command = Command.from(unvalidatedCommand);
        return new ValidatedCommand(command);
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

}
