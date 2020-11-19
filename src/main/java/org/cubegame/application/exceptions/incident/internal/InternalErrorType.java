package org.cubegame.application.exceptions.incident.internal;

public enum InternalErrorType {

    GAME_NOT_FOUND("1"),
    DISK_IO_ERROR("2"),
    JSON_MAPPING("3"),
    JSON_PARSING("4");

    private final String value;
    private static final String PREFIX = "IE";

    InternalErrorType(final String code) {
        this.value = PREFIX + code;
    }

    public String getValue() {
        return value;
    }

    public static void main(String[] args) {
        final ErrorType mapping1 = Fail.Network.Api.REST;
        final ErrorType mapping2 = Fail.Network.Io.REST;
        final ErrorType mapping3 = Fail.Network.IO;

        System.out.println(mapping1.equals(mapping2));
    }
}

interface Fail {
    enum Database implements ErrorType {
        MAPPING,
        PARSING
    }

    interface Network extends ErrorType {
        Network IO = Io.IO;

        enum Io implements Network {
            REST
        }

        enum Api implements Network {
            REST,
            RPC
        }
    }
}

interface ErrorType {}