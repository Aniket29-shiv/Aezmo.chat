package com.aniket.aezmodate.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmMessage;

@Entity
public class MessageBean implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "account")
    private String account;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "beSelf")
    private boolean beSelf;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "peerId")
    private String peerId;

    private String cacheFile;
    private int background;
//    private RtmClient mRtmClient;


//    private String chatTime;

    public MessageBean(String message, String userId, String peerId) {
        this.message = message;
        this.userId = userId;
        this.peerId = peerId;
    }




    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) { this.message = message; }

    public String getCacheFile() {
        return cacheFile;
    }

    public void setCacheFile(String cacheFile) {
        this.cacheFile = cacheFile;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public boolean isBeSelf() {
        return beSelf;
    }

    public void setBeSelf(boolean beSelf) {
        this.beSelf = beSelf;
    }

//    public String getChatTime() {
//        return chatTime;
//    }
//    public String setChatTime() {
//        return chatTime;
//    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }
}
