package com.laixer.swabbr.presentation.viewmodel

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Abstract base for a view model.
 */
abstract class ViewModelBase : ViewModel() {
    /**
     *  Used for graceful resource disposal.
     */
    protected val compositeDisposable = CompositeDisposable()

    /**
     *  Called on disposal.
     */
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
