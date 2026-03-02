package com.gyu.engdu.domain.engdu.presentation.dto.request;

public record CreateEngduRequest(
    String topic,
    Long engduId,
    String level
) {

}
