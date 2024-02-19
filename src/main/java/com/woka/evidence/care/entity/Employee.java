package com.woka.evidence.care.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Employee {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("managerId")
    private int managerId;

    private int directReport;
    private int indirectReport;
    
}
