package com.woka.evidence.care.response;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {
    private List<EmployeeResponse> employeeResponses;
    private String error;
    private HttpStatus httpStatus;

    public Response(List<EmployeeResponse> employeeResponses, String error, HttpStatus httpStatus) {
        this.employeeResponses = employeeResponses;
        this.error = error;
        this.httpStatus=httpStatus;
    }

    public List<EmployeeResponse> getEmployeeResponses() {
        return employeeResponses;
    }

    public String getError() {
        return error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}