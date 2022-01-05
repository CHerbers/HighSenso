package name.herbers.android.highsenso.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import name.herbers.android.highsenso.data.AnswerSheet
import name.herbers.android.highsenso.data.Questionnaire
import timber.log.Timber
import java.io.File

/**
 * This [RoomDatabase] holds all past [Questionnaire]s and [AnswerSheet]s loaded from the webserver.
 *
 * At the first start of the HighSenso app, a new empty persistent Database will be stored on the device.
 *
 * On later starts the existing database will be used instead of storing a new one. All database
 * manipulations during the apps existence will be done on the on-device database.
 *
 * In case the on-device database will be deleted a new database will be created and stored on the
 * device like it was done on the first start of the app.
 *
 * @project HighSenso
 * @author Christoph Herbers
 * @since 1.0
 * */
@Database(entities = [Questionnaire::class, AnswerSheet::class], version = 4, exportSchema = false)
@TypeConverters(
    QuestionConverter::class,
    AnswerConverter::class,
    SensorDataConverter::class,
    ClientConverter::class
)
abstract class HighSensoDatabase : RoomDatabase() {

    abstract val highSensoDatabaseDao: HighSensoDatabaseDao

    companion object {
        private val roomDbPath =
            Environment.getDataDirectory().path + "/data/name.herbers.android.highsenso/databases/high_senso_database"
        private val roomDb = File(roomDbPath)

        @Volatile
        private var INSTANCE: HighSensoDatabase? = null
        fun getInstance(context: Context): HighSensoDatabase {
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
                        "high_senso_database already exists on this device! " +
                                "Therefore no new database will be initiated!"
                    )

                } else {
                    Timber.i(
                        "questions_database did not already exist on this device! " +
                                "A new one will be initiated based on the questions_database.db from assets!"
                    )
                }
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HighSensoDatabase::class.java,
                        "high_senso_database"
                    )
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