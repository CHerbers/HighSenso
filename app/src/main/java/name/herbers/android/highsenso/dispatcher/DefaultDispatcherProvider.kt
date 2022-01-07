package name.herbers.android.highsenso.dispatcher

import kotlinx.coroutines.Dispatchers
import name.herbers.android.highsenso.database.DatabaseHandler

/**
 * This Class implements the [DispatcherProvider] interface to provide [Dispatchers] to the
 * [DatabaseHandler] which needs it for coroutines.
 * This Class is an example for a DispatcherProvider and was copied from GitHub.
 * @see <a href="https://github.com/CDRussell/testing-coroutines">CDRussell</a>
 * @since 1.0
 * */
class DefaultDispatcherProvider : DispatcherProvider