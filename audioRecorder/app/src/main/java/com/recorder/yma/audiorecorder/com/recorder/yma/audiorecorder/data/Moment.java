package com.recorder.yma.audiorecorder.com.recorder.yma.audiorecorder.data;

/**
 * Created by ramh on 22/06/2017.
 */

public class Moment {

    public Moment() {

    }

    public Moment(String attachment, String content) {
        super();
        setAttachment(attachment);
        setContent(content);
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMomentID() {
        return momentID;
    }

    public void setMomentID(String momentID) {
        this.momentID = momentID;
    }

    String attachment;
    String content;
    long createdAt;
    String userId;
    String momentID;

}
