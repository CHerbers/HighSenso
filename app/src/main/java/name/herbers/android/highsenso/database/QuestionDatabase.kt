package name.herbers.android.highsenso.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import timber.log.Timber

@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionDatabase : RoomDatabase() {

    abstract val questionDatabaseDao: QuestionDatabaseDao

    companion object {
        private const val DB_PATH = "database/questions_database.db"

        @Volatile
        private var INSTANCE: QuestionDatabase? = null
        fun getInstance(context: Context): QuestionDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        QuestionDatabase::class.java,
                        "questions_database"
                    )
                        .createFromAsset(DB_PATH)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                Timber.i("Database initialized!")
                return instance
            }
        }
    }
}