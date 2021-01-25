package com.laixer.swabbr.presentation.vlogs.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laixer.presentation.Resource
import com.laixer.presentation.setError
import com.laixer.presentation.setLoading
import com.laixer.presentation.setSuccess
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for vlog list display.
 */
class VlogListViewModel constructor(
    private val usersVlogsUseCase: VlogUseCase,
    private val vlogUseCase: VlogUseCase
) : ViewModel() {

    val vlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()
    private val compositeDisposable = CompositeDisposable()

    /**
     *  Gets the reccomended vlogs from the data store.
     *
     *  @param refresh Force a data refresh.
     */
    fun getRecommendedVlogs(refresh: Boolean) =
        compositeDisposable.add(
            usersVlogsUseCase.getRecommendedVlogs(refresh)
                .doOnSubscribe { vlogs.setLoading() }
                .subscribeOn(Schedulers.io())
                .map { list ->
                    list.sortedByDescending { it.vlog.dateStarted }
                        .mapToPresentation()
                }
                .subscribe(
                    { vlogs.setSuccess(it) },
                    { vlogs.setError(it.message) }
                )
        )

    /**
     *  Get the amount of reactions that belong to a vlog.
     *
     *  @param vlogId The vlog to get the count for.
     */
    fun getReactionCount(vlogId: UUID) = vlogUseCase.getReactionCount(vlogId)

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
