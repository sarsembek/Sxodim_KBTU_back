package kz.kbtu.sxodimkbtu.controller;

import kz.kbtu.sxodimkbtu.model.Department;
import kz.kbtu.sxodimkbtu.model.Event;
import kz.kbtu.sxodimkbtu.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class DepartmentController {

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/getDepartments")
    public List<Department> getDepartments() throws InterruptedException, ExecutionException {
        return departmentService.getDepartments();
    }

    @PostMapping("/createDepartment")
    public String createDepartment(@RequestBody Department department ) throws InterruptedException, ExecutionException {
        return departmentService.saveDepartmentDetails(department);
    }
}
