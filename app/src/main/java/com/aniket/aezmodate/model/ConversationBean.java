package com.aniket.aezmodate.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ConversationBean {

    @PrimaryKey
    private int conversationId;

    @PrimaryKey
    private int profileId;

    @ColumnInfo(name = "profileName")
    private String profileName;

    @ColumnInfo(name = "profileImage")
    private String profileImage;

    @ColumnInfo(name = "lastMessage")
    private String lastMessage;

    @ColumnInfo(name = "isLastMessageSeen")
    private boolean isLastMessageSeen;

    @ColumnInfo(name = "lastMessageTime")
    private String lastMessageTime;

    @ColumnInfo(name = "unseenMessageCount")
    private String unseenMessageCount;

    public ConversationBean(int conversationId, int profileId, String profileName, String profileImage, String lastMessage, boolean isLastMessageSeen, String lastMessageTime, String unseenMessageCount) {
        this.conversationId = conversationId;
        this.profileId = profileId;
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.lastMessage = lastMessage;
        this.isLastMessageSeen = isLastMessageSeen;
        this.lastMessageTime = lastMessageTime;
        this.unseenMessageCount = unseenMessageCount;
    }

    public int getConversationId() {
        return conversationId;
    }
    public void setConversationId(int conversationId){this.conversationId = conversationId;}

    public int getProfileId() {
        return profileId;
    }
    public void setProfileId(int profileId){this.profileId = profileId;}



    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String profileMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean isLastMessageSeen() {
        return isLastMessageSeen;
    }

    public void setLastMessageSeen(boolean lastMessageSeen) {
        isLastMessageSeen = lastMessageSeen;
    }

    public String getUnseenMessageCount() {
        return unseenMessageCount;
    }

    public void setUnseenMessageCount(String unseenMessageCount) {
        this.unseenMessageCount = unseenMessageCount;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
