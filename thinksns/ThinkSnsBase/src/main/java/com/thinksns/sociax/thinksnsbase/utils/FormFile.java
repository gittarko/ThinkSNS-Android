package com.thinksns.sociax.thinksnsbase.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

@SuppressWarnings("serial")
public class FormFile implements Serializable {
	// 定义了使用的文件的特点
	// 上传文件的数据
	private byte[] data;
	private InputStream inStream;
	// 文件名称
	private String fileName;
	// 请求参数名称
	private String Formnames;
	// 内容类型
	private String contentType = "application/octet-stream";

	public FormFile() {
		super();
	}

	public FormFile(byte[] data, String fileName, String formnames,
			String contentType) {
		this.data = data;
		this.fileName = fileName;
		Formnames = formnames;
		this.inStream = new ByteArrayInputStream(data);
		if (contentType != null)
			this.contentType = contentType;
	}

	public FormFile(InputStream inStream, String fileName, String formnames,
			String contentType) {
		this.inStream = inStream;
		this.fileName = fileName;
		Formnames = formnames;
		if (contentType != null)
			this.contentType = contentType;
	}

	public FormFile(InputStream inStream, String fileName, String formnames) {
		this(inStream, fileName, formnames, null);
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFormnames() {
		return Formnames;
	}

	public void setFormnames(String formnames) {
		Formnames = formnames;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	// @Override
	// public int describeContents() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public void writeToParcel(Parcel parcel, int arg1) {
	// // TODO Auto-generated method stub
	// parcel.writeByteArray(data);
	// parcel.writeSerializable(fileName);
	// parcel.writeSerializable(Formnames);
	// parcel.writeSerializable(contentType);
	// }
	//
	// public static final Parcelable.Creator<FormFile> CREATOR = new
	// Creator<FormFile>() {
	// @Override
	// public FormFile createFromParcel(Parcel source) {
	// FormFile app = new FormFile();
	// app.data = source.createByteArray();
	// app.fileName = source.readString();
	// app.Formnames = source.readString();
	// app.contentType = source.readString();
	// return app;
	// }
	//
	// @Override
	// public FormFile[] newArray(int size) {
	// return new FormFile[size];
	// }
	//
	// };

}
