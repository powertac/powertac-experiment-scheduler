package org.powertac.rachma.util.schema;

import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class Schema<E> {

    @Setter
    private String delimiter = "; ";

    private final List<Field<E>> fields;

    @SafeVarargs
    public static <T> Schema<T> create(Field<T>... fields) {
        return new Schema<>(List.of(fields));
    }

    public Schema(List<Field<E>> fields) {
        this.fields = fields;
    }

    public void add(Field<E> field) {
        fields.add(field);
    }

    public String header() {
        return formatCsv(fields.stream().map(Field::getName).collect(Collectors.toList()));
    }

    public String format(E object) {
        return formatCsv(fields.stream().map(col -> col.format(object)).collect(Collectors.toList()));
    }

    private String formatCsv(List<String> elements) {
        return String.join(delimiter, elements);
    }

}
