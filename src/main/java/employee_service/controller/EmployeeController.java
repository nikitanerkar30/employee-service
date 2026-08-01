package employee_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/employees")
public class EmployeeController {


    @GetMapping
    public List<String> getEmployees(){

        return Arrays.asList(
                "Nikita",
                "Rahul",
                "Amit"
        );
    }


    @GetMapping("/{id}")
    public String getEmployee(
            @PathVariable int id){

        return "Employee ID : " + id;
    }

}