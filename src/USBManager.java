import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class USBManager {

    private static final Pattern DEVICE_PATTERN = Pattern.compile("Bus\\s+(\\d+)\\s+Device\\s+(\\d+).+ID\\s(\\w+:\\w+)\\s(.+)$", Pattern.CASE_INSENSITIVE);
    private static final String WHITELIST_FILE = System.getProperty("user.home") + "/usb_whitelist.txt";

    public static void addConnectedUSBs() {
        List<String> usbIds = listConnectedUSBs();
        for (var usb : usbIds) {
            System.out.println(usb);
        }
    }

    public static List<String> listConnectedUSBs() {
        List<String> deviceIds = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("lsusb");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEVICE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    String id = matcher.group(3);
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
