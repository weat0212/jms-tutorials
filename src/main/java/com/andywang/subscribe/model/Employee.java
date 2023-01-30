package com.andywang.subscribe.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String designation;
    private String phone;
}
