package com.example.athro.data.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

class Tutor(
    var subjects: ArrayList<Subject>,
    var reviews: ArrayList<Review>,
    var rating: Float,
    var requests: ArrayList<String>,
    var tutees: ArrayList<String>,
    var logs: ArrayList<Log>,
) :
    Parcelable {
    constructor() : this(
        ArrayList(), ArrayList(), 0F, ArrayList(), ArrayList(), ArrayList()
    )

    constructor(parcel: Parcel) : this(
    ) {
        parcel.readTypedList(subjects, Subject.CREATOR)
        parcel.readTypedList(reviews, Review.CREATOR)
        parcel.readStringList(requests)
        parcel.readStringList(tutees)
        parcel.readTypedList(logs, Log.CREATOR)
        initialiseRating()
    }

    constructor(obj: JSONObject) : this() {
        try {
            for (i in 0 until obj.getJSONArray("subjects").length()) {
                subjects.add(Subject.valueOf(obj.getJSONArray("subjects").getString(i)))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {
            for (i in 0 until obj.getJSONArray("reviews").length()) {
                reviews.add(Review(obj.getJSONArray("reviews").getJSONObject(i)!!))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        initialiseRating()

        try {
            for (i in 0 until obj.getJSONArray("requests").length()) {
                requests.add(obj.getJSONArray("requests").getString(i))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {
            for (i in 0 until obj.getJSONArray("tutees").length()) {
                tutees.add(obj.getJSONArray("tutees").getString(i))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {
            for (i in 0 until obj.getJSONArray("logs").length()) {
                logs.add(Log(obj.getJSONArray("logs").getJSONObject(i)!!))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(subjects)
        parcel.writeTypedList(reviews)
        parcel.writeStringList(requests)
        parcel.writeStringList(tutees)
        parcel.writeTypedList(logs)
    }

    override fun describeContents(): Int {
        return 0
    }

    private fun initialiseRating() {
        rating = 0F
        reviews.forEach { review ->
            rating += review.rating
        }
        rating /= reviews.size

        if (rating.isNaN()) rating = 0F
    }

    companion object CREATOR : Parcelable.Creator<Tutor> {
        override fun createFromParcel(parcel: Parcel): Tutor {
            return Tutor(parcel)
        }

        override fun newArray(size: Int): Array<Tutor?> {
            return arrayOfNulls(size)
        }
    }
}