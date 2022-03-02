package controller;

import app.ForwardMail;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Client;
import var.Email;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Objects;
import java.util.ResourceBundle;

public class ForwardController implements Initializable {
    @FXML
    private Label result;
    @FXML
    private Label txt;
    @FXML
    private Label user;
    @FXML
    private TextField newDest;

    private Client client;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client= ForwardMail.getClient();
        user.setText(client.getUser());
        txt.setText("Da: "+ForwardMail.getWho_for()+
                "->"+ForwardMail.getTxt());
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
                Email e = new Email(0, client.getUser(), d, "inoltro nuovo messaggio",txt.getText(),
                        Calendar.getInstance().getTime().toString(),newDest.getText());
                client.sendTxt(e);

            }
            user.getScene().getWindow().hide();
        }

    }
}
