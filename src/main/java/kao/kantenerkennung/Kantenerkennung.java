package kao.kantenerkennung;

import Utility.Devices;
import com.aparapi.Kernel;
import com.aparapi.Range;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.nio.IntBuffer;

public class Kantenerkennung extends Application {
    public static void main(String[] args) {
        launch();
    }

    private static final String DATEI = "C:\\Users\\Lukas\\IdeaProjects\\KAO\\src\\main\\java\\kao\\kantenerkennung\\TRex.jpg";
    private static Rectangle2D screen = Screen.getPrimary().getBounds();

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(vorherNachher()));
        stage.setTitle("Kantenerkennung (" + DATEI + ")");
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    private HBox vorherNachher() {
        Image input = new Image(DATEI);
        int w = (int) input.getWidth(), h = (int) input.getHeight();
        int[] in = new int[w * h], out = new int[in.length];
        WritablePixelFormat<IntBuffer> format = PixelFormat.getIntArgbInstance();
        input.getPixelReader().getPixels(0, 0, w, h, format, in, 0, w);
        Range range = Range.create2D(Devices.selectFirstDevice(), w, h,16,16);
        Kernel k = new KantenKernel(in, out, range).execute(range);
        double time = k.getExecutionTime() - k.getConversionTime();
        System.out.printf("%s\n%.0f ms\n", range, time);
        WritableImage output = new WritableImage(w, h);
        output.getPixelWriter().setPixels(0, 0, w, h, format, out, 0, w);
        return new HBox(scale(input), scale(output));
    }

    private ImageView scale(Image image) {
        ImageView view = new ImageView(image);
        double maxWidth = screen.getWidth() / 2;
        double maxHeight = screen.getHeight() * 0.9;
        if (image.getWidth() > maxWidth) view.setFitWidth(maxWidth);
        if (image.getHeight() > maxHeight) view.setFitHeight(maxHeight);
        view.setPreserveRatio(true);
        return view;
    }
}