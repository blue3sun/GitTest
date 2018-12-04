package com.xy.bizportdemo.model;

public abstract class Record {
	public final String SYMBOL_TAB = "\t";//写入文件的时候号码后面的\t不要去掉，不然用excel打开后电话号码会用科学记数法表示，加个\t可以避免该问题
	public static String SYMBOL_COMMA = ",";
	private int type;
	private long receiveTime;
	private long msgId;
	private String titleNumber;//情景号码
	private String phoneNumber;// 号码
	private String content;// 短信内容
	private boolean isEnterPrise;//是否是企业短信
	private long smsParseTime;
	private long recogniseParseTime;

	public static String getSymbolComma() {
		return SYMBOL_COMMA;
	}

	public static void setSymbolComma(String symbolComma) {
		Record.SYMBOL_COMMA = symbolComma;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public String getTitleNumber() {
		return titleNumber;
	}

	public void setTitleNumber(String titleNumber) {
		this.titleNumber = titleNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSmsParseTime() {
		return smsParseTime;
	}

	public void setSmsParseTime(long smsParseTime) {
		this.smsParseTime = smsParseTime;
	}

	public long getRecogniseParseTime() {
		return recogniseParseTime;
	}

	public void setRecogniseParseTime(long recogniseParseTime) {
		this.recogniseParseTime = recogniseParseTime;
	}

	public boolean isEnterPrise() {
		return isEnterPrise;
	}

	public void setEnterPrise(boolean enterPrise) {
		isEnterPrise = enterPrise;
	}
}
