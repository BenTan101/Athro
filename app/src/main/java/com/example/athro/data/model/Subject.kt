package com.example.athro.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.example.athro.R

enum class Subject(val type: String) : Parcelable {
    MATHEMATICS("Science"),
    BIOLOGY("Science"),
    CHEMISTRY("Science"),
    PHYSICS("Science"),
    CS("Science"),
    ENGLISH("Language"),
    HIGHER_CHINESE("Language"),
    CHINESE("Language"),
    HIGHER_MALAY("Language"),
    MALAY("Language"),
    HIGHER_TAMIL("Language"),
    TAMIL("Language"),
    HINDI("Language"),
    FRENCH("Language"),
    JAPANESE("Language"),
    INTEGRATED_HUMANITIES("Humanities"),
    ART("Humanities"),
    MUSIC("Humanities"),
    GEOGRAPHY("Humanities"),
    HISTORY("Humanities"),
    ENGLISH_LITERATURE("Humanities"),
    ECONOMICS("Humanities");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getName(fragment: Fragment): String {
        return when (this) {
            MATHEMATICS -> fragment.getString(R.string.mathematics)
            BIOLOGY -> fragment.getString(R.string.biology)
            CHEMISTRY -> fragment.getString(R.string.chemistry)
            PHYSICS -> fragment.getString(R.string.physics)
            CS -> fragment.getString(R.string.cs)
            ENGLISH -> fragment.getString(R.string.english)
            HIGHER_CHINESE -> fragment.getString(R.string.higher_chinese)
            CHINESE -> fragment.getString(R.string.chinese)
            HIGHER_MALAY -> fragment.getString(R.string.higher_malay)
            MALAY -> fragment.getString(R.string.malay)
            HIGHER_TAMIL -> fragment.getString(R.string.higher_tamil)
            TAMIL -> fragment.getString(R.string.tamil)
            HINDI -> fragment.getString(R.string.hindi)
            FRENCH -> fragment.getString(R.string.french)
            JAPANESE -> fragment.getString(R.string.japanese)
            INTEGRATED_HUMANITIES -> fragment.getString(R.string.integrated_humanities)
            ART -> fragment.getString(R.string.art)
            MUSIC -> fragment.getString(R.string.music)
            GEOGRAPHY -> fragment.getString(R.string.geography)
            HISTORY -> fragment.getString(R.string.history)
            ENGLISH_LITERATURE -> fragment.getString(R.string.english_literature)
            ECONOMICS -> fragment.getString(R.string.economics)
        }
    }

    companion object CREATOR : Parcelable.Creator<Subject> {
        override fun createFromParcel(parcel: Parcel): Subject {
            return valueOf(parcel.readString()!!)
        }

        override fun newArray(size: Int): Array<Subject?> {
            return arrayOfNulls(size)
        }

        fun getSubject(text: CharSequence?, fragment: Fragment): Subject? {
            return when (text) {
                fragment.getString(R.string.mathematics) -> MATHEMATICS
                fragment.getString(R.string.biology) -> BIOLOGY
                fragment.getString(R.string.chemistry) -> CHEMISTRY
                fragment.getString(R.string.physics) -> PHYSICS
                fragment.getString(R.string.cs) -> CS
                fragment.getString(R.string.english) -> ENGLISH
                fragment.getString(R.string.higher_chinese) -> HIGHER_CHINESE
                fragment.getString(R.string.chinese) -> CHINESE
                fragment.getString(R.string.higher_malay) -> HIGHER_MALAY
                fragment.getString(R.string.malay) -> MALAY
                fragment.getString(R.string.higher_tamil) -> HIGHER_TAMIL
                fragment.getString(R.string.tamil) -> TAMIL
                fragment.getString(R.string.hindi) -> HINDI
                fragment.getString(R.string.french) -> FRENCH
                fragment.getString(R.string.japanese) -> JAPANESE
                fragment.getString(R.string.integrated_humanities) -> INTEGRATED_HUMANITIES
                fragment.getString(R.string.art) -> ART
                fragment.getString(R.string.music) -> MUSIC
                fragment.getString(R.string.geography) -> GEOGRAPHY
                fragment.getString(R.string.history) -> HISTORY
                fragment.getString(R.string.english_literature) -> ENGLISH_LITERATURE
                fragment.getString(R.string.economics) -> ECONOMICS
                else -> null
            }
        }
    }
}
