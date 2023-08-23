package com.kyr.streaming.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unknown video quality specified")
public class UknownQualityRequestedException extends RuntimeException{
}
