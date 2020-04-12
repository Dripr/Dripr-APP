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
import com.dripr.dripr.entities.payloads.RegisterData
import com.dripr.dripr.others.Utils.Companion.onError
import com.dripr.dripr.viewmodels.AuthViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // View Model
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Form
        form {
            input(registerFirstNameInput) {
                isNotEmpty().description("Please enter a value!")
            }
            input(registerLastNameInput) {
                isNotEmpty().description("Please enter a value!")
            }
            input(registerEmailInput) {
                isNotEmpty().description("Please enter a value!")
                isEmail().description("Please enter a valid email!")
            }
            input(registerFirstPasswordInput) {
                isNotEmpty().description("Please enter a value!")
                length().atLeast(5).description("Please choose a longer password!")
                length().atMost(25).description("Please choose a shorter password!")
            }
            input(registerSecondPasswordInput) {
                isNotEmpty().description("Please enter a value!")
            }
            submitWith(registerButton) { register() }
        }

        // Listeners
        registerHaveAccount.setOnClickListener { goToLoginActivity() }
    }

    private fun register() {
        setUiState("loading")

        val informations = getInformations()
        val registerLiveData: LiveData<User> = authViewModel.register(informations) { onError(this, it) }

        registerLiveData.observe(this, Observer { user ->
            setUiState("complete")
            User.saveToDevice(this, user)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
    }

    private fun getInformations(): RegisterData = RegisterData(
        firstName = registerFirstNameInput.text.toString(),
        lastName = registerLastNameInput.text.toString(),
        email = registerEmailInput.text.toString(),
        password = registerFirstPasswordInput.text.toString()
    )

    private fun setUiState(state: String) = when(state) {
        "loading" -> {
            registerButton.visibility = View.INVISIBLE
            registerProgress.visibility = View.VISIBLE
        }
        else -> {
            registerProgress.visibility = View.INVISIBLE
            registerButton.visibility = View.VISIBLE
        }
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}