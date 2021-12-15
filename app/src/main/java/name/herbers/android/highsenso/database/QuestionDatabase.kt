package name.herbers.android.highsenso.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import timber.log.Timber
import java.io.File

/**
 * This [RoomDatabase] holds all the question and result information needed in the app.
 *
 * At the first start of the HighSenso app, a new persistent Database will be stored on the device
 * made of the *question_database.db* from the *assets* folder.
 *
 * On later starts the existing database will be used instead of storing a new one. All database
 * manipulations during the apps existence will be done on the on-device database.
 * The *questions_database.db* file will stay untouched!
 *
 * In case the on-device database will be deleted a new database will be created and stored on the
 * device like it was done on the first start of the app. In this case all user data (rating) will
 * be lost.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionDatabase : RoomDatabase() {

    abstract val questionDatabaseDao: QuestionDatabaseDao

    companion object {
        //path of the pre-populated database (questions_database.db) in assets
//        private const val DB_PATH = "database/questions_database.db"
        //create a File from an existing question_database
        private val roomDbPath =
            Environment.getDataDirectory().path + "/data/name.herbers.android.highsenso/databases/questions_database"
        private val roomDb = File(roomDbPath)

        @Volatile
        private var INSTANCE: QuestionDatabase? = null
        fun getInstance(context: Context): QuestionDatabase? {
            synchronized(this) {
                var instance = INSTANCE

                /**
                 * Checks if the db already exists on this device - if so, the app was started before and
                 * the database was already initialized. In this case the existing db will be used
                 * and no new db will be initialized from the questions_database.db file from assets.
                 * This is needed to provide the user their past question ratings.
                 * */
                if (roomDb.exists()) {
                    Timber.i(
                        "questions_database already exists on this device! " +
                                "Therefore no new database will be initiated!"
                    )
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            QuestionDatabase::class.java,
                            "questions_database"
                        )
                            .fallbackToDestructiveMigration()
                            .build()

                        INSTANCE = instance
                    }
//                } else {
//                    Timber.i(
//                        "questions_database did not already exist on this device! " +
//                                "A new one will be initiated based on the questions_database.db from assets!"
//                    )
//                    if (instance == null) {
//                        instance = Room.databaseBuilder(
//                            context.applicationContext,
//                            QuestionDatabase::class.java,
//                            "questions_database"
//                        )
//                            .createFromAsset(DB_PATH)
//                            .fallbackToDestructiveMigration()
//                            .build()
//
//                        INSTANCE = instance
//                    }
                }
                Timber.i("Database initialized!")
                return instance
            }
        }
    }

}