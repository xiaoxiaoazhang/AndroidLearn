package com.chihun.learn.autovaluedemo.pojoauto;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Directive{

	@SerializedName("payload")
	private Payload payload;

	@SerializedName("header")
	private Header header;

	public void setPayload(Payload payload){
		this.payload = payload;
	}

	public Payload getPayload(){
		return payload;
	}

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	@Override
 	public String toString(){
		return 
			"Directive{" + 
			"payload = '" + payload + '\'' + 
			",header = '" + header + '\'' + 
			"}";
		}
}