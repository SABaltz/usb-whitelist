import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhitelistManager {
    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);

    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";

    public static void modifyWhitelist(String usbId, boolean add) {
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

    public static void showWhitelist() {
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
