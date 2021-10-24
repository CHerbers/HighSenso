package name.herbers.android.highsenso.dispatcher

import kotlinx.coroutines.Dispatchers

/**
 * This Class implements the [DispatcherProvider] interface to provide [Dispatchers] to the
 * [DatabaseHandler] which needs it for coroutines.
 * This Class is an example for a DispatcherProvider and was copied from GitHub.
 * @see <a href="https://github.com/CDRussell/testing-coroutines">CDRussell</a>
 * @since 1.0
 * */
class DefaultDispatcherProvider : DispatcherProvider