package com.omb.ocpp.server.iso15118.dto;

public enum MessageType {
    CALL(2),
    CALL_RESULT(3),
    CALL_ERROR(4);

    private final Integer id;

    MessageType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
