package com.example.bike.repository

import android.content.Context
import android.widget.ArrayAdapter
import com.example.bike.databinding.ActivityHistoryRootDetailsBinding
import com.example.bike.databinding.FragmentProfileBinding
import com.example.bike.datasources.Route
import com.example.bike.ui.RouteAdapter
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import org.joda.time.format.DateTimeFormat


// Класс для добавления данных маршрута в базу
object RouteRepository : IRepository<Route> {

    // База данных
    private val db = Firebase.database;

    // метод добавления
    override fun addItem(item: Route) {
        try {
            // подключение к таблице route и задание нового ключа
            db.getReference("routes").child(item.id).setValue(item)
        }
        // если добавить данные не вышло
        // вызывается исключеник
        catch (ex: FirebaseException) {
            throw ex;
        }
    }

    fun getItemsAsync(userId : String, adapter: ArrayAdapter<Route>?, context: Context, bind: FragmentProfileBinding) : ArrayList<Route>? {
        val arrayList : ArrayList<Route> = arrayListOf();

        var adapterRoute = adapter

        // установка асинхронности потока
        db.reference.child("routes").orderByChild("userId").equalTo(userId).keepSynced(true)

        db.reference.child("routes").orderByChild("userId").equalTo(
            FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val result = snapshot.children!!

                // перебор данных
                // перевод из HashMap в класс Route
                result.forEach { it ->
                    val res = it.children!!
                    val arrayOfData = arrayListOf<String>()

                    res.forEach { date ->
                        arrayOfData.add(date.value.toString())
                    }

                    val route = Route(
                        arrayOfData[0],
                        arrayOfData[1],
                        arrayOfData[2],
                        arrayOfData[3],
                        arrayOfData[4],
                        arrayOfData[5],
                        arrayOfData[6],
                        arrayOfData[7],
                        arrayOfData[8])

                    arrayList.add(route)
                }

                RouteAdapter(context, arrayList).also { adapterRoute = it }

                adapterRoute!!.sort { route1, route2 ->
                    route2.startTime.compareTo(route1.startTime) // flipped for reverse order
                }

                bind.listHistoryItems.adapter = adapterRoute;
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        });

        return arrayList;
    }

    fun getItem(routeId: String?, bind: ActivityHistoryRootDetailsBinding) : Route? {
        db.reference.child("routes").child(routeId!!).keepSynced(true);

        var route : Route? = null;

        db.reference.child("routes").child(routeId!!).addValueEventListener(object : ValueEventListener {
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

                var format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm:ss");

    //            val from = org.joda.time.DateTime.parse(route!!.startTime, format).toLocalDate()
    //            val to = org.joda.time.DateTime.parse(route!!.endTime, format).toLocalDate()
    //
    //            val period = java.time.Period.

                val date = org.joda.time.LocalDateTime(route!!.endTime)

                bind.totalKm.text = date.toString(format)

                bind.latStart.text = route!!.newLocLat;
                bind.longStart.text = route!!.newLocLong;
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        });

        return route;
    }


}