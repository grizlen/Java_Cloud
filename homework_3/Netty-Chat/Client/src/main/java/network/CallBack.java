package network;

import model.Message;

public interface CallBack {
    void processMessage(Message message);
}
