package com.woka.evidence.care.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woka.evidence.care.entity.Employee;
import com.woka.evidence.care.response.EmployeeResponse;
import com.woka.evidence.care.response.Response;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeService {

    //Read file .json uploaded to Employee entity
    public Employee[] readUploadFile(MultipartFile file){  
        try{
            byte[] bytes = file.getBytes();

                // Convert byte array to String
            String json = new String(bytes);
            
                // Convert JSON to array of Employee objects
            ObjectMapper objectMapper = new ObjectMapper();
            Employee[] result= objectMapper.readValue(json, Employee[].class);

         
            return result;
        }catch(Exception e){
            return null;
        }
    }

    public Response findEmployee(String names, List<Employee> listEmp, int count) {
        List<EmployeeResponse> employeeFound = new ArrayList<EmployeeResponse>();
        Map<String, String> managerNames = new HashMap<String, String>();
        
        //Multiple input names split by comma
        String[] namesList= names.toLowerCase().split(",");
        Response processTree= new Response(null,null,null);
        try{
            validateNoHierarcy(names, listEmp);
            for (String name : namesList){
                // Generate Employee Tree Hierarcy
                processTree=processGenerateTreeHierarcy(name, listEmp, count, employeeFound, managerNames, name);
                    
                   
                    managerNames.forEach((key, value) -> {
                        Employee emp = findEmployeeByName(name, listEmp);
                        String[] strs= value.split(",");
                     
                        if (emp != null && key==emp.getName() && strs.length > 2){
                            String errorMessage = String.format("Unable to process employee tree. %s has multiple managers: %s ", key, value.substring(0, value.length() - 2));
                            throw new NullPointerException(errorMessage);
            
                        }

                        
                    });
            
                
            }
        }catch(Exception e){                       
            return new Response(null, e.getMessage(), HttpStatus.NOT_FOUND);
        } 
        if (processTree.getError() != null){
            return processTree;
        }else{
            // Show employee that found 
            employeeFound=processTree.getEmployeeResponses();
        }
        return new Response (employeeFound, null, HttpStatus.ACCEPTED);
    }
    

    private Response processGenerateTreeHierarcy(String name, List<Employee> listEmp, int count, List<EmployeeResponse>employeeFound, Map<String, String> managerNames, String mainSearch){
        int direct=1;
        int indirect= 0;
        EmployeeResponse empRes=new EmployeeResponse(); 
        
        try{
        for (Employee employee : listEmp) {
            
                            
                if (employee.getName().equals(name)) {
                    
                    if(employee.getManagerId() > 0) {
                        //Count indirect report
                        if (count == 0){
                            count=countReports(employee.getManagerId(), listEmp);
                        }
                        if (count>0){
                            indirect=count-1;
                        }
                        empRes=employeeResponseById(employee.getManagerId(), listEmp, direct, indirect);
                   
                        
                        processGenerateTreeHierarcy(empRes.getName(), listEmp, count, employeeFound, managerNames, mainSearch);

                    }
                    //input response manager information into employee's manager field
                    empRes=mapToResponse(employee, empRes);     
                    empRes.setDirectReport(direct);
                    empRes.setIndirectReport(count);
                    
               
                    if(managerNames.get(findEmployeeById(Integer.parseInt(empRes.getId()), listEmp).getName()) !=null){
                        managerNames.put(findEmployeeById(Integer.parseInt(empRes.getId()), listEmp).getName(), managerNames.get(findEmployeeById(Integer.parseInt(empRes.getId()), listEmp).getName()) + empRes.getManager().getName()+", ");
                    }else{
                        managerNames.put(findEmployeeById(Integer.parseInt(empRes.getId()), listEmp).getName(),  empRes.getManager().getName()+", "); 
                    }
      
                    
                }
              
            }
                //Ensure only the name that user try to search in param only that will be shown 
                if (mainSearch.equals(empRes.getName())){
                    employeeFound.add(empRes);
                }

                if (employeeFound.size()==0){
                    throw new NullPointerException("Name That You Search May Not Exist in Employee File Uploaded");
                }

            }catch(Exception e){
                return new Response(null, e.getMessage(), HttpStatus.NOT_FOUND);
            } 
            return new Response(employeeFound, null, HttpStatus.OK);
    }

    //Validate is the name of input have hierarcy or not
    private void validateNoHierarcy(String names, List<Employee> listEmp){
        List<String> allNamesUnableToProcess= new ArrayList<String>();
        String[] nameList= names.split(",");
        for (Employee emp : listEmp){
            if (!emp.getId().equals("1") && emp.getManagerId() <= 0){
                for(String name:nameList){
                    if(emp.getName().equals(name)){
                    allNamesUnableToProcess.add(name);
                    }
                }
            }
        }
       
        if (allNamesUnableToProcess.size() > 0){
                String errorMessage = String.format("Error: Unable to process employeee hierarchy. %s not having hierarchy", String.join(",", allNamesUnableToProcess).replaceAll(",$", "")); 
                throw new IllegalStateException(errorMessage);
        }
    
    }

    //Generate employee response information by using id
    private EmployeeResponse employeeResponseById(int id, List<Employee> list, int direct, int indirect) {        
        int newDirect=direct;
        int newIndirect=indirect-1;
        EmployeeResponse res= new EmployeeResponse();
        for(Employee employee :list) {
           if (Integer.parseInt(employee.getId())== id) {
               
                res.setId(employee.getId());
                res.setName(employee.getName());
                res.setManager(employee.getManagerId() > 0 ?employeeResponseById(employee.getManagerId(), list, newDirect, newIndirect):null);
                if (res.getManager()==null){
                    newDirect=newDirect-1;
                    res.setDirectReport(newDirect);
                    res.setIndirectReport(0);
                }else{
                    res.setDirectReport(direct);
                    res.setIndirectReport(indirect);
                }
                
                
                    return res;
                }
            
        }
            return res;
    }

    public static int countReports(int id, List<Employee> emps) {
        int count = 0;
        Employee employee = findEmployeeById(id, emps);
        while (employee != null && employee.getManagerId() > 0) {
            count++;
            employee = findEmployeeById(employee.getManagerId(), emps);
        }
        return count;
    }

     //Find employee information by using id
    public static Employee findEmployeeById(int id, List<Employee> emps) {
        for (Employee employee : emps) {
            if (Integer.parseInt(employee.getId()) == id) {
                return employee;
            }
        }
        return null;
    }

     //Find employee information by using name
    public static Employee findEmployeeByName(String  name, List<Employee> emps) {
        for (Employee employee : emps) {
            if (employee.getName().equals(name)) {
              
                return employee;
            }
        }
        return null;
    }

     //Structure Format of Hierarcy
    private EmployeeResponse mapToResponse(Employee entity, EmployeeResponse empRes){
         return EmployeeResponse.builder().id(entity.getId()).name(entity.getName()).manager(empRes).build();
   }
  
}
