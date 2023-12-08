package finall.service;

import finall.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StudentService {
    List<Student> getAllStudents();
    boolean studentIdExists(String studentId);
    void updateStudentDetails(String studentId, Student updatedStudent);
    Student getStudentById(String id);
    void sortResults(List<Student> students, String sortAttribute, String sortOrder);
    List<Student> searchById(String id);
    List<Student> searchByLastName(String lastName);
    List<Student> searchByGender(String gender);
    List<Student> searchByLevel(String level);
    List<Student> searchByAddress(String address);
    List<Student> searchByGpa(String gpa);
    List<String> validateStudents(List<Student> students);

    List<String> validateUpdatedStudents(List<Student> students);
    void addStudents(List<Student> students);
    //List<Student> searchByGpa(String gpa);

    List<Student> searchByFirstName(String firstName);
    void deleteStudent(String id);
}