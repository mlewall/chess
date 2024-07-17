package model;

import chess.ChessGame; //in order to know what a game is

//game ID
// whiteUser
// blackUser
// gameName
// ChessGame

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
