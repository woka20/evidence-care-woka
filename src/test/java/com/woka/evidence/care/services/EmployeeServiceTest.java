package com.woka.evidence.care.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woka.evidence.care.entity.Employee;
import com.woka.evidence.care.response.EmployeeResponse;
import com.woka.evidence.care.response.Response;

// import lombok.extern.slf4j.Slf4j;


// @Slf4j
public class EmployeeServiceTest {
    
    private static EmployeeService employeeService;
    
    
    private static List<Employee> employees = new ArrayList<Employee>();
   

    @BeforeAll
    static void setUp(){
        employeeService = new EmployeeService();
    }

  
    static void getFileEmployee(String filePath) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Traverse the directory to find JSON files
        Files.walk(Path.of(filePath))
             .filter(Files::isRegularFile)
             .filter(p -> p.toString().endsWith(".json"))
             .forEach(jsonFilePath -> {
                 try {
                     // Read and parse JSON file to Employee object
                     employees = objectMapper.readValue(jsonFilePath.toFile(), new TypeReference<List<Employee>>(){});
                    
                 } catch (IOException e) {
                     e.printStackTrace(); // Handle exception appropriately
                 }
             });

     
    
}

    


    @Test
    void testfindEmployee() throws Exception {
        getFileEmployee("src/test/resources/files/correct-employees.json");
        List<EmployeeResponse> expectedRes=  new ArrayList<EmployeeResponse>();
        EmployeeResponse empValue= new EmployeeResponse();
        EmployeeResponse manager= new EmployeeResponse();
        manager.setId("1");
        manager.setName("raelynn");
        manager.setManager(null);
        empValue.setId("3");
        empValue.setName("kacie");
        empValue.setManager(manager);
        empValue.setDirectReport(1);
        empValue.setIndirectReport(0);
        manager.setDirectReport(0);
        manager.setIndirectReport(0);
        expectedRes.add(empValue);
        Response result = employeeService.findEmployee("kacie", employees, 0);
     
        Assertions.assertEquals(expectedRes,result.getEmployeeResponses());
    }

    @Test
    void testfindEmployeeMultipleManager() throws Exception {
        getFileEmployee("src/test/resources/files/another-faulty-employees.json");
        Response result = employeeService.findEmployee("linton", employees, 0);

        Assertions.assertEquals(HttpStatus.NOT_FOUND,result.getHttpStatus());
    }

    @Test
    void testfindEmployeeNoHieracy() throws Exception {
        getFileEmployee("src/test/resources/files/faulty-employees.json");
        Response result = employeeService.findEmployee("keane", employees, 0);

        Assertions.assertEquals(HttpStatus.NOT_FOUND,result.getHttpStatus());
    }
    
}
