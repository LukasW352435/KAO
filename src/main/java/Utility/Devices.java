package Utility;// ggf. package-Anweisung

import com.aparapi.device.OpenCLDevice;
import com.aparapi.internal.opencl.OpenCLPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Devices {
    public static OpenCLDevice selectDevice() {
        // Informationen �ber alle verf�gbaren OpenCL-Implementierungen ausgeben

        class Processor { // Hilfsklasse
            String name, version, typ;
            OpenCLDevice device;

            Processor(OpenCLPlatform platform, OpenCLDevice device) {
                this.device = device;
                name = platform.getName();
                Matcher m = Pattern.compile("\\d.\\d").matcher(platform.getVersion());
                version = m.find() ? m.group() : "";
                typ = device.getType().name();
            }

            @Override
            public String toString() {
                return typ + " - OpenCL " + version + " - " + name;
            }
        }

        // erzeuge alle Platform-Device-Kombinationen

        List<Processor> processors = new ArrayList<>();

        for (var platform : OpenCLPlatform.getUncachedOpenCLPlatforms())
            for (var device : platform.getOpenCLDevices())
                processors.add(new Processor(platform, device));

        for (int i = 0; i < processors.size(); i++)
            System.out.println(i + 1 + ") " + processors.get(i));

        var tastatur = new Scanner(System.in);
        System.out.print("\nSelect: ");
        int i = tastatur.nextInt() - 1;

        var processor = processors.get(i);
        System.out.println("\n" + processor + "\n");

        return processor.device;
    }

    public static OpenCLDevice selectFirstDevice() {
        // Informationen �ber alle verf�gbaren OpenCL-Implementierungen ausgeben

        class Processor {                    // Hilfsklasse
            String name, version, typ;
            OpenCLDevice device;

            Processor(OpenCLPlatform platform, OpenCLDevice device) {
                this.device = device;
                name = platform.getName();
                Matcher m = Pattern.compile("\\d.\\d").matcher(platform.getVersion());
                version = m.find() ? m.group() : "";
                typ = device.getType().name();
            }

            @Override
            public String toString() {
                return typ + " - OpenCL " + version + " - " + name;
            }
        }

        // erzeuge alle Platform-Device-Kombinationen

        List<Processor> processors = new ArrayList<>();

        for (var platform : OpenCLPlatform.getUncachedOpenCLPlatforms())
            for (var device : platform.getOpenCLDevices())
                processors.add(new Processor(platform, device));

        for (int i = 0; i < processors.size(); i++)
            System.out.println(i + 1 + ") " + processors.get(i));

        int i = 0;

        var processor = processors.get(i);
        System.out.println("\n" + processor + "\n");

        return processor.device;
    }

}
