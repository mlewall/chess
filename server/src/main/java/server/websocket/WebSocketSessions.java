package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
//import webSocketMessages.Notification;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class WebSocketSessions {
    //Todo: should I make a gameID object? Like is it worth it if I need to serialize or deserialize it?
    ConcurrentHashMap<Integer, HashSet<Session>> sessionMap;
    //keeps track of which sessions are associated with each game ID.
    // A set is useful because we are avoiding duplicate connections

    WebSocketSessions() {
         sessionMap = new ConcurrentHashMap<>();
    }

    //todo: this might need to be a Game ID object. Only if it's going to be JSON at some point.
    public void addSessionToGame(int gameID, Session session){
        //then the gameID didn't exist in there; this is the first connection to a game
        if(sessionMap.get(gameID) == null){
            sessionMap.put(gameID, new HashSet<>());
            Set<Session> sessions = sessionMap.get(gameID);
            sessions.add(session);
        }
        else{
            Set<Session> sessions = sessionMap.get(gameID);
            sessions.add(session);
        }
    }

    public void removeSessionFromGame(int gameID, Session session){
        if(sessionMap.get(gameID) == null){
            //todo: return some kind of error because you're trying to join a game that doesn't exist
            //this shouldn't be allowed
        }
        else{
            Set<Session> sessions = sessionMap.get(gameID);
            boolean removed = sessions.remove(session);
            if(!removed){
                //todo: throw some kind of error because it wasn't removed.
            }
        }
    }

    public void removeSession(Session session){
        //todo: implement this
    }

    HashSet<Session> getSessionsForGame(int gameID){
        return sessionMap.get(gameID);
    }
}
