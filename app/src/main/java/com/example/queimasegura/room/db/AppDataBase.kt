package com.example.queimasegura.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.queimasegura.room.dao.AuthDao
import com.example.queimasegura.room.dao.ControllerDao
import com.example.queimasegura.room.dao.FireDao
import com.example.queimasegura.room.dao.ZipCodeDao
import com.example.queimasegura.room.dao.ReasonDao
import com.example.queimasegura.room.dao.StatusDao
import com.example.queimasegura.room.dao.TypeDao
import com.example.queimasegura.room.entities.Auth
import com.example.queimasegura.room.entities.Controller
import com.example.queimasegura.room.entities.Fire
import com.example.queimasegura.room.entities.ZipCode
import com.example.queimasegura.room.entities.Reason
import com.example.queimasegura.room.entities.Status
import com.example.queimasegura.room.entities.Type


@Database(entities = [Auth::class, Controller::class, Reason::class, Type::class, Status::class, ZipCode::class, Fire::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun controllerDao(): ControllerDao
    abstract fun reasonDao(): ReasonDao
    abstract fun typeDao(): TypeDao
    abstract fun statusDao(): StatusDao
    abstract fun zipCodeDao(): ZipCodeDao
    abstract fun fireDao(): FireDao

    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}