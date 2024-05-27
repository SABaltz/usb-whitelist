import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    private static Path filePath = Paths.get(System.getProperty("user.home") + "/usb_whitelist.txt");

    public static void checkWhiteListFileExistence() {
        if (Files.exists(filePath)) {
            System.out.println("File exists, continuing");
        } else {
            System.out.println("Whitelist File Not Found");
            try {
                File parentDir = filePath.getParent().toFile();
                if (parentDir != null && !parentDir.exists()) {
                    if (parentDir.mkdirs()) {
                        System.out.println("Parent directories created successfully");
                    } else {
                        System.err.println("Failed to create parent directories");
                        return;
                    }
                }
                if (Files.createFile(filePath).toFile().exists()) {
                    System.out.println("File created successfully");
                } else {
                    System.out.println("File already exists");
                }
            } catch (IOException e) {
                System.err.println("Failed to create the file: " + e.getMessage());
            }
        }
    }
}
