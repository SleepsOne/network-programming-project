package controller;
import dao.UserDAO;
import java.io.IOException;



public class Room {
    private final int id;
    private final ClientHandler user1;
    private ClientHandler user2;
    private String password;
    private final UserDAO userDAO;
    private final GameRoom gameRoom;
    
    
    
    
    public Room(ClientHandler user1) {
        System.out.println("Tạo phòng thành công, ID là: " + GameServer.ROOM_ID);
        this.password = " ";
        this.id = GameServer.ROOM_ID++;
        userDAO = new UserDAO();
        this.user1 = user1;
        gameRoom = new GameRoom(id+"");
        gameRoom.addClient(user1);
        
        this.user2 = null;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }
    
    
    
    public int getId() {
        return id;
    }
    public ClientHandler getUser2() {
        return user2;
    }
    public void setUser2(ClientHandler user2) {
        this.user2 = user2;
        gameRoom.setHostId(user1.getUser().getID());
        gameRoom.addClient(user2);
        
        
    }

    public ClientHandler getUser1() {
        return user1;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNumberOfUser() {
        return user2 == null ? 1 : 2;
    }
    
    public void boardCast(String message) {
        try {
            user1.write(message);
            user2.write(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    
    public int getCompetitorID(int ID_ClientNumber) {
        if (user1.getClientNumber() == ID_ClientNumber)
            return user2.getUser().getID();
        return user1.getUser().getID();
    }

    public ClientHandler getCompetitor(int ID_ClientNumber) {
        if (user1.getClientNumber() == ID_ClientNumber)
            return user2;
        return user1;
    }

    public void setUsersToPlaying() {
        userDAO.updateToPlaying(user1.getUser().getID());
        if (user2 != null) {
            userDAO.updateToPlaying(user2.getUser().getID());
        }
    }

    public void setUsersToNotPlaying() {
        userDAO.updateToNotPlaying(user1.getUser().getID());
        if (user2 != null) {
            userDAO.updateToNotPlaying(user2.getUser().getID());
        }
    }


    public void increaseNumberOfGame() {
        userDAO.addGame(user1.getUser().getID());
        userDAO.addGame(user2.getUser().getID());
    }

    public void increaseNumberOfDraw() {
        userDAO.addDrawGame(user1.getUser().getID());
        userDAO.addDrawGame(user2.getUser().getID());
    }

    public void decreaseNumberOfGame() {
        userDAO.decreaseGame(user1.getUser().getID());
        userDAO.decreaseGame(user2.getUser().getID());
    }
    
}
