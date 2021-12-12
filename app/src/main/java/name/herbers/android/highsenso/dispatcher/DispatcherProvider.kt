package name.herbers.android.highsenso.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * This Interface provide [Dispatchers] to the [DatabaseHandler] which needs it for
 * coroutines. This extra Interface is needed for testing.
 * This Interface is an example for a DispatcherProvider and was copied from CDRussell (GitHub).
 *
 * @see <a href="https://github.com/CDRussell/testing-coroutines">CDRussell</a>
 * @since 1.0
 * */
interface DispatcherProvider {

    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}