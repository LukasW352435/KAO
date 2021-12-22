package kao.rauschfilter;

import com.aparapi.*;

public class FilterKernel extends Kernel {
    public float[] noisy;
    public float[] clean;

    public int filterLaenge = 14;

    FilterKernel(float[] noisy, float[] clean) {
        this.noisy = noisy;
        this.clean = clean;
    }

    @Override
    public void run() {
        int index = getGlobalId();

        float sum = 0;

        for (int i = filterLaenge / 2 * -1; i < filterLaenge / 2; i++) {
            // start i = -7
            // end i = +7
            if (index + i >= 0 && index + i < noisy.length) {
                sum += noisy[index + i];
            }
            // else: wenn kein wert vorhanden ist, dann sum += 0
        }

        clean[index] = sum / (filterLaenge + 1);
    }
}
