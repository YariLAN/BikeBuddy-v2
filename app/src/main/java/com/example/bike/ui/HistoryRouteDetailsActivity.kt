package com.example.bike.ui

import android.R.attr
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bike.R
import com.example.bike.databinding.ActivityHistoryRootDetailsBinding
import com.example.bike.databinding.FragmentProfileBinding
import com.example.bike.datasources.Route
import com.example.bike.repository.RouteRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.getValue
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import java.time.temporal.ChronoUnit


class HistoryRouteDetailsActivity : AppCompatActivity() {

    lateinit var historyRouteDetailsBinding : ActivityHistoryRootDetailsBinding
    private lateinit var intent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyRouteDetailsBinding = ActivityHistoryRootDetailsBinding.inflate(layoutInflater)

        setContentView(historyRouteDetailsBinding.root)

        historyRouteDetailsBinding.totalKm.text = "date.toString(format)"
        intent = getIntent()

        init()
    }

    private fun initFormatWords(n: Int, list: List<String>): String {
        return when (n) {
            11, 12, 13, 14 -> "$n ${list[2]}"
            else -> when (n % 10) {
                1 -> "$n ${list[0]}"
                2, 3, 4 -> "$n ${list[1]}"
                else -> "$n ${list[2]}"
            }
        }
    }

    private fun getDifferenceBetweenDates(startDate: String, endDate: String): Array<Int> {
        var start: LocalDateTime = LocalDateTime.parse(startDate)
        var end: LocalDateTime = LocalDateTime.parse(endDate)

        val hours = Hours.hoursBetween(start, end).hours
        val minutes = Minutes.minutesBetween(start, end).minutes
        val seconds = Seconds.secondsBetween(start, end).seconds

        return arrayOf(hours, minutes, seconds)
    }

    private fun setDifferenceFormat(startDate: String, endDate: String): String {
        val diff = getDifferenceBetweenDates(startDate, endDate)

        var h = initFormatWords(diff[0], listOf("час", "часа", "часов"))
        var m = initFormatWords(diff[1], listOf("минута", "минуты", "минут"))
        var s = initFormatWords(diff[2], listOf("секунда", "секунды", "секунд"))

        return "$h  $m  $s"
    }

    private fun getSecondsFromDate(startDate: String, endDate: String): Int {
        val date = getDifferenceBetweenDates(startDate, endDate)

        return date[0] * 3600 + date[1] * 60 + date[2]
    }

    private fun init() {
        val extras = intent.extras

        if (extras != null) {
            val value = intent.getStringExtra("routeId")

            var route : Route? = null;

            val listener =  object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val result = snapshot.children

                        val arrayOfData = arrayListOf<String>()

                        result.forEach { date ->
                            arrayOfData.add(date.value.toString())
                        }

                        route = Route(
                            arrayOfData[0],
                            arrayOfData[1],
                            arrayOfData[2],
                            arrayOfData[3],
                            arrayOfData[4],
                            arrayOfData[5],
                            arrayOfData[6],
                            arrayOfData[7],
                            arrayOfData[8]
                        );

                        val diff = setDifferenceFormat(route!!.startTime, route!!.endTime)

                        historyRouteDetailsBinding.totalKm.text = diff

                        historyRouteDetailsBinding.latStart.text = route!!.newLocLat;
                        historyRouteDetailsBinding.longStart.text = route!!.newLocLong;

                        historyRouteDetailsBinding.totalKm2.text = route!!.distance + " метров"

                        val seconds = getSecondsFromDate(route!!.startTime, route!!.endTime)

                        var speed: Double = 0.0
                        if (route!!.distance.toInt() != 0) {
                            speed = (seconds / route!!.distance.toDouble())
                        }

                        historyRouteDetailsBinding.speed.text = String.format("%.3f", speed) + " м/c"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
            }

            Firebase.database.reference.child("routes").child(value!!)
                .addListenerForSingleValueEvent(listener);
        }
    }

}