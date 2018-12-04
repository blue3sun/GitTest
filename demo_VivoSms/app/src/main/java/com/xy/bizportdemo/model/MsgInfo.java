package com.xy.bizportdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Classname MsgInfo
 * @Describe 短信样例的对象
 * @Author zenglanjing
 * @Time 2018/11/15 13:10
 */
public class MsgInfo implements Parcelable{
    private long msgId;// 消息id
    private String phone; // 短信接入码
    private String content;// 短信内容
    private String smsCenterNum;// 接收短信的短信中心号码
    private String scene;
    private long recieveTime;//接收时间

    public MsgInfo() {
    }

    protected MsgInfo(Parcel in) {
        msgId = in.readLong();
        phone = in.readString();
        content = in.readString();
        smsCenterNum = in.readString();
        scene = in.readString();
        recieveTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(msgId);
        dest.writeString(phone);
        dest.writeString(content);
        dest.writeString(smsCenterNum);
        dest.writeString(scene);
        dest.writeLong(recieveTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MsgInfo> CREATOR = new Creator<MsgInfo>() {
        @Override
        public MsgInfo createFromParcel(Parcel in) {
            return new MsgInfo(in);
        }

        @Override
        public MsgInfo[] newArray(int size) {
            return new MsgInfo[size];
        }
    };

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSmsCenterNum() {
        return smsCenterNum;
    }

    public void setSmsCenterNum(String smsCenterNum) {
        this.smsCenterNum = smsCenterNum;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public long getRecieveTime() {
        return recieveTime;
    }

    public void setRecieveTime(long recieveTime) {
        this.recieveTime = recieveTime;
    }
}
