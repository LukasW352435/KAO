package kao.rauschfilter;

import Utility.Devices;
import com.aparapi.Kernel;
import com.aparapi.Range;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Rauschfilter extends javafx.application.Application {
    private static Rectangle2D bounds = Screen.getPrimary().getBounds();
    private static final int BREITE = (int) (bounds.getWidth() * 0.8);
    private static final int HÖHE = BREITE * 9 / 16;
    private static final double OMEGA = 8 * Math.PI / BREITE;
    private static final int FILTERLAENGE = 15;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        float[] noisy = new float[BREITE], clean = new float[BREITE];
        for (int i = 0; i < BREITE; i++) // verrauschter Sinus
        {
            noisy[i] = (float) (5 * Math.sin(i * OMEGA) + Math.random() - 0.5);
        }

        Kernel k = new FilterKernel(noisy, clean);
        Range range = Range.create(Devices.selectFirstDevice(), BREITE, 256);
        System.out.println(range);
        k.execute(range);

        Canvas oben = getCanvas(noisy), unten = getCanvas(clean);
        VBox vbox = new VBox(oben, unten);
        vbox.setStyle("-fx-background-color: whitesmoke");
        stage.setScene(new Scene(vbox));
        stage.setTitle("Mittelwertfilter der Ordnung " + (FILTERLAENGE - 1));
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private Canvas getCanvas(float[] curve) {
        Canvas canvas = new Canvas(BREITE, HÖHE / 2);
        var statistics = IntStream.range(0, curve.length).mapToDouble(i -> curve[i]).summaryStatistics();
        double max = statistics.getMax(), min = statistics.getMin();
        double scale = HÖHE / 2 / (max - min) * 0.9;
        double[] x = new double[BREITE], y = new double[BREITE];
        Arrays.parallelSetAll(x, i -> i);
        Arrays.parallelSetAll(y, i -> curve[i] * scale + HÖHE / 4);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setLineWidth(2);
        g.strokePolyline(x, y, BREITE);
        return canvas;
    }
}
