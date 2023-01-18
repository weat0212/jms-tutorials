package com.andywang.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@ToString
public class CustomMessage implements Serializable {

    private Date timestamp;

    private String message;
}
