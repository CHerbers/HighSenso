package name.herbers.android.highsenso.menu

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * This is the [ViewModelProvider.Factory] for [PrivacyViewModel].
 * Creates a PrivacyViewModel.
 *
 *@project HighSenso
 *@author Herbers
 */
class PrivacyViewModelFactory(
    private val preferences: SharedPreferences,
    private val resources: Resources
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrivacyViewModel::class.java)) {
            return PrivacyViewModel(
                preferences,
                resources
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}