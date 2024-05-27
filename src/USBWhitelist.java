import java.io.*;
import java.nio.file.*;
import java.util.*;

public class USBWhitelist {

    private static final String WHITELIST_FILE = "/etc/usb_whitelist.txt";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: USBWhitelist {start|stop|status|add|delete|list|whitelist} [usb_id]");
            return;
        }

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
//                            Runtime.getRuntime().exec("shutdown -h now");
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

    private static void listConnectedUSBs() {
        File devDir = new File("/dev");
        File[] files = devDir.listFiles((dir, name) -> name.startsWith("sd")); // Assuming USB devices are named like 'sdX'

        if (files != null && files.length > 0) {
            System.out.println("Connected USB devices:");
            for (File file : files) {
                System.out.println(" - " + file.getName());
            }
        } else {
            System.out.println("No USB devices connected.");
        }
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