package controller;

import app.SendMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import model.Client;
import var.Email;


import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

public class SendController implements Initializable {

    @FXML
    private Label user;
    @FXML
    private TextField newDest;
    @FXML
    private TextField newArg;
    @FXML
    private Label result;
    @FXML
    private TextArea txt;

    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = SendMail.getClient();
        user.setText(client.getUser());
    }

    public void handleSend(ActionEvent actionEvent) throws IOException {
        String[] destinatari = newDest.getText().split(",");
        boolean val=true;
        if(!Objects.equals(destinatari[0], "")){
            for (String d:destinatari) {
                if(!Email.validateEmailAddress(d)) {
                    val=false;
                    result.setText("le mail nella destinazione non sono esistenti");
                }
            }
        }else{
            val=false;
            result.setText("Non è stato inserito nessun destinatario");
        }
        if(val) {
            result.setText("");
            for (String d : destinatari) {
                Email e = new Email(0, client.getUser(), d, newArg.getText(),txt.getText(),
                        Calendar.getInstance().getTime().toString(),newDest.getText());
                client.sendTxt(e);

            }
            user.getScene().getWindow().hide();
        }
    }
}
