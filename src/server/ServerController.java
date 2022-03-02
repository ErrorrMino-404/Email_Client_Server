package server;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class ServerController {
    @FXML
    private Button on;
    @FXML
    private Button off;
    @FXML
    private TableView<ServerNew.Log> logs;
    @FXML
    private TableColumn<ServerNew.Log, String> time;
    @FXML
    private TableColumn<ServerNew.Log, String> event;
    private ServerNew serverNew;
    public void setModel(ServerNew s) {
        on.setVisible(false);
        this.serverNew = s;
        time.setCellValueFactory(log->
                new SimpleStringProperty(log.getValue().getLogTime()));
        event.setCellValueFactory(log->
                new SimpleStringProperty(log.getValue().getLogEvent()));
        logs.setItems(this.serverNew.getLogs());
    }

    public void handleOn(ActionEvent actionEvent) {
        serverNew.setON();
        on.setVisible(false);
        off.setVisible(true);
    }

    public void handleOff(ActionEvent actionEvent) {
        serverNew.setOFF();
        on.setVisible(true);
        off.setVisible(false);
    }
}
