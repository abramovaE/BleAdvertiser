package ru.kotofeya.bleadvertiser

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PackageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun packDao(): PackageDao

    companion object{
        @Volatile
        private var instanse: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            val tempInstanse = instanse
            if(tempInstanse != null){
                return tempInstanse
            }
            return instanse
            ?: synchronized(this){
                val inst = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "ble_adv_db")
                    .build()
                instanse = inst
                return inst
            }
        }

    }
}