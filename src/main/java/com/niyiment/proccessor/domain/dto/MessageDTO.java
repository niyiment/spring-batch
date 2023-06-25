package com.niyiment.proccessor.domain.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private String message;

    public MessageDTO(String message) {
        this.message = message;
    }
}