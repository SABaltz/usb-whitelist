import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USBManager {

    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);
    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";

    public static void addConnectedUSBs() {
        List<String> usbIds = connectedUSBs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(WHITELIST_FILE, true))) {
            for (String usb : usbIds) {
                writer.write(usb);
                writer.newLine();
                System.out.println("Added USB ID to whitelist: " + usb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> connectedUSBs(){
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
