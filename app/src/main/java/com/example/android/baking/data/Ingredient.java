package com.example.android.baking.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kenneth on 04/12/2017.
 */

public class Ingredient implements Parcelable {

    private String name;
    private String measure;
    private String quantity;

    public Ingredient(String name, String measure, String quantity) {
        this.name = name;
        this.measure = measure;
        this.quantity = quantity;
    }

    private Ingredient(Parcel in) {
        name = in.readString();
        measure = in.readString();
        quantity = in.readString();
    }

    static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getMeasure() {
        return measure;
    }

    public String getQuantity() {
        return quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(measure);
        parcel.writeString(quantity);
    }
}
