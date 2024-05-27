import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class WhitelistManager {
    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);

    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";

    public static void modifyWhitelist(String lineNumber) {
        try {
            List<String> lines = Files.readAllLines(Path.of(WHITELIST_FILE));
            int lineNumberToDelete = Integer.parseInt(lineNumber);
            if (lineNumberToDelete >= 0 && lineNumberToDelete < lines.size()) {
                lines.remove(lineNumberToDelete);
                Files.write(Path.of(WHITELIST_FILE), lines);
                System.out.println("Line " + (lineNumberToDelete) + " deleted successfully.");
            } else {
                System.out.println("Invalid line number: " + (lineNumberToDelete + 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showWhitelist() {
        try (BufferedReader reader = new BufferedReader(new FileReader(WHITELIST_FILE))) {
            String line;
            System.out.println("USB Whitelist:");
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                System.out.println(lineCount++ + ".)    " + line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
