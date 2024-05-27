import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

public class USBMonitor {

    public static void startMonitoring() {
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

                        if (!USBManager.isWhitelisted(deviceFile)) {
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

    public static void stopMonitoring() {
        System.out.println("Stop monitoring not implemented yet");
    }

    public static void checkStatus() {
        System.out.println("Status check not implemented yet");
    }
}
