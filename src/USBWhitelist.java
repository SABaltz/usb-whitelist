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
