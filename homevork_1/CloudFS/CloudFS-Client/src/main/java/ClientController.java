import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    private static final String FILES_PATH = "CloudFS-Client/client-files/";

    public ListView<String> listOutput;
    public TextField textInput;
    private Net net;

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String cmd = textInput.getText();
        if (cmd.startsWith("/send")){
            String[] args = cmd.split(" ");
            if (args.length == 2) {
                try {
                    sendFile(args[1]);
                    net.sendMessage("send file: " + args[1]);
                } catch (Exception e) {
                    System.err.println("Exception file sending.");
                }
            } else {
                net.sendMessage("invalid parameters: " + cmd);
            }
        } else {
            net.sendMessage(cmd);
        }
        textInput.clear();
    }

    private void sendFile(String fileName) throws Exception {
        File f = new File(FILES_PATH + fileName);
        if (!f.exists()){
            throw new Exception("File not exists.");
        }
        FileInputStream fs = new FileInputStream(f.getPath());
        long sz = f.length();
        net.sendMessage("/takeFile");
        net.sendMessage(f.getName());
        net.sendMessage(String.valueOf(sz));
        byte[] buf = new byte[1024];
        while (fs.read(buf) > -1){
            net.sendData(buf);
        }
        fs.close();
    }

    private void saveFile(String fileName) throws IOException {
        File f = new File(FILES_PATH + fileName);
        long sz = Long.parseLong(net.getMessage());
        FileOutputStream fs = new FileOutputStream(f);
        byte[] data;
        while (sz > -1){
            data = net.getData();
            fs.write(data, 0, (int) sz);
            sz -= 1024;
        }
        fs.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            net = Net.start(8189);
            Thread thread = new Thread(() -> {
                try {
                    while (true){
                        String msg = net.getMessage();
                        if (msg.equals("/takeFiles")){
                            String files = net.getMessage();
                            String[] names = files.split(",");
                            Platform.runLater(() -> {
                                listOutput.getItems().clear();
                                listOutput.getItems().addAll(names);
                            });
                        } else if (msg.equals("/takeFile")){
                            String fn = net.getMessage();
                            saveFile(fn);
                            Platform.runLater(() -> listOutput.getItems().add("Saved: " + fn));
                        } else {
                            Platform.runLater(() -> listOutput.getItems().add(msg));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Exception while reading.");
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            System.err.println("Connection was broken.");
        }
    }
}
