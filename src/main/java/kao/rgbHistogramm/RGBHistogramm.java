package kao.rgbHistogramm;

import Utility.Devices;
import com.aparapi.Range;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RGBHistogramm extends Application {
    public static void main(String[] args) {
        launch();
    }

    //private static final String DATEI = "C:\\Users\\Lukas\\IdeaProjects\\KAO\\src\\main\\java\\kao\\rgbHistogramm\\lena.bmp";
    private static final String DATEI = "C:\\Users\\Lukas\\IdeaProjects\\KAO\\src\\main\\java\\kao\\kantenerkennung\\TRex.jpg";

    private static Rectangle2D screen = Screen.getPrimary().getBounds();
    private static final int HGAP = 10; // Abstand Bild/Histogramm
    private static final int MIN_HISTO_WIDTH = 256;
    private static final int MAX_HISTO_WIDTH = 512;
    private int[] r = new int[256]; // rot
    private int[] g = new int[256]; // grün
    private int[] b = new int[256]; // blau

    @Override
    public void start(Stage stage) {
        Image image = new Image(DATEI);
        int w = (int) image.getWidth();
        int h = (int) image.getHeight();
        int[] pixel = new int[w * h];
        image.getPixelReader().getPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), pixel, 0, w);
        Range range = Range.create2D(Devices.selectFirstDevice(), w, h, 16, 16);
        new RGBKernel(pixel, r, g, b).execute(range);
        ImageView view = scale(image);
        HBox hbox = new HBox(HGAP, view, histogram(view));
        stage.setScene(new Scene(hbox));
        stage.setTitle("RGB-Histogramm (" + DATEI + ")");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private ImageView scale(Image image) {
        // Skaliere das Bild so, dass es auf den Bildschirm passt
        // und für das Histogramm wenigstens die minimale Breite bleibt.
        ImageView view = new ImageView(image);
        double maxWidth = screen.getWidth() - HGAP - MIN_HISTO_WIDTH;
        double maxHeight = screen.getHeight() * 0.9;
        if (image.getWidth() > maxWidth) view.setFitWidth(maxWidth);
        if (image.getHeight() > maxHeight) view.setFitHeight(maxHeight);
        view.setPreserveRatio(true); // Seitenverhältnis sperren
        return view;
    }

    private Node histogram(ImageView view) {
        Bounds scaledImage = view.getBoundsInParent();
        double deltaW = screen.getWidth() - scaledImage.getWidth();
        double width = Math.min(deltaW, MAX_HISTO_WIDTH);
        double height = scaledImage.getHeight();
        Canvas histo = new Canvas(width, height);
        // Ermittle für die y-Skalierung den maximalen Häufigkeitswert.
        int max = Stream.of(r, g, b).flatMapToInt(IntStream::of).max().getAsInt();
        draw(histo, width / 256, height / max);
        return histo;
    }

    private void draw(Canvas histo, double xScale, double yScale) {
        GraphicsContext graphics = histo.getGraphicsContext2D();
        graphics.setLineWidth(3);
        graphics.setGlobalBlendMode(BlendMode.ADD);
        double[] x = new double[256], y = new double[256];
        Arrays.parallelSetAll(x, i -> i * xScale);
        var pairs = Map.of(r, Color.RED, g, Color.GREEN, b, Color.BLUE);
        double h = histo.getHeight();
        for (var pair : pairs.entrySet()) {
            Arrays.parallelSetAll(y, i -> h - pair.getKey()[i] * yScale);
            graphics.setStroke(pair.getValue());
            graphics.strokePolyline(x, y, 256);
        }
    }
}