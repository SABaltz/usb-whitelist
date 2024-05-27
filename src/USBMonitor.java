import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

public class USBMonitor {
    private static WatchService watchService;
    private static Thread monitoringThread;
    private static boolean running = false;

    public static void startMonitoring() {
        if (running) {
            System.out.println("Monitoring is already running.");
            return;
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get("/dev");
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("USB Whitelist Service Started");
            running = true;

            monitoringThread = new Thread(() -> {
                while (running) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        return;
                    } catch (ClosedWatchServiceException e) {
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
            });
            monitoringThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopMonitoring() {
        if (!running) {
            System.out.println("Monitoring is not running.");
            return;
        }

        try {
            running = false;
            watchService.close();
            monitoringThread.interrupt();
            System.out.println("USB Whitelist Service Stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void checkStatus() {
        if (running) {
            System.out.println("Monitoring is currently running.");
        } else {
            System.out.println("Monitoring is not running.");
        }
    }
}
