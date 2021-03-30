package com.laixer.swabbr.utils.resources

sealed class ResourceState {
    /**
     *  Resource state indicating we are loading a resource from
     *  nothing. Data can be present already or not, irrelevant.
     */
    object LOADING : ResourceState()

    /**
     *  Resource state indicating a successful data get.
     */
    object SUCCESS : ResourceState()

    /**
     *  Resource state indicating an unsuccessful data get.
     */
    object ERROR : ResourceState()
}
