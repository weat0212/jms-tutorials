package com.andywang.p2p.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class Candidates implements Serializable {

    private String id;

    private String name;

    private int age;

    private String resume;

    private Education education;
}

