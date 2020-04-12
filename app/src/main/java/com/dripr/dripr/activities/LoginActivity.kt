package com.dripr.dripr.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.vvalidator.form
import com.dripr.dripr.R
import com.dripr.dripr.entities.User
import com.dripr.dripr.entities.payloads.LoginData
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View Model
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Form
        form {
            input(loginEmailInput) {
                isNotEmpty().description("Please enter a value!")
                isEmail().description("Please enter a valid email!")
            }
            input(loginPasswordInput) {
                isNotEmpty().description("Please enter a password!")
                length().atLeast(5).description("Please choose a longer password!")
                length().atMost(25).description("Please choose a shorter password!")
            }
            submitWith(loginButton) { login() }
        }

        // Listeners
        loginNoAccount.setOnClickListener { goToRegisterActivity() }
    }

    private fun login() {
        setUiState("loading")

        val credentials: LoginData = getCredentials()
        val loginLiveData: LiveData<User> = authViewModel.login(credentials) { onError(this, it) }

        loginLiveData.observe(this, Observer { user: User ->
            setUiState("complete")
            User.saveToDevice(this, user)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
    }

    private fun getCredentials(): LoginData = LoginData(
        email = loginEmailInput.text.toString(),
        password = loginPasswordInput.text.toString()
    )

    private fun setUiState(state: String): Unit = when(state) {
        "loading" -> {
            loginButton.visibility = View.INVISIBLE
            loginProgress.visibility = View.VISIBLE
        }
        else -> {
            loginProgress.visibility = View.INVISIBLE
            loginButton.visibility = View.VISIBLE
        }
    }

    private fun goToRegisterActivity() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}

