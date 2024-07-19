package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Fileoperation {
    private String folderPath;
    private String fileName;
    private String password;
    private String passwordHash;

    private void initializeFile() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the folder path: ");
        folderPath = scanner.nextLine();

        System.out.print("Enter the file name: ");
        fileName = scanner.nextLine();

        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                System.out.println("Failed to create the folder.");
                return;
            }
        }

        loadPassword();

        if (password == null) {
            setPassword();
            savePassword();
        }
    }

    private void setPassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Set a password: ");
        password = scanner.nextLine();
        this.passwordHash = convertToHashedPassword(password);
    }
    private String convertToHashedPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        return null;
    }
}
private void storeHashedPassword() {
    try {
        String passwordFilePath = folderPath + File.separator + "hashed_password.txt";
        File passwordFile = new File(passwordFilePath);
        FileWriter writer = new FileWriter(passwordFile);
        writer.write(passwordHash);
        writer.close();
        System.out.println("Hashed password stored in: " + passwordFile.getAbsolutePath());
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    private boolean verifyPassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();
        return password.equals(enteredPassword);
    }

    private void loadPassword() {
        try {
            String passwordFilePath = folderPath + File.separator + fileName;
            File passwordFile = new File(passwordFilePath);
            if (passwordFile.exists()) {
                Scanner scanner = new Scanner(passwordFile);
                password = scanner.nextLine();
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePassword() {
        try {
            String passwordFilePath = folderPath + File.separator + fileName;
            File passwordFile = new File(passwordFilePath);
            FileWriter writer = new FileWriter(passwordFile);
             writer.write(password);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void addStudentDetails() {
        try {
            if (!verifyPassword()) {
                System.out.println("Incorrect password. Access denied.");
                return;
            }

            String filePath = folderPath + File.separator + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter the number of students: ");
                int numStudents = scanner.nextInt();
                scanner.nextLine();

                StringBuilder contentBuilder = new StringBuilder();
                for (int i = 1; i <= numStudents; i++) {
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("USN: ");
                    String usn = scanner.nextLine();
                    System.out.print("Semester: ");
                    String semester = scanner.nextLine();
                    System.out.print("Branch: ");
                    String branch = scanner.nextLine();

                    contentBuilder.append("Name: ").append(name).append(" | ");
                    contentBuilder.append("USN: ").append(usn).append(" | ");
                    contentBuilder.append("Semester: ").append(semester).append(" | ");
                    contentBuilder.append("Branch: ").append(branch).append(" | ");
                    contentBuilder.append("$").append("\n"); // Add '$' at the end of each record
                }

                String content = contentBuilder.toString();
                writeToFile(content);
            } else {
                System.out.println("File does not exist for the given password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String content) {
        try {
            if (!verifyPassword()) {
                System.out.println("Incorrect password. Access denied.");
                return;
            }
    
            String filePath = folderPath + File.separator + fileName;
            FileWriter writer = new FileWriter(filePath, true); // Append mode
    
            // Check if the file is empty
            File file = new File(filePath);
            if (file.length() != 0) {
                writer.write("\n"); // Add a new line before appending the content
            }
    
            writer.write(content);
            writer.close();
            System.out.println("Record added successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    




    private void deleteStudentRecord() {
        try {
            if (!verifyPassword()) {
                System.out.println("Incorrect password. Access denied.");
                return;
            }

            String filePath = folderPath + File.separator + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter the student USN to delete: ");
                String usn = scanner.nextLine();

                List<String> remainingLines = new ArrayList<>();
                Scanner fileScanner = new Scanner(file);
                boolean recordDeleted = false; // Flag to track if any record was deleted

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.contains("USN: " + usn)) {
                        System.out.println("Record deleted successfully.");
                        System.out.println("Deleted Record is:");
                        System.out.println(line);
                        recordDeleted = true;
                    } else {
                        remainingLines.add(line);
                    }
                }
                fileScanner.close();

                if (recordDeleted) {
                    FileWriter writer = new FileWriter(file);
                    for (String line : remainingLines) {
                        writer.write(line);
                        writer.write(System.lineSeparator());
                    }
                    writer.close();

                    if (remainingLines.isEmpty()) {
                        // If no records are remaining, delete the file
                        file.delete();
                        System.out.println("File deleted successfully.");
                    }
                } else {
                    System.out.println("No record found for the given USN.");
                }
            } else {
                System.out.println("File does not exist for the given password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifyStudentRecord() {
        try {
            if (!verifyPassword()) {
                System.out.println("Incorrect password. Access denied.");
                return;
            }

            String filePath = folderPath + File.separator + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter the student USN to modify: ");
                String usn = scanner.nextLine();

                List<String> modifiedLines = new ArrayList<>();
                Scanner fileScanner = new Scanner(file);
                boolean recordModified = false; // Flag to track if any record was modified

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.contains("USN: " + usn)) {
                        System.out.println("Current Record:");
                        System.out.println(line);
                        System.out.println("Enter modified details:");
                        System.out.println("1. Modify Name");
                        System.out.println("2. Modify USN");



                        System.out.println("3. Modify Semester");
                        System.out.println("4. Modify Branch");
                        System.out.println("5. Skip Modification");
                        System.out.print("Enter your choice: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Consume the newline character

                        String[] details = line.split(" \\| ");
                        String modifiedLine = line;

                        switch (choice) {
                            case 1:
                                System.out.print("Enter modified Name: ");
                                String modifiedName = scanner.nextLine();
                                details[0] = "Name: " + modifiedName;
                                break;
                            case 2:
                                System.out.print("Enter modified USN: ");
                                String modifiedUSN = scanner.nextLine();
                                details[1] = "USN: " + modifiedUSN;
                                break;
                            case 3:
                                System.out.print("Enter modified Semester: ");
                                String modifiedSemester = scanner.nextLine();
                                details[2] = "Semester: " + modifiedSemester;
                                break;
                            case 4:
                                System.out.print("Enter modified Branch: ");
                                String modifiedBranch = scanner.nextLine();
                                details[3] = "Branch: " + modifiedBranch;
                                break;
                            case 5:
                                break;
                            default:
                                System.out.println("Invalid choice. Skipping modification.");
                                break;
                        }

                        modifiedLine = String.join(" | ", details);
                        System.out.println("Modified Record:");
                        System.out.println(modifiedLine); // Display the modified record
                        modifiedLines.add(modifiedLine);
                        recordModified = true;
                    } else {
                        modifiedLines.add(line);
                    }
                }

                fileScanner.close();

                if (recordModified) {
                    FileWriter writer = new FileWriter(file);
                    for (String line : modifiedLines) {
                        writer.write(line);
                        writer.write(System.lineSeparator());
                    }
                    writer.close();
                    System.out.println("Record modified successfully.");
                } else {
                    System.out.println("No record found for the given USN.");
                }
            } else {
                System.out.println("File does not exist for the given password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchStudentRecord() {
        try {
            if (!verifyPassword()) {
                System.out.println("Incorrect password. Access denied.");
                return;
            }

            String filePath = folderPath + File.separator + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                Scanner scanner = new Scanner(System.in);

                System.out.print("Enter the student USN to search: ");
                String usn = scanner.nextLine();

                Scanner fileScanner = new Scanner(file);
                boolean recordFound = false;

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.contains("USN: " + usn)) {
                        System.out.println("Record found:");
                        System.out.println(line);
                        recordFound = true;
                        break;
                    }
                }

                fileScanner.close();

                if (!recordFound) {
                    System.out.println("No record found for the given USN.");
                }
            } else {
                System.out.println("File does not exist for the given password.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
    private void openFile() {
        try {
            if (!verifyPassword()) {
                System.out.println("Incorrect password. Access denied.");
                return;
            }
            String filePath = folderPath + File.separator + fileName;
            Scanner scanner = new Scanner(System.in);
            System.out.println("Choose an option:");
            System.out.println("1. Display content in terminal");
            System.out.println("2. Open file");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
    
            if (choice == 1) {
                File file = new File(filePath);
                if (file.exists()) {
                    Scanner fileScanner = new Scanner(file);
    
                    // Read the lines and store them in a List
                    List<String> lines = new ArrayList<>();
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        lines.add(line);
                    }
                    fileScanner.close();
    
                    // Determine the maximum length of each column
                    int[] columnLengths = new int[4];
                    for (String line : lines) {
                        String[] columns = line.split(" \\| ");
                        for (int i = 0; i < Math.min(columns.length, columnLengths.length); i++) {
                            if (columns[i].length() > columnLengths[i]) {
                                columnLengths[i] = columns[i].length();
                            }
                        }
                    }
    
                    // Build the formatted table
                    StringBuilder tableBuilder = new StringBuilder();
                    tableBuilder.append("+" + repeatChar('-', columnLengths[0] + 2) + "+"
                            + repeatChar('-', columnLengths[1] + 2) + "+"
                            + repeatChar('-', columnLengths[2] + 2) + "+"
                            + repeatChar('-', columnLengths[3] + 2) + "+\n");
                    tableBuilder.append("| " + padRight("Name", columnLengths[0]) + " | "
                            + padRight("USN", columnLengths[1]) + " | "
                            + padRight("Semester", columnLengths[2]) + " | "
                            + padRight("Branch", columnLengths[3]) + " |\n");
                    tableBuilder.append("+" + repeatChar('-', columnLengths[0] + 2) + "+"
                            + repeatChar('-', columnLengths[1] + 2) + "+"
                            + repeatChar('-', columnLengths[2] + 2) + "+"
                            + repeatChar('-', columnLengths[3] + 2) + "+\n");
    
                    for (String line : lines) {
                        String[] columns = line.split(" \\| ");
                        if (columns.length >= 4) {
                            tableBuilder.append("| " + padRight(columns[0].substring(6), columnLengths[0]) + " | "
                                    + padRight(columns[1].substring(5), columnLengths[1]) + " | "
                                    + padRight(columns[2].substring(10), columnLengths[2]) + " | "
                                    + padRight(columns[3].substring(7), columnLengths[3]) + " |\n");
                        }
                    }
    
                    tableBuilder.append("+" + repeatChar('-', columnLengths[0] + 2) + "+"
                            + repeatChar('-', columnLengths[1] + 2) + "+"
                            + repeatChar('-', columnLengths[2] + 2) + "+"
                            + repeatChar('-', columnLengths[3] + 2) + "+\n");
    
                    System.out.println(tableBuilder.toString());
                } else {
                    System.out.println("File does not exist.");
                }
            } else if (choice == 2) {
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", filePath);
                pb.start();
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String repeatChar(char c, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
    
    private String padRight(String s, int length) {
        if (s.length() >= length) {
            return s;
        } else {
            return s + repeatChar(' ', length - s.length());
        }
    }
    

    public void run() {
        Scanner scanner = new Scanner(System.in);

        initializeFile();
        storeHashedPassword();

        System.out.println("Menu:");
        System.out.println("1. Add Student Record");
        System.out.println("2. Delete Student Record");
        System.out.println("3. Modify Student Record");
        System.out.println("4. Search Student Record");
        System.out.println("5. Open File");
        System.out.println("6. Exit");

        int option;
        do {
            System.out.print("Enter your choice (1-6): ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    addStudentDetails();
                    break;
                case 2:
                    deleteStudentRecord();
                    break;
                case 3:
                    modifyStudentRecord();
                    break;
                case 4:
                    searchStudentRecord();
                    break;
                case 5:
                    openFile();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
            System.out.println();
        } while (option != 6);

        scanner.close();
    }

    public static void main(String[] args) {
        Fileoperation fileOperation = new Fileoperation();
        fileOperation.run();
    }
}