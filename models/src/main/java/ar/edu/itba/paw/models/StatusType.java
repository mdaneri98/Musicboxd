package ar.edu.itba.paw.models;

public enum StatusType {
    READ,
    UNREAD,
    ALL;

    public static StatusType fromString(String status) {
        return StatusType.valueOf(status.toUpperCase());
    }
}
