package database

import android.os.Build.VERSION.INCREMENTAL
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration

import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.Crime
@Database
    (entities = [ Crime::class ], version=3,exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase :RoomDatabase(){

    abstract fun crimeDao(): CrimeDao


}
var migration_1_2=object :Migration(1,2){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }
}
val migration_2_3 = object : Migration(2,3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE crime ADD COLUMN suspectPhone TEXT NOT NULL DEFAULT '' ")
    }
}