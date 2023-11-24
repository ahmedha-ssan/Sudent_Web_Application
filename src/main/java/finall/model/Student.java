package finall.model;


import jakarta.xml.bind.annotation.*;
import lombok.Getter;

@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student {
    @Getter
    private int numStudents;
    @XmlAttribute
    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String gpa;
    private String level;
    private String address;
    public Student() {
    }
    public Student(String id, String firstName, String lastName, String gender, String gpa, String level, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.gpa = gpa;
        this.level = level;
        this.address = address;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }
    @XmlElement(name = "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "FirstName")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name = "LastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlElement(name = "Gender")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @XmlElement(name = "GPA")
    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    @XmlElement(name = "Level")
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @XmlElement(name = "Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", gpa='" + gpa + '\'' +
                ", level='" + level + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}