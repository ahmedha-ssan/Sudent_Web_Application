package finall.service;

import finall.model.Student;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private static final String XML_FILE_PATH = "src/main/xml/university.xml";
    @Override
    public List<Student> getAllStudents() {
        return readStudentsFromXml();
    }
    private void createXmlFile(File xmlFile) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("University");
            doc.appendChild(rootElement);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<Student> readStudentsFromXml() {
        List<Student> students = new ArrayList<>();

        try {
            File xmlFile = new File(XML_FILE_PATH);
            if (!xmlFile.exists()) {
                createXmlFile(xmlFile);
            }
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);

            NodeList studentNodes = document.getElementsByTagName("Student");

            for (int i = 0; i < studentNodes.getLength(); i++) {
                Element studentElement = (Element) studentNodes.item(i);
                Student student = new Student();
                student.setId(getTextContent(studentElement, "id"));
                student.setFirstName(getTextContent(studentElement, "firstName"));
                student.setLastName(getTextContent(studentElement, "lastName"));
                student.setGender(getTextContent(studentElement, "gender"));
                student.setGpa(getTextContent(studentElement, "gpa"));
                student.setLevel(getTextContent(studentElement, "level"));
                student.setAddress(getTextContent(studentElement, "address"));

                students.add(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return students;
    }
    private String getTextContent(Element parent, String childTagName) {
        NodeList nodeList = parent.getElementsByTagName(childTagName);
        return nodeList.item(0).getTextContent();
    }

    public void deleteStudent(String studentId) {
        //createXmlFile(); // Ensure the XML file exists
        List<Student> students = readStudentsFromXml();
        students.removeIf(student -> student.getId().equals(studentId));
        saveStudentsToXml(students);
    }
    private void saveStudentsToXml(List<Student> students) {
        try {
            File xmlFile = new File(XML_FILE_PATH);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlFile);

            // Remove existing student elements
            NodeList existingStudents = doc.getElementsByTagName("Student");
            for (int i = existingStudents.getLength() - 1; i >= 0; i--) {
                Node studentNode = existingStudents.item(i);
                studentNode.getParentNode().removeChild(studentNode);
            }

            // Add new student elements
            for (Student student : students) {
                Element studentElement = doc.createElement("Student");
                createElementAndAppend(doc, studentElement, "id", student.getId());
                createElementAndAppend(doc, studentElement, "firstName", student.getFirstName());
                createElementAndAppend(doc, studentElement, "lastName", student.getLastName());
                createElementAndAppend(doc, studentElement, "gender", student.getGender());
                createElementAndAppend(doc, studentElement, "gpa", student.getGpa());
                createElementAndAppend(doc, studentElement, "level", student.getLevel());
                createElementAndAppend(doc, studentElement, "address", student.getAddress());
                doc.getDocumentElement().appendChild(studentElement);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void createElementAndAppend(Document doc, Element parentElement, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.setTextContent(textContent);
        parentElement.appendChild(element);
    }
    public void addStudents(List<Student> students) {
        //System.out.println(students.get(0).getId());
        List<Student> existingStudents = readStudentsFromXml();
        existingStudents.addAll(students);
        saveStudentsToXml(existingStudents);
    }
    @Override
    public boolean studentIdExists(String studentId) {
        List<Student> allStudents = readStudentsFromXml();
        return allStudents.stream().anyMatch(student -> student.getId().equals(studentId));
    }
    @Override
    public Student getStudentById(String id) {
        List<Student> students = readStudentsFromXml();
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }
    @Override
    public List<String> validateStudents(List<Student> students) {
        List<String> validationErrors = new ArrayList<>();

        for (Student student : students) {
            if (StringUtils.isBlank(student.getId()) || StringUtils.isBlank(student.getFirstName()) ||
                    StringUtils.isBlank(student.getLastName()) || StringUtils.isBlank(student.getGender()) ||
                    StringUtils.isBlank(student.getGpa()) || StringUtils.isBlank(student.getLevel()) ||
                    StringUtils.isBlank(student.getAddress())) {
                validationErrors.add("HEY, ALL ATTRIBUTES ARE REQUIRED.");
            }

            if (studentIdExists(student.getId())) {
                validationErrors.add("Student ID " + student.getId() + " already exists. 'ID MUST BE UNIQUE' .");
            }

            if (!isValidName(student.getFirstName()) || !isValidName(student.getLastName()) ||
                    !isValidCharacterOnly(student.getAddress())) {
                validationErrors.add("Use only characters in student name or address.");
            }

            try {
                double gpa = Double.parseDouble(student.getGpa());
                if (gpa < 0 || gpa > 4) {
                    validationErrors.add("GPA must be between 0 and 4.");
                }
            } catch (NumberFormatException e) {
                validationErrors.add("Please enter a valid numeric value.");
            }
        }

        return validationErrors;
    }
    @Override
    public List<String> validateUpdatedStudents(List<Student> students) {
        List<String> validationErrors = new ArrayList<>();

        for (Student student : students) {
            if (StringUtils.isBlank(student.getId()) || StringUtils.isBlank(student.getFirstName()) ||
                    StringUtils.isBlank(student.getLastName()) || StringUtils.isBlank(student.getGender()) ||
                    StringUtils.isBlank(student.getGpa()) || StringUtils.isBlank(student.getLevel()) ||
                    StringUtils.isBlank(student.getAddress())) {
                validationErrors.add("All attributes are REQUIRED for each student.");
            }

            if (!isValidName(student.getFirstName()) || !isValidName(student.getLastName()) ||
                    !isValidCharacterOnly(student.getAddress())) {
                validationErrors.add("Use only characters in student name or address.");
            }

            try {
                double gpa = Double.parseDouble(student.getGpa());
                if (gpa < 0 || gpa > 4) {
                    validationErrors.add("GPA must be between 0 and 4.");
                }
            } catch (NumberFormatException e) {
                validationErrors.add("Please enter a valid numeric value.");
            }
        }

        return validationErrors;
    }

    private boolean isValidName(String name) {
        return StringUtils.isAlphaSpace(name);
    }
    private boolean isValidCharacterOnly(String value) {
        return StringUtils.isAlpha(value);
    }
    @Override
    public void updateStudentDetails(String studentId, Student updatedStudent) {
        List<Student> students = readStudentsFromXml();

        for (int i = 0; i < students.size(); i++) {

            Student existingStudent = students.get(i);
            System.out.println(studentId);

            if (existingStudent.getId().equals(studentId)) {
                updatedStudent.setId(studentId);
                if (updatedStudent.getFirstName() == null) {
                    updatedStudent.setFirstName(existingStudent.getFirstName());
                }
                if (updatedStudent.getLastName() == null) {
                    updatedStudent.setLastName(existingStudent.getLastName());
                }
                if (updatedStudent.getGender() == null) {
                    updatedStudent.setGender(existingStudent.getGender());
                }
                if (updatedStudent.getGpa() == null) {
                    updatedStudent.setGpa(existingStudent.getGpa());
                }
                if (updatedStudent.getLevel() == null) {
                    updatedStudent.setLevel(existingStudent.getLevel());
                }
                if (updatedStudent.getAddress() == null) {
                    updatedStudent.setAddress(existingStudent.getAddress());
                }
                students.set(i, updatedStudent);
                break;
            }
        }

        saveStudentsToXml(students);
    }
    public void sortResults(List<Student> students, String sortAttribute, String sortOrder) {
        Comparator<Student> comparator;

        switch (sortAttribute) {
            case "id":
                comparator = Comparator.comparing(Student::getId);
                break;
            case "firstName":
                comparator = Comparator.comparing(Student::getFirstName);
                break;
            case "lastName":
                comparator = Comparator.comparing(Student::getLastName);
                break;
            case "gpa":
                // GPA is a numerical value, so use Double::compareTo for correct sorting
                comparator = Comparator.comparing(student -> Double.parseDouble(student.getGpa()));
                break;
            case "address":
                comparator = Comparator.comparing(Student::getAddress);
                break;
            case "level":
                comparator = Comparator.comparing(Student::getLevel);
                break;
            default:
                comparator = Comparator.comparing(Student::getId);
        }
        // Apply ascending or descending order
        if (sortOrder.equalsIgnoreCase("desc")) {
            students.sort(comparator.reversed());
        } else {
            students.sort(comparator);
        }
        saveStudentsToXml(students);
    }
    @Override
    public List<Student> searchById(String id) {
        return searchStudentsByAttribute("id", id);
    }
    @Override
    public List<Student> searchByFirstName(String firstName) {
        return searchStudentsByAttribute("firstName", firstName);
    }
    @Override
    public List<Student> searchByLastName(String lastName) {
        return searchStudentsByAttribute("lastName", lastName);
    }
    @Override
    public List<Student> searchByGender(String gender) {
        return searchStudentsByAttribute("gender", gender);
    }
    @Override
    public List<Student> searchByGpa(String gpa) {
        return searchStudentsByAttribute("gpa", gpa);
    }
    @Override
    public List<Student> searchByLevel(String level) {
        return searchStudentsByAttribute("level", level);
    }
    @Override
    public List<Student> searchByAddress(String address) {
        return searchStudentsByAttribute("address", address);
    }
    private List<Student> searchStudentsByAttribute(String attribute, String value) {
        List<Student> allStudents = readStudentsFromXml();
        List<Student> searchResults = new ArrayList<>();

        for (Student student : allStudents) {
            String attributeValue;
            switch (attribute) {
                case "id":
                    attributeValue = student.getId();
                    break;
                case "firstName":
                    attributeValue = student.getFirstName();
                    break;
                case "lastName":
                    attributeValue = student.getLastName();
                    break;
                case "gender":
                    attributeValue = student.getGender();
                    break;
                case "gpa":
                    attributeValue = student.getGpa();
                    break;
                case "level":
                    attributeValue = student.getLevel();
                    break;
                case "address":
                    attributeValue = student.getAddress();
                    break;
                default:
                    attributeValue = "";
            }

            if (attributeValue != null && attributeValue.equals(value)) {
                searchResults.add(student);
            }
        }

        return searchResults;
    }
  }