package com.example.bike.datasources

import com.google.firebase.database.Exclude

class Route(
    var distance: String,
    var endTime: String,
    var id: String,
    var newLocLat: String,
    var newLocLong: String,
    var oldLocLat: String,
    var oldLocLong: String,
    var startTime: String,
    var userId: String,
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "distance" to distance,
            "startTime" to startTime,
            "endTime" to endTime,
            "newLocLat" to newLocLat,
            "newLocLong" to newLocLong,
            "oldLocLat" to oldLocLat,
            "oldLocLong" to oldLocLong,
            "userId" to userId
        )
    }
}