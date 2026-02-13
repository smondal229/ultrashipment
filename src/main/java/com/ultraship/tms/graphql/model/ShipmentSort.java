package com.ultraship.tms.graphql.model;

public class ShipmentSort {
    private final Field field;
    private final Direction direction;

    public enum Field {
        RATE("rate"),
        ID("id");

        private final String dbField;

        Field(String dbField) {
            this.dbField = dbField;
        }

        public String dbField() {
            return dbField;
        }
    }

    public enum Direction {
        ASC,
        DESC
    }

    public ShipmentSort(Field field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public Field field() {
        return field;
    }

    public Direction direction() {
        return direction;
    }
}
