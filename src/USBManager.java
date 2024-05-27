import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USBManager {

    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);
    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";

    public static void addConnectedUSBs() {
        Set<String> whitelist = loadWhitelist();
        List<String> usbIds = connectedUSBs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WHITELIST_FILE, true))) {
            for (String usb : usbIds) {
                Matcher matcher = DEVICE_PATTERN.matcher(usb);
                if (matcher.matches()) {
                    String id = matcher.group(3);
                    if (!whitelist.contains(id)) {
                        writer.write(usb);
                        writer.newLine();
                        System.out.println("Added USB ID to whitelist: " + usb);
                        whitelist.add(id);  // Add to set to avoid duplicates in the current run
                    } else {
                        System.out.println("USB ID already in whitelist: " + usb);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> loadWhitelist() {
        Set<String> whitelist = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(WHITELIST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEVICE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String id = matcher.group(3);
                    whitelist.add(id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return whitelist;
    }

    public static List<String> connectedUSBs() {
        List<String> deviceIds = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("lsusb");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEVICE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String name = matcher.group(4);
                    String id = matcher.group(3);
                    deviceIds.add(id);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceIds;
    }

    public static void listConnectedUSBs() {
        try {
            Process process = Runtime.getRuntime().exec("lsusb");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEVICE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String name = matcher.group(4);
                    String id = matcher.group(3);
                    System.out.println("USBid: " + id + " USB Name: " + name);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWhitelisted(File deviceFile) {
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
}
