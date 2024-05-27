import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USBWhitelist {

    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";
    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);
    private static final List<String> WHITELIST = List.of("1234:5678", "abcd:ef12"); // Replace with your actual whitelist
    private static Path filePath = Paths.get(WHITELIST_FILE);

    public static void main(String[] args) {
        checkWhiteListFileExistence();

        String command = args[0];

        switch (command) {
            case "start":
                startMonitoring();
                break;
            case "stop":
                stopMonitoring();
                break;
            case "status":
                checkStatus();
                break;
            case "add":
                if (args.length < 2) {
                    System.out.println("Usage: USBWhitelist add [usb_id]");
                } else if (Objects.equals(args[1], "connected")) {
                    System.out.println("Adding all connected whitelists");
                } else {
                    modifyWhitelist(args[1], true);
                }
                break;
            case "delete":
                if (args.length < 2) {
                    System.out.println("Usage: USBWhitelist delete [usb_id]");
                } else {
                    modifyWhitelist(args[1], false);
                }
                break;
            case "list":
                listConnectedUSBs();
                break;
            case "whitelist":
                showWhitelist();
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private static void checkWhiteListFileExistence() {
        if (Files.exists(filePath)) {
            System.out.println("File exists, continuing");
        } else {
            System.out.println("Whitelist File Not Found");
            try {
                // Ensure the parent directories exist
                File parentDir = filePath.getParent().toFile();
                if (parentDir != null && !parentDir.exists()) {
                    if (parentDir.mkdirs()) {
                        System.out.println("Parent directories created successfully");
                    } else {
                        System.err.println("Failed to create parent directories");
                        return;
                    }
                }

                // Create the file if it does not exist
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

    private static void startMonitoring() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("/dev");
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("USB Whitelist Service Started");

            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        File deviceFile = new File("/dev", filename.toString());

                        if (!isWhitelisted(deviceFile)) {
                            System.out.println("Unauthorized USB device detected. Shutting down...");
                            // Uncomment the following line to actually shut down the system
                            // Runtime.getRuntime().exec("shutdown -h now");
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void stopMonitoring() {
        // Implement stop logic if needed (e.g., terminate the running Java process)
        System.out.println("Stop monitoring not implemented yet");
    }

    private static void checkStatus() {
        // Implement status check logic if needed
        System.out.println("Status check not implemented yet");
    }

    private static boolean isWhitelisted(File deviceFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(WHITELIST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(deviceFile.getName())) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void modifyWhitelist(String usbId, boolean add) {
        try {
            List<String> whitelist = new ArrayList<>();
            File whitelistFile = new File(WHITELIST_FILE);

            if (whitelistFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(whitelistFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        whitelist.add(line.trim());
                    }
                }
            }

            if (add) {
                if (!whitelist.contains(usbId)) {
                    whitelist.add(usbId);
                    System.out.println("Added USB ID to whitelist: " + usbId);
                } else {
                    System.out.println("USB ID already in whitelist: " + usbId);
                }
            } else {
                if (whitelist.contains(usbId)) {
                    whitelist.remove(usbId);
                    System.out.println("Removed USB ID from whitelist: " + usbId);
                } else {
                    System.out.println("USB ID not found in whitelist: " + usbId);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(whitelistFile))) {
                for (String id : whitelist) {
                    writer.write(id);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> listConnectedUSBs() {
        List<String> deviceIds = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("lsusb");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEVICE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String bus = matcher.group(1);
                    String device = matcher.group(2);
                    String id = matcher.group(3);
                    String tag = matcher.group(4);
                    deviceIds.add(id);

                    System.out.println("USBid: " + id);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceIds;
    }

    private static void showWhitelist() {
        try (BufferedReader reader = new BufferedReader(new FileReader(WHITELIST_FILE))) {
            String line;
            System.out.println("USB Whitelist:");
            while ((line = reader.readLine()) != null) {
                System.out.println(" - " + line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
