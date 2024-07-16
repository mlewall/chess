package model;

public record AuthData(String authToken, String username) {}

/*note that JavaRecords are SICK because of a few reasons:
- immutability
- simple constructor
- automatic getters
- automatic equals that compares all the fields
- auto hashcodes
- auto toString
 */