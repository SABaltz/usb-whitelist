import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class USBWhitelist {

    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";
    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);
    private static final List<String> WHITELIST = List.of("1234:5678", "abcd:ef12");
    private static Path filePath = Paths.get(WHITELIST_FILE);

    public static void main(String[] args) {
        FileManager.checkWhiteListFileExistence();

        String command = args[0];

        switch (command) {
            case "start":
                USBMonitor.startMonitoring();
                break;
            case "stop":
                USBMonitor.stopMonitoring();
                break;
            case "status":
                USBMonitor.checkStatus();
                break;
            case "add":
                if (args.length < 2) {
                    System.out.println("Usage: USBWhitelist add [usb_id]");
                } else if (Objects.equals(args[1], "connected")) {
                    USBManager.addConnectedUSBs();
                    System.out.println("Adding all connected USBs to whitelist");
                } else {
                    WhitelistManager.modifyWhitelist(args[1], true);
                }
                break;
            case "delete":
                if (args.length < 2) {
                    System.out.println("Usage: USBWhitelist delete [usb_id]");
                } else {
                    WhitelistManager.modifyWhitelist(args[1], false);
                }
                break;
            case "list":
                USBManager.listConnectedUSBs();
                break;
            case "whitelist":
                WhitelistManager.showWhitelist();
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }
}
