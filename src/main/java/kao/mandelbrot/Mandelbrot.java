package kao.mandelbrot;

import Utility.Devices;
import com.aparapi.Range;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

public class Mandelbrot extends Application {
    private static Rectangle2D bounds = Screen.getPrimary().getBounds();
    private static final int BREITE = (int) (bounds.getWidth() * 0.9);
    private static final int HÖHE = BREITE * 9 / 16;
    private static final int ZOOM = 10;
    private double[] farbe = new double[BREITE * HÖHE];
    private double[] helligkeit = new double[BREITE * HÖHE];
    private MandelbrotKernel k = new MandelbrotKernel(farbe, helligkeit);
    // TODO nachfragen ob man die local size auch größer machen kann
    private Range range = Range.create2D(Devices.selectFirstDevice(), BREITE, HÖHE, 16, 16);
    private double x1 = -3.0;
    private double x2 = 1.8;
    private double y1 = (x2 - x1) * HÖHE / BREITE / 2;
    private double y2 = -y1;
    private int maxIter = 100; // Startwert
    private int exponent = 0; // ZOOM ^ exponent
    private WritableImage image = new WritableImage(BREITE, HÖHE);
    private WritablePixelFormat<ByteBuffer> format = PixelFormat.getByteBgraPreInstance();
    private PixelWriter pw = image.getPixelWriter();
    private PixelReader pr = image.getPixelReader();
    private Deque<byte[]> imageStack = new ArrayDeque<>();
    private Deque<double[]> frameStack = new ArrayDeque<>();
    private Stage stage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        ImageView view = new ImageView(image);
        view.setFocusTraversable(true);
        view.setOnMousePressed(this::zoomIn);
        view.setOnKeyPressed(this::zoomOut);
        zeichne();
        stage.setScene(new Scene(new Group(view)));
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private void zeichne() {
        stage.setTitle("Computing ...");
        k.setParameters(x1, y1, x2, y2, maxIter);
        k.execute(range);
        for (int i = 0; i < farbe.length; i++) {
            Color c = Color.hsb(farbe[i], 1, helligkeit[i]);
            pw.setColor(i % BREITE, i / BREITE, c);
        }
        setTitle();
    }

    private void setTitle() {
        stage.setTitle(String.format
                ("Mandelbrot (Zoom: %d^%d, n = %,d)",
                        ZOOM, exponent, maxIter));
    }

    private void zoomIn(MouseEvent e) {
        byte[] buffer = new byte[4 * BREITE * HÖHE];
        pr.getPixels(0, 0, BREITE, HÖHE, format, buffer, 0, 4 * BREITE);
        imageStack.push(buffer);
        frameStack.push(new double[]{x1, y1, x2, y2});
        double x = x1 + (x2 - x1) * e.getX() / BREITE;
        double y = y1 + (y2 - y1) * e.getY() / HÖHE;
        double dx = (x2 - x1) / ZOOM / 2;
        double dy = (y2 - y1) / ZOOM / 2;
        x1 = x - dx;
        x2 = x + dx;
        y1 = y - dy;
        y2 = y + dy;
        maxIter *= 2;
        exponent++;
        zeichne();
    }

    private void zoomOut(Object e) {
        if (!imageStack.isEmpty()) {
            double[] frame = frameStack.pop();
            x1 = frame[0];
            y1 = frame[1];
            x2 = frame[2];
            y2 = frame[3];
            maxIter /= 2;
            exponent--;
            pw.setPixels(0, 0, BREITE, HÖHE, format, imageStack.pop(), 0, 4 * BREITE);
            setTitle();
        }
    }
}
