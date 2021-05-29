package com.example.athro.data.model

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONException
import org.json.JSONObject

class Tutee(
    var subjects: ArrayList<Subject>,
    var reviews: ArrayList<Review>,
    var rating: Float,
    var tutors: ArrayList<String>,
) :
    Parcelable {
    constructor() : this(
        ArrayList(), ArrayList(), 0F, ArrayList()
    )

    constructor(parcel: Parcel) : this(
    ) {
        parcel.readTypedList(subjects, Subject.CREATOR)
        parcel.readTypedList(reviews, Review.CREATOR)
        parcel.readStringList(tutors)
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
            for (i in 0 until obj.getJSONArray("tutors").length()) {
                tutors.add(obj.getJSONArray("tutors").getString(i))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(subjects)
        parcel.writeTypedList(reviews)
        parcel.writeStringList(tutors)
    }

    private fun initialiseRating() {
        rating = 0F
        reviews.forEach { review ->
            rating += review.rating
        }
        rating /= reviews.size

        if (rating.isNaN()) rating = 0F
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tutee> {
        override fun createFromParcel(parcel: Parcel): Tutee {
            return Tutee(parcel)
        }

        override fun newArray(size: Int): Array<Tutee?> {
            return arrayOfNulls(size)
        }
    }
}