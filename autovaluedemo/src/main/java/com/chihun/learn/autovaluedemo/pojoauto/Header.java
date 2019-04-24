package com.chihun.learn.autovaluedemo.pojoauto;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Header{

	@SerializedName("namespace")
	private String namespace;

	@SerializedName("name")
	private String name;

	@SerializedName("messageId")
	private String messageId;

	public void setNamespace(String namespace){
		this.namespace = namespace;
	}

	public String getNamespace(){
		return namespace;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setMessageId(String messageId){
		this.messageId = messageId;
	}

	public String getMessageId(){
		return messageId;
	}

	@Override
 	public String toString(){
		return 
			"Header{" + 
			"namespace = '" + namespace + '\'' + 
			",name = '" + name + '\'' + 
			",messageId = '" + messageId + '\'' + 
			"}";
		}
}