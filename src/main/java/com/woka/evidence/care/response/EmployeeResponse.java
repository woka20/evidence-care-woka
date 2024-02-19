package com.woka.evidence.care.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String id;
    private  String name;
    private EmployeeResponse manager;
    private int directReport;
    private int indirectReport;
    
}
