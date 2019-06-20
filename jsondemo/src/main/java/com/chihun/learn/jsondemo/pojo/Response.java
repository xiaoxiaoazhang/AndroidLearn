package com.chihun.learn.jsondemo.pojo;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
@AutoValue
public abstract class Response {

	@SerializedName("directive")
	public abstract Directive directive();

	public static TypeAdapter<Response> typeAdapter(Gson gson) {
		return new AutoValue_Response.GsonTypeAdapter(gson);
	}

	@Override
	public String toString() {
		return "{" + directive() + "}";
	}
}