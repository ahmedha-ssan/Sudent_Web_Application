package finall.controller;

import finall.model.Student;
import finall.model.StudentsForm;
import finall.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class indexController {
    private final StudentService studentService;
    public indexController(StudentService studentService) {
        this.studentService = studentService;
    }

    //List all students in the XML
    @GetMapping("/list")
    public String getAllStudents(
            @RequestParam(name = "sort", required = false, defaultValue = "id") String sortAttribute,
            @RequestParam(name = "order", required = false, defaultValue = "asc") String sortOrder,
            Model model) {

        List<Student> students = studentService.getAllStudents();
        studentService.sortResults(students, sortAttribute, sortOrder);
        model.addAttribute("students", students);
        return "listSTD";
    }



    @GetMapping("/search-by-gpa")
    public String searchByGpa(@RequestParam(name = "gpa", required = false) String gpa, Model model) {
        List<Student> searchResults;
        if (gpa != null && !gpa.isEmpty()) {
            searchResults = studentService.searchByGpa(gpa);
        } else {
            searchResults = studentService.searchByFirstName("");
        }
        model.addAttribute("students", searchResults);
        return "searchByGpa";
    }
    @GetMapping("/search-by-firstname")
    public String searchByFirstName(@RequestParam(name = "firstName", required = false) String firstName, Model model) {
        List<Student> searchResults;
        if (firstName != null && !firstName.isEmpty()) {
            searchResults = studentService.searchByFirstName(firstName);
        } else {
            searchResults = studentService.searchByGpa("22222222222222222222222222");
        }
        model.addAttribute("students", searchResults);
        return "searchByFirstName";
    }


    @GetMapping("/add-students-form")
    public String addStudentsForm(Model model) {
        model.addAttribute("student", new Student());
        return "addStudents";
    }

    @PostMapping("/add-students")
    public String addStudents(@ModelAttribute Student student, Model model) {
        if (student.getNumStudents() > 0) {
            model.addAttribute("numStudents", student.getNumStudents());
            return "addStudents";
        } else {
            return "redirect:/list";
        }
    }

    @PostMapping("/save-students")
    public String saveStudents(@ModelAttribute("studentsForm") StudentsForm studentsForm, Model model) {

        List<Student> students = studentsForm.getStudents();
        List<String> validationErrors = studentService.validateStudents(students);

        if (!validationErrors.isEmpty()) {
            model.addAttribute("validationErrors", validationErrors);
            return "addStudents";
        }
        studentService.addStudents(students);
        return "redirect:/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return "redirect:/list";
    }


    @GetMapping("/update/{id}")
    public String showUpdateStudentForm(@PathVariable String id, Model model) {
        Student existingStudent = studentService.getStudentById(id);

        if (existingStudent != null) {
            model.addAttribute("existingStudent", existingStudent);
            return "updateStudent";
        } else {
            return "redirect:/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable String id, @ModelAttribute Student updatedStudent, Model model) {
        List<String> validationErrors = studentService.validateUpdatedStudents(List.of(updatedStudent));
        if (!validationErrors.isEmpty()) {
            model.addAttribute("validationErrors", validationErrors);
            return "error";
        }
        studentService.updateStudentDetails(id, updatedStudent);
        return "redirect:/list";
    }


    @GetMapping("/search")
    public String searchStudents(
            @RequestParam(name = "field", required = false, defaultValue = "id") String searchField,
            @RequestParam(name = "query", required = false) String query,
            Model model) {

        List<Student> searchResults;
        if (query != null && !query.isEmpty()) {
            switch (searchField) {
                case "id":
                    searchResults = studentService.searchById(query);
                    break;
                case "firstName":
                    searchResults = studentService.searchByFirstName(query);
                    break;
                case "lastName":
                    searchResults = studentService.searchByLastName(query);
                    break;
                case "gender":
                    searchResults = studentService.searchByGender(query);
                    break;
                case "gpa":
                    searchResults = studentService.searchByGpa(query);
                    break;
                case "level":
                    searchResults = studentService.searchByLevel(query);
                    break;
                case "address":
                    searchResults = studentService.searchByAddress(query);
                    break;
                default:
                    searchResults = new ArrayList<>();
                    break;
            }
        } else {
            searchResults = studentService.getAllStudents();
        }

        model.addAttribute("students", searchResults);
        model.addAttribute("numStudents", searchResults.size());
        return "searchByAnyAttribute";
    }
}
