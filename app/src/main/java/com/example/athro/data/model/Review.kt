package com.example.athro.data.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

class Review(val reviewer: User, val rating: Float, val text: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(User::class.java.classLoader)!!,
        parcel.readFloat(),
        parcel.readString()!!
    )

    constructor(obj: JSONObject) : this(
        User(obj.getJSONObject("reviewer")),
        obj.getDouble("rating").toFloat(),
        obj.getString("text")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(reviewer, flags)
        parcel.writeFloat(rating)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }

}
