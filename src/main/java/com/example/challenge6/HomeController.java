package com.example.challenge6;

import com.cloudinary.utils.ObjectUtils;
import com.example.challenge6.config.CloudinaryConfig;
import com.example.challenge6.model.Employee;
import com.example.challenge6.model.Department;
import com.example.challenge6.repository.DepartmentRepository;
import com.example.challenge6.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listEmployees(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "list";
    }

    @GetMapping("/addempl")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentRepository.findAll());
        return "emplform";
    }

    @GetMapping("/adddept")
    public String newDepartment(Model model){
        model.addAttribute("department", new Department());
        return "deptform";
    }

    @PostMapping("/adddept")
    public String processCategory(@Valid @ModelAttribute("department") Department department){
        departmentRepository.save(department);
        return "redirect:/";
    }
    @PostMapping("/addempl")
    public String processEmployee(@Valid @ModelAttribute("employee") Employee employee,
                              @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            System.out.println("File is empty...");
            return "redirect:/addempl";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            employee.setPictureUrl(uploadResult.get("url").toString());

            employeeRepository.save(employee);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/addempl";
        }
        return "redirect:/";
    }


    @RequestMapping("/detail/{id}")
    public String showMeme(@PathVariable("id") long id, Model model) {
        model.addAttribute("employee", employeeRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMeme(@PathVariable("id") long id, Model model) {
        model.addAttribute("employee", employeeRepository.findById(id));
        return "emplform";
    }

    @RequestMapping("/delete/{id}")
    public String deleteMeme(@PathVariable("id") long id) {
        employeeRepository.deleteById(id);
        return "redirect:/";
    }

}
