package org.cubegame.domain.exceptions;

import java.util.Arrays;
import java.util.List;

public class EnumException extends RuntimeException {
    public final String className;
    public final String actualValue;
    public final List<String> allowedValues;


    public EnumException(String className, String actualValue, List<String> allowedValues) {
        this.className = className;
        this.actualValue = actualValue;
        this.allowedValues = allowedValues;
    }

    @Override
    public String toString() {
        return "Cannot create instance of " + className + ". Actual value '" + actualValue + "'. Allowed values: " + Arrays.toString(allowedValues.toArray());
    }
}
