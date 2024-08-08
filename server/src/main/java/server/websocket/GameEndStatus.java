package server.websocket;

public class GameEndStatus {
    boolean inCheck;
    boolean inCheckMate;
    boolean inStaleMate;

    GameEndStatus(){
        inCheck = false;
        inCheckMate = false;
        inStaleMate = false;
    }

    GameEndStatus(boolean inCheck, boolean inCheckMate, boolean inStaleMate){
        this.inCheck = inCheck;
        this.inCheckMate = inCheckMate;
        this.inStaleMate = inStaleMate;
    }

    public boolean isInCheck(){
        return inCheck;
    }

    public boolean isInCheckMate(){
        return inCheckMate;
    }
    public boolean isInStaleMate(){
        return inStaleMate;
    }

    public void setInCheck(boolean inCheck){
        this.inCheck = inCheck;
    }
    public void setInCheckMate(boolean inCheckMate){
        this.inCheckMate = inCheckMate;
    }

    public void setInStaleMate(boolean inStaleMate){
        this.inStaleMate = inStaleMate;
    }
}

