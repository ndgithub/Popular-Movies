package com.example.android.popularmovies.data;

public class CastMember {
    private String mActorName;
    private String mCharacterName;
    private String mProfilePicPath;

    public CastMember(String actorName, String characterName,String profilePicPath) {
        this.mActorName = actorName;
        this.mCharacterName = characterName;
        this.mProfilePicPath = profilePicPath;
    }

    public String getActorName() {
        return mActorName;
    }

    public String getCharacterName() {
        return mCharacterName;
    }

    public String getProfilePicPath() {
        return mProfilePicPath;
    }
}
