package kao.mandelbrot;

import com.aparapi.*;

public class MandelbrotKernel extends Kernel {

    public double[] farbe;
    public double[] helligkeit;
    public double x1;
    public double y1;
    public double x2;
    public double y2;
    public int maxIter;


    public MandelbrotKernel(double[] farbe, double[] helligkeit) {
        this.farbe = farbe;
        this.helligkeit = helligkeit;
    }

    public void setParameters(double x1, double y1, double x2, double y2, int maxIter) {

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.maxIter = maxIter;
    }

    @Override
    public void run() {
        int y = getGlobalId(1);
        int x = getGlobalId(0);

        // c real = x
        // ci imag = y

        // komplexe Zahl
        double c = x1 + (x2 - x1) * x / getGlobalSize(0);
        double ci = y1 + (y2 - y1) * y / getGlobalSize(1);

        // Start wert Z0
        double zi = 0;
        double z = 0;

        for (int i = 0; i < maxIter; i++) {

            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);

            z = zT + c;
            zi = ziT + ci;

            // betrag von complexer zahl
            // 4 = 2 ^2
            if (z * z + zi * zi >= 4.0) {
                // farbe setzen
                // helligkeit setzen
                int index = y * getGlobalSize(0) + x;
                //farbe[index] = log(i) * 360;
                // Julia = max_iter - remain_iter - log(log(betrag_2) / log(4)) / log(2)
                farbe[index] = maxIter - i - log(log(i) / log(4)) / log(2);
                helligkeit[index] = i < maxIter ? 1 : 0;
            }
        }

    }
}
