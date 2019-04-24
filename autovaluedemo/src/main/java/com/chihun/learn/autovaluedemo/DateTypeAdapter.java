package com.chihun.learn.autovaluedemo;

import android.os.Parcel;

import com.ryanharter.auto.value.parcel.TypeAdapter;

import java.util.Date;

public class DateTypeAdapter implements TypeAdapter<Date> {

    public Date fromParcel(Parcel in) {
        return new Date(in.readLong());
    }

    public void toParcel(Date value, Parcel dest) {
        dest.writeLong(value.getTime());
    }
}
