package com.laixer.swabbr.presentation.vlogs.list

import androidx.lifecycle.MutableLiveData
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.presentation.utils.todosortme.setError
import com.laixer.swabbr.presentation.utils.todosortme.setLoading
import com.laixer.swabbr.presentation.utils.todosortme.setSuccess
import com.laixer.swabbr.domain.usecase.VlogUseCase
import com.laixer.swabbr.presentation.model.VlogWrapperItem
import com.laixer.swabbr.presentation.model.mapToPresentation
import com.laixer.swabbr.presentation.viewmodel.ViewModelBase
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 *  View model for vlog list display.
 */
class VlogListViewModel constructor(
    private val usersVlogsUseCase: VlogUseCase,
    private val vlogUseCase: VlogUseCase
) : ViewModelBase() {
    /**
     *  Used as resource to store vlogs. Note that all get list
     *  functions use this as their storage target.
     */
    val vlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()

    // TODO Implement optional vlog wrapper? Bind to backend? Probably that last one.

    /**
     *  Gets the recommended vlogs and stores them in [vlogs].
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
     *  Gets vlogs for a user and stores them in [vlogs].
     *
     *  @param userId The user to get the vlogs for.
     *  @param refresh Force a data refresh.
     */
    fun getVlogsForUser(userId: UUID, refresh: Boolean = false) =
        compositeDisposable.add(
            vlogUseCase.getAllForUser(userId, refresh)
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
}
