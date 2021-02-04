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
    /**
     *  Used to store the result of any vlog list retrieval. Note
     *  that all list retrieval functions store their vlogs here.
     *  TODO That might be bad design.
     */
    val vlogs = MutableLiveData<Resource<List<VlogWrapperItem>>>()

    private val compositeDisposable = CompositeDisposable()

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

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
