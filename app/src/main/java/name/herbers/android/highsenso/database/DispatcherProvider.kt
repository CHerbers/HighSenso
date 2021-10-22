package name.herbers.android.highsenso.database

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * This Interface and Class provide [Dispatchers] to the [DatabaseHandler] which needs it for
 * coroutines. This extra Interface/Class is needed for testing.
 * This Class is an example for a DispatcherProvider and was copied from GitHub.
 * @see <a href="https://github.com/CDRussell/testing-coroutines">CDRussell</a>
 * */
interface DispatcherProvider {

    fun main(): CoroutineDispatcher = Dispatchers.Main
    fun default(): CoroutineDispatcher = Dispatchers.Default
    fun io(): CoroutineDispatcher = Dispatchers.IO
    fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
}

class DefaultDispatcherProvider : DispatcherProvider