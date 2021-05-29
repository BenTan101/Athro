package com.example.athro.data.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

class User(
    val name: String?,
    val email: String?,
    val year: Int,
    var tutor: Tutor?,
    var tutee: Tutee?,
) : Parcelable {
    constructor() : this(
        "",
        "",
        "0",
        false,
        false,
    )

    constructor(name: String, email: String, year: Int) : this(
        name,
        email,
        year,
        null,
        null
    )

    constructor(
        name: String,
        email: String,
        year: String,
        isTutor: Boolean,
        isTutee: Boolean
    ) : this(
        name,
        email,
        year.toInt(),
        if (isTutor) Tutor() else null,
        if (isTutee) Tutee() else null,
    )

    constructor(name: String, email: String, year: Int, tutor: Tutor) : this(
        name,
        email,
        year,
        tutor,
        null,
    )

    constructor(name: String, email: String, year: Int, tutee: Tutee) : this(
        name,
        email,
        year,
        null,
        tutee,
    )

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readParcelable(Tutor::class.java.classLoader),
        parcel.readParcelable(Tutee::class.java.classLoader),
    )

    constructor(obj: JSONObject) : this(
        obj.getString("name"),
        obj.getString("email"),
        obj.getInt("year"),
    ) {
        try {
            tutor = Tutor(obj.getJSONObject("tutor"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        try {
            tutee = Tutee(obj.getJSONObject("tutee"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeInt(year)
        parcel.writeParcelable(tutor, flags)
        parcel.writeParcelable(tutee, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is User) return false
        return this.email == other.email
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + year
        result = 31 * result + (tutor?.hashCode() ?: 0)
        result = 31 * result + (tutee?.hashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}