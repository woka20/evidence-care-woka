package com.woka.evidence.care.controller;

import static org.mockito.Mockito.when;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.web.multipart.MultipartFile;

import com.woka.evidence.care.entity.Employee;
import com.woka.evidence.care.response.EmployeeResponse;
import com.woka.evidence.care.response.Response;
import com.woka.evidence.care.services.EmployeeService;

// @WebMvcTest(EmployeeController.class)
// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    public void testUploadFile() {
        // Prepare test data
        String name = "test";
        String jsonContent = "[{\"id\": \"1\",\"name\": \"John\", \"managerId\": 30}]";
        MultipartFile file = new MockMultipartFile("file", "test.json", "application/json", jsonContent.getBytes(StandardCharsets.UTF_8));

        // Mock the behavior of employeeService.readUploadFile()
        Employee[] employees = {/* populate with your employee objects */};
        when(employeeService.readUploadFile(file)).thenReturn(employees);

        // Mock the behavior of employeeService.findEmployee()
        /* mock your response object */
        EmployeeResponse empResp=new EmployeeResponse();
            empResp.setId("1");
            empResp.setName("John");
            List<EmployeeResponse> listResp= new ArrayList<EmployeeResponse>();
            listResp.add(empResp);
           Response resp= new Response(listResp, null, HttpStatus.ACCEPTED);
        when(employeeService.findEmployee(name, Arrays.asList(employees), 0)).thenReturn(resp);

        // Call the controller method
        ResponseEntity<Object> result = employeeController.uploadFile(name, file);

        // Verify the result
        Assertions.assertNotNull(result);
    }
}
