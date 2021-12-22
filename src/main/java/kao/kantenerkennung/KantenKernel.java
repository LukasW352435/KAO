package kao.kantenerkennung;

import com.aparapi.Kernel;
import com.aparapi.Range;

public class KantenKernel extends Kernel {
    public final int[] in;
    public final int[] out;
    public final Range range;
    public final int size;

    public KantenKernel(int[] in, int[] out, Range range) {
        this.in = in;
        this.out = out;
        this.range = range;
        size = getGlobalSize();
    }

    @Override
    public void run() {
        int x = getGlobalId(0);
        int y = getGlobalId(1);

        int index = y * getGlobalSize(0) + x;

        int gx = -1 * (in[getIndex(x - 1, y - 1)] & 0xFF) + (in[getIndex(x - 1, y + 1)] & 0xFF) - 2 * (in[getIndex(x, y - 1)] & 0xFF) + 2 * (in[getIndex(x, y + 1)] & 0xFF) - (in[getIndex(x + 1, y - 1)] & 0xFF) + (in[getIndex(x + 1, y + 1)] & 0xFF);

        int gy = -1 * (in[getIndex(x - 1, y - 1)] & 0xFF) + (in[getIndex(x + 1, y - 1)] & 0xFF) - 2 * (in[getIndex(x - 1, y)] & 0xFF) + 2 * (in[getIndex(x + 1, y)] & 0xFF) - (in[getIndex(x - 1, y + 1)] & 0xFF) + (in[getIndex(x + 1, y + 1)] & 0xFF);

        int gij = (int) min(sqrt(gx * gx + gy * gy), 255);

        out[index] = gij;

    }

    private int getIndex(int x, int y) {
        if (x < 0) {
            x = 0;
        } else if (x > size - 1) {
            x = size;
        }
        if (y < 0) {
            y = 0;
        } else if (y > size - 1) {
            y = size;
        }
        return y * size + x;
    }
}
