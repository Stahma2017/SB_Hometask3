package ru.skillbranch.skillarticles.ui.auth

import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel

class AuthFragment : BaseFragment<AuthViewModel>() {
    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_auth

    override fun setupViews() {
        btn_login.setOnClickListener {
            viewModel.hadleLogin(et_login.text.toString(), et_password.text.toString(), null)
        }
    }
}