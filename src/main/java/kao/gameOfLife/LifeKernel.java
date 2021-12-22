package kao.gameOfLife;

import com.aparapi.Kernel;

public class LifeKernel extends Kernel {
    public int[] game;
    public int offset;

    public LifeKernel(int[] game) {
        this.game = game;
    }

    @Override
    public void run() {

    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
