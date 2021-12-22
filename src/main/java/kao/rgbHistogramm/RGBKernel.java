package kao.rgbHistogramm;

import com.aparapi.Kernel;

public class RGBKernel extends Kernel {
    public final int[] pixel;
    public final int[] r;
    public final int[] g;
    public final int[] b;

    public RGBKernel(int[] pixel, int[] r, int[] g, int[] b) {
        this.pixel = pixel;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void run() {
        int x = getGlobalId(0);
        int y = getGlobalId(1);

        // zugriffs index auf pixel
        int index = y * getGlobalSize(0) + x;

        // check pixel
        int red = (pixel[index] & 0x00_FF_00_00) >> 16;
        int green = (pixel[index] & 0x00_00_FF_00) >> 8;
        int blue = pixel[index] & 0x00_00_00_FF;

        // increment
        atomicAdd(r, red, 1);
        atomicAdd(g, green, 1);
        atomicAdd(b, blue, 1);
    }
}
