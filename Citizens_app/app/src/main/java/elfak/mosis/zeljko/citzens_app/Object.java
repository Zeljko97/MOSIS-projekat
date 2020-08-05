package elfak.mosis.zeljko.citzens_app;

import android.location.Location;

import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Object {
    public String name;
    public String category;
    public String description;
    public String longitude;
    public String latitude;
    public String UserID;
    public String date;
    @Exclude
    public String key;
    public Object(){

    }

    public Object(String name, String description, String category, String longitude, String latitude,String UserID,String date)
    {
        this.name = name;
        this.category = category;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.UserID=UserID;
        this.date=date;
    }

    public Object(String name, String description, String category)
    {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getLongitude() {
        return longitude;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getName(){
        return name;
    }
    public String getDate(){return date;}
}

