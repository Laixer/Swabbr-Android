package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.*
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import kotlinx.android.synthetic.main.fragment_auth_profile_details.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AuthProfileDetailsFragment : AuthFragment() {

    private val profileVm: ProfileViewModel by sharedViewModel()
    private var profileVlogsAdapter: ProfileVlogsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileVlogsAdapter = null
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
