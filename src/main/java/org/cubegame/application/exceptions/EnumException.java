package org.cubegame.application.exceptions;

import java.util.List;

public final class EnumException extends RuntimeException {
    public final String className;
    public final String actualValue;
    private final List<String> allowedValues;


    public EnumException(String className, String actualValue, List<String> allowedValues) {
        this.className = className;
        this.actualValue = actualValue;
        this.allowedValues = allowedValues;
    }

    @Override
    public String toString() {
        final String allowedValuesStringBuilder = "[" + String.join(",", allowedValues) + "]";
        return "Cannot create instance of " + className + ". Actual value '" + actualValue + "'. Allowed values: " + allowedValuesStringBuilder;
    }
}
