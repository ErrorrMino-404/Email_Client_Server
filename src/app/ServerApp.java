package app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.ServerController;
import server.ServerNew;

import java.util.Objects;

public class ServerApp extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/serverF.fxml"));
        Parent root = loader.load();
        stage.setTitle("Server logger");
        stage.setScene(new Scene(root, 700, 400));
        stage.show();

        ServerController controller = loader.getController();
        ServerNew sn = new ServerNew();
        controller.setModel(sn);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                sn.shutdown();
            }
        });
    }
}
