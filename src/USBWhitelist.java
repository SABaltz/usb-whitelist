import java.util.Objects;

public class USBWhitelist {

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
                if (Objects.equals(args[1], "connected") && Objects.equals(args[0], "add")) {
                    USBManager.addConnectedUSBs();
                    System.out.println("Adding all connected USBs to whitelist");
                } else {
                    System.out.println("Usage: USBWhitelist add connected");
                }
                break;
            case "delete":
                if (args.length < 2) {
                    System.out.println("Usage: USBWhitelist delete [whitelist_line_number]");
                    System.out.println("Please select a usb from the list below: \n");
                    WhitelistManager.showWhitelist();
                } else {
                    WhitelistManager.modifyWhitelist(args[1]);
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
