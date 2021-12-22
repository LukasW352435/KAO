module kao.mandelbrot.kao {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires aparapi;

    opens kao.mandelbrot to javafx.fxml;
    exports kao.mandelbrot;

    opens kao.rgbHistogramm to javafx.fxml;
    exports kao.rgbHistogramm;

    opens kao.kantenerkennung to javafx.fxml;
    exports kao.kantenerkennung;

    opens kao.gameOfLife to javafx.fxml;
    exports kao.gameOfLife;

    opens kao.rauschfilter to javafx.fxml;
    exports kao.rauschfilter;
}