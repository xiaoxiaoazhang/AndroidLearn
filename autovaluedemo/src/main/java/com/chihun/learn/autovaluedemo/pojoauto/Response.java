package com.chihun.learn.autovaluedemo.pojoauto;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Response{

	@SerializedName("directive")
	private Directive directive;

	public void setDirective(Directive directive){
		this.directive = directive;
	}

	public Directive getDirective(){
		return directive;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"directive = '" + directive + '\'' + 
			"}";
		}
}