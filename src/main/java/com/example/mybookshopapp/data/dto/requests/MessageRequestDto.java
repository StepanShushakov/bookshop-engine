package com.example.mybookshopapp.data.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author karl
 */

@Getter
@Setter
@AllArgsConstructor
public class MessageRequestDto {

    private String name;
    private String mail;
    private String topic;
    private String message;
    private String sendMessage;

}
