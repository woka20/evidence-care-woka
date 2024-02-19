package com.woka.evidence.care.controller;


import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.woka.evidence.care.entity.Employee;
import com.woka.evidence.care.response.Response;
import com.woka.evidence.care.services.EmployeeService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value ="")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    @PostMapping("/find-employee-upload")
    public ResponseEntity<Object> uploadFile(@RequestParam("name") String name , @RequestParam("file") MultipartFile file) {
      
           Response listEmpResp= new Response(null, null, null);
           Employee[] employees =employeeService.readUploadFile(file);

            // Return list of Employee objects
            List<Employee> listEmp= Arrays.asList(employees);
          

            listEmpResp=employeeService.findEmployee(name, listEmp, 0);
            if (listEmpResp.getError() != null){
                return ResponseEntity.status(listEmpResp.getHttpStatus()).body(listEmpResp.getError());

            }
            return ResponseEntity.ok(listEmpResp.getEmployeeResponses());
      
    }

    
}
