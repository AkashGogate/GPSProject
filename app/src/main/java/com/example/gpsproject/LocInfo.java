package com.example.gpsproject;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;


public class LocInfo implements Parcelable {
    long time;
    Address address;
    Location location;

    public LocInfo(long timeElapsed, Address addressVisited, Location locationVisited){
        location = locationVisited;
        address = addressVisited;
        time = timeElapsed;
    }

    protected LocInfo(Parcel in) {
        time = in.readLong();
        address = in.readParcelable(Address.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<LocInfo> CREATOR = new Creator<LocInfo>() {
        @Override
        public LocInfo createFromParcel(Parcel in) {
            return new LocInfo(in);
        }

        @Override
        public LocInfo[] newArray(int size) {
            return new LocInfo[size];
        }
    };




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(time);
        parcel.writeParcelable(address, i);
        parcel.writeParcelable(location, i);
    }

    public String getAddress(){
        return address.getAddressLine(0);
    }
    public long getTime() {
        return time;
    }
}