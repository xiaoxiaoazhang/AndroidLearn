package com.chihun.learn.autovaluedemo;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.ryanharter.auto.value.parcel.ParcelAdapter;

import java.util.Date;

@AutoValue
public abstract class User implements Parcelable {
    public abstract String name();
    public abstract int age();
    @ParcelAdapter(DateTypeAdapter.class)
    public abstract Date date();

    public static User create(String name, int age, Date date) {
        return builder()
                .name(name)
                .age(age)
                .date(date)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_User.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(String name);

        public abstract Builder date(Date date);

        public abstract Builder age(int age);

        public abstract User build();
    }
}
