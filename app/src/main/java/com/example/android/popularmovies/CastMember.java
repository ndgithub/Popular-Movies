package com.example.android.popularmovies;

public class CastMember {
    private String actorName;
    private String characterName;
    private String profilePicPath;

    public CastMember(String actorName, String characterName,String profilePicPath) {
        this.actorName = actorName;
        this.characterName = characterName;
        this.profilePicPath = profilePicPath;
    }

    public String getActorName() {
        return actorName;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getProfilePicPath() {
        return profilePicPath;
    }
}
