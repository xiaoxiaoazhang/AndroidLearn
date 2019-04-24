package com.chihun.learn.jsondemo.pojo;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
@AutoValue
public abstract class Directive{

	@SerializedName("payload")
	public abstract Payload payload();

	@SerializedName("header")
	public abstract Header header();

	public static TypeAdapter<Directive> typeAdapter(Gson gson) {
		return new AutoValue_Directive.GsonTypeAdapter(gson);
	}
}