package dev.nnicrow.parser.services

import org.json.JSONObject
import kotlin.random.Random

fun sleep(): Unit {
    Thread.sleep(Random.nextLong(1200, 3700))
}

fun JSONObject.optNullableString(name: String, fallback: String? = null) : String? {
    return if (this.has(name) && !this.isNull(name)) {
        this.getString(name)
    } else {
        fallback
    }
}

fun JSONObject.optNullableBoolean(name: String, fallback: Boolean? = null) : Boolean? {
    return if (this.has(name) && !this.isNull(name)) {
        this.getBoolean(name)
    } else {
        fallback
    }
}

fun JSONObject.optNullableInt(name: String, fallback: Int? = null) : Int? {
    return if (this.has(name) && !this.isNull(name)) {
        this.getInt(name)
    } else {
        fallback
    }
}