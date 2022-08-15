package org.powertac.rachma.util.schema;

import java.util.function.Function;

public class Field<E> {

    private final String name;
    private final Function<E, String> formatter;

    public static <T> Field<T> create(String name, Function<T, String> formatter) {
        return new Field<>(name, formatter);
    }

    public Field(String name, Function<E, String> formatter) {
        this.name = name;
        this.formatter = formatter;
    }

    public String getName() {
        return name;
    }

    public String format(E object) {
        return formatter.apply(object);
    }

}
