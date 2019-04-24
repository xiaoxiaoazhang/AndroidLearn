package com.chihun.learn.jsondemo.pojo;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
@AutoValue
public abstract class Payload{

	public static TypeAdapter<Payload> typeAdapter(Gson gson) {
		return new AutoValue_Payload.GsonTypeAdapter(gson);
	}
}