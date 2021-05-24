package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Message;
import network.CallBack;
import network.Net;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public ListView<String> listOutput;
    public TextField textInput;
    private Net net;

    public void sendMessage(ActionEvent actionEvent) {
        Message message = Message.builder()
                .content(textInput.getText())
                .author("user")
                .sendAt(System.currentTimeMillis())
                .build();
        net.writeMessage(message);
        textInput.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        net = Net.getNet();
        net.setCallBack(getCallback());
        Thread thread = new Thread(net);
        thread.setDaemon(true);
        thread.start();
    }

    private CallBack getCallback() {
        return message -> Platform.runLater(() -> listOutput.getItems().add(message.getAuthor() + ": " + message.getContent()));
    }
}
