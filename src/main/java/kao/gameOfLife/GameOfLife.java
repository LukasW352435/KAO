package kao.gameOfLife;

import Utility.Devices;
import com.aparapi.Range;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameOfLife extends Application {
    public static void main(String[] args) {
        launch();
    }

    private static Rectangle2D screen = Screen.getPrimary().getBounds();
    private static final int ZOOM = 4;
    private static final int WIDTH = ((int) (screen.getWidth() * 0.9)) / ZOOM * ZOOM;
    private static final int HEIGHT = ((int) (screen.getHeight() * 0.8)) / ZOOM * ZOOM;
    static final int DEAD = 0xff_ff_ff_ff; // weiß
    static final int ALIVE = 0xff_00_00_00; // schwarz
    private int offset;
    private Random r = new Random();
    private ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void start(Stage stage) {
        WritableImage image = new WritableImage(WIDTH, HEIGHT);
        setupAndRun(image);
        stage.setScene(new Scene(new Group(new ImageView(image))));
        stage.show();
    }

    @Override
    public void stop() {
        thread.shutdownNow();
    }

    private void setupAndRun(WritableImage image) {
        var game = new int[2 * WIDTH * HEIGHT]; // Doppelpuffer
        Arrays.parallelSetAll(game, i -> r.nextBoolean() ? DEAD : ALIVE);
        var range = Range.create2D(Devices.selectFirstDevice(), WIDTH / ZOOM, HEIGHT / ZOOM, 16,16);
        var k = new LifeKernel(game);
        var f = PixelFormat.getIntArgbInstance();
        var pw = image.getPixelWriter();
        Runnable calc = () -> {
            k.setOffset(offset);
            k.execute(range);
            offset = game.length / 2 - offset;
            Platform.runLater(() -> pw.setPixels(0, 0, WIDTH, HEIGHT, f, game, offset, WIDTH));
        };
        thread.scheduleWithFixedDelay(calc, 0, 1, TimeUnit.MILLISECONDS);
    }
}