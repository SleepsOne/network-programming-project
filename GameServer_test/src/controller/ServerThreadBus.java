// ổn
package controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerThreadBus {

    private final List<ClientHandler> listClientHandlers;

    public ServerThreadBus() {
        listClientHandlers = new ArrayList<>();
    }

    public List<ClientHandler> getListClientHandlers() {
        return listClientHandlers;
    }

    public void add(ClientHandler clientHandler) {
        listClientHandlers.add(clientHandler);
    }

    public void boardCast(int id, String message) {
        try {
            for (ClientHandler clientHandler : GameServer.serverThreadBus.getListClientHandlers()) {
                if (clientHandler.getClientNumber() != id) {
                    clientHandler.write(message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getLength() {
        return listClientHandlers.size();
    }

    public void sendMessageToUserID(int id, String message) throws IOException {
        try {
            for (ClientHandler clientHandler : GameServer.serverThreadBus.getListClientHandlers()) {
                if (clientHandler.getUser().getID() == id) {
                    clientHandler.write(message);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ClientHandler getClientHandlerByUserID(int ID) {
        for (int i = 0; i < GameServer.serverThreadBus.getLength(); i++) {
            if (GameServer.serverThreadBus.getListClientHandlers().get(i).getUser().getID() == ID) {
                return GameServer.serverThreadBus.listClientHandlers.get(i);
            }
        }
        return null;
    }

    public void remove(int id) {
        for (int i = 0; i < GameServer.serverThreadBus.getLength(); i++) {
            if (GameServer.serverThreadBus.getListClientHandlers().get(i).getClientNumber() == id) {
                GameServer.serverThreadBus.listClientHandlers.remove(i);
                break;
            }
        }
    }
}
