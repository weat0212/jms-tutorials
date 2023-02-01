package com.andywang.jms.filtering;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class Mail implements Serializable {

    private static final long serialVersionUID = -1311359092135672840L;
    private int mailId;
    private String sender;
    private String senderAddress;
    private String receiver;
    private String receiverAddress;
    private boolean international;
    private double postage;
}
