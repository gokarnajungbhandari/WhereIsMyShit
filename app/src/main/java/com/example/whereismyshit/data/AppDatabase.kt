package com.example.whereismyshit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Shit::class], // all the tables
    version = 2 // if you added a column on a table, you increase the version and migrate the data from the old database version to new
)
abstract class AppDatabase : RoomDatabase() { // Creating a Room database

    abstract fun shitDao(): ShitDao // Returns the Dao so you can call it's functions

    companion object { // can call from the class itself instead of calling from the object

        @Volatile
        private var INSTANCE: AppDatabase? = null /* INSTANCE holds the database after it is created initial value inside the database is null*/


        fun getDatabase(context: Context): AppDatabase {
            /*Imagine different parts of your app did this:
                    Screen A
                       ↓
                    creates AppDatabase #1

                    Screen B
                       ↓
                    creates AppDatabase #2

                    ViewModel
                       ↓
                    creates AppDatabase #3
            You don't want lots of database instances/connections unnecessarily.
            Instead:
                                    AppDatabase
                                         ↑
                                         │
                                  ONE INSTANCE
                                 /       |       \
                                /        |        \
                           Screen A   Screen B   ViewModel
            */

            return INSTANCE ?: synchronized(this) {/* If we've already created the database, return it.
                                                        Otherwise, enter this block and create it.
                                                        synchronised basically allows only one thread
                                                        into that critical section at a time
                                                        without synchronised, multiple threads could try to create
                                                        the instance of the database at the same time*/


                val instance = Room.databaseBuilder(
                    context.applicationContext, /*This gives Room access to the Android application environment.
                                                For example, Room needs Android's filesystem to store the SQLite database.*/
                    AppDatabase::class.java, /*You're telling Room: Build the database described by my AppDatabase class.*/
                    "shit_database" //Database name
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}