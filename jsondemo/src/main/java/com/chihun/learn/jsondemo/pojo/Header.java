package com.chihun.learn.jsondemo.pojo;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
@AutoValue
public abstract class Header{

	@SerializedName("namespace")
	public abstract String namespace();

	@SerializedName("name")
	public abstract String name();

	@SerializedName("messageId")
	public abstract String messageId();

	public static TypeAdapter<Header> typeAdapter(Gson gson) {
		return new AutoValue_Header.GsonTypeAdapter(gson);
	}
}