package com.example.athro.data.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Log(val tuteeName: String, val hours: Double, val date: String) : Parcelable {
    constructor(tuteeName: String, hours: Double, date: Date) : this(
        tuteeName,
        hours,
        SimpleDateFormat("dd/MM/yyyy").format(date)
    )

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readString()!!
    )

    constructor(obj: JSONObject) : this(
        obj.getString("tuteeName"),
        obj.getDouble("hours"),
        obj.getString("date")
    )


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(tuteeName)
        parcel.writeDouble(hours)
        parcel.writeString(date)
    }

    companion object CREATOR : Parcelable.Creator<Log> {
        override fun createFromParcel(parcel: Parcel): Log {
            return Log(parcel)
        }

        override fun newArray(size: Int): Array<Log?> {
            return arrayOfNulls(size)
        }
    }

}