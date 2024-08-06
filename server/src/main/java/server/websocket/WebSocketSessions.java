package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
//import webSocketMessages.Notification;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class WebSocketSessions {
    //Todo: should I make a gameID object? Like is it worth it if I need to serialize or deserialize it?
    ConcurrentHashMap<Integer, HashMap<String, Session>> allGamesAndSessions;
    //<Integer gameID, Map<String authToken, Session session>>

    //keeps track of which sessions are associated with each game ID.

    WebSocketSessions() {
         allGamesAndSessions = new ConcurrentHashMap<>();
    }

    //todo: this might need to be a Game ID object. Only if it's going to be JSON at some point.
    public void addSessionToGame(int gameID, String authToken, Session session){
        //then the gameID didn't exist in there; this is the first connection to a game
        if(allGamesAndSessions.get(gameID) == null){
            HashMap<String, Session> sessions = new HashMap<>(); //initialize the hashmap before you put the session in it
            sessions.put(authToken, session);
            this.allGamesAndSessions.put(gameID, sessions);  //put the game in there with an empty map of sessions
        }
        //game existed in there already (so someone must be joined to it in a session already)
        else{
            HashMap<String, Session> sessions = allGamesAndSessions.get(gameID);
            sessions.put(authToken, session);
        }
    }

    public void removeUserSession(int gameID, String authToken, Session session){
        //remove ONE user from that Map
        if(allGamesAndSessions.get(gameID) == null){
            //todo: return some kind of error because you're trying to join a game that doesn't exist
            //(this shouldn't be allowed)
        }
        else{
            HashMap<String, Session> sessions = allGamesAndSessions.get(gameID);
            sessions.remove(authToken,session); //this is good because what if you're in multiple games at once?
        }
    }

    public void removeGameSession(int gameID, Session session){
        //remove whole game/ID / session grouping pair (all of it GONE)
        if(allGamesAndSessions.get(gameID) != null){
            allGamesAndSessions.remove(gameID);
        }
    }

    HashMap<String, Session> getSessionsForGame(int gameID){
        return allGamesAndSessions.get(gameID);
    }
}
