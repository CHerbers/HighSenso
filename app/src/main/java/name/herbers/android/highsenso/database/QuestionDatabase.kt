package name.herbers.android.highsenso.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionDatabase : RoomDatabase() {

    abstract val questionDatabaseDao: QuestionDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: QuestionDatabase? = null
        fun getInstance(context: Context): QuestionDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        QuestionDatabase::class.java,
                        "questions_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}