package com.laixer.swabbr.presentation.utils.todosortme

import androidx.lifecycle.MutableLiveData
import com.laixer.swabbr.utils.resources.Resource
import com.laixer.swabbr.utils.resources.ResourceState

/**
 *  Set the resource state to [ResourceState.SUCCESS].
 */
fun <T> MutableLiveData<Resource<T>>.setSuccess(data: T) =
    postValue(Resource(ResourceState.SUCCESS, data))

/**
 *  Set the resource state to [ResourceState.LOADING].
 */
fun <T> MutableLiveData<Resource<T>>.setLoading() =
    postValue(Resource(ResourceState.LOADING, value?.data))

/**
 *  Set the resource state to [ResourceState.ERROR].
 */
fun <T> MutableLiveData<Resource<T>>.setError(message: String? = null) =
    postValue(Resource(ResourceState.ERROR, value?.data, message))
