package com.laixer.swabbr.presentation.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.laixer.presentation.Resource
import com.laixer.presentation.ResourceState
import com.laixer.swabbr.R
import com.laixer.swabbr.presentation.AuthFragment
import com.laixer.swabbr.presentation.model.UserItem
import kotlinx.android.synthetic.main.fragment_auth_profile_details.*

class AuthProfileDetailsFragment : AuthFragment() {

    private lateinit var user_original: UserItem
    private lateinit var user_copy: UserItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        authUserVm.user.observe(viewLifecycleOwner, Observer { updateProfile(it) })
        return inflater.inflate(R.layout.fragment_auth_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkChanges()

        firstNameInput.doOnTextChanged { text, _, _, _ ->
            if (!::user_copy.isInitialized) return@doOnTextChanged

            user_copy = user_copy.apply {
                firstName = if (text?.isNotBlank() == true) text.toString() else null
            }
            checkChanges()
        }

        lastNameInput.doOnTextChanged { text, _, _, _ ->
            if (!::user_copy.isInitialized) return@doOnTextChanged

            user_copy = user_copy.apply {
                lastName = if (text?.isNotBlank() == true) text.toString() else null
            }
            checkChanges()
        }

        nicknameInput.doOnTextChanged { text, _, _, _ ->
            if (!::user_copy.isInitialized) return@doOnTextChanged

            user_copy = user_copy.apply {
                nickname = if (text?.isNotBlank() == true) text.toString() else ""
            }
            checkChanges()
        }

        privateSwitch.setOnCheckedChangeListener { _, _ ->
            if (!::user_copy.isInitialized) return@setOnCheckedChangeListener

            user_copy = user_copy.apply {
                isPrivate = privateSwitch.isChecked
            }
            checkChanges()
        }

        applyButton.setOnClickListener {
            if (!::user_copy.isInitialized) return@setOnClickListener

            applyButton.isEnabled = false
            authUserVm.updateSelf(user_copy)
        }
    }

    private fun checkChanges() {
        applyButton.isEnabled =
            if (!::user_original.isInitialized) false
            else user_original.equals(user_copy).not()
    }

    fun setFormValues(userItem: UserItem) {
        firstNameInput.setText(userItem.firstName)
        lastNameInput.setText(userItem.lastName)
        nicknameInput.setText(userItem.nickname)
        privateSwitch.isChecked = userItem.isPrivate

        checkChanges()
    }

    private fun updateProfile(res: Resource<UserItem>) {
        when (res.state) {
            ResourceState.LOADING -> {
                checkChanges()
                // TODO: Loading state
            }
            ResourceState.SUCCESS -> {
                res.data?.let { user ->
                    user_original = user.copy()
                    user_copy = user.copy()
                    setFormValues(user)
                }
            }
            ResourceState.ERROR -> {
                checkChanges()
                Toast.makeText(requireActivity(), res.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    internal companion object {
        const val TAG = "AuthProfileFragment"
    }
}
