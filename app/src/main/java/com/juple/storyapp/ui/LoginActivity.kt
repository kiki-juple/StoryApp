package com.juple.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.juple.storyapp.databinding.ActivityLoginBinding
import com.juple.storyapp.local.UserModel
import com.juple.storyapp.local.UserPreference
import com.juple.storyapp.viewmodel.LoginViewModel
import com.juple.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
        setupView()
    }

    private fun setupView() {
        supportActionBar?.hide()
        binding.apply {
            passwordEt.apply {
//                this.filterMinLength(6)
                this.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val password = binding.passwordEt.text
                        binding.btnLogin.isEnabled =
                            password.toString().isNotBlank() && password != null
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }
                })
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
        viewModel.responseCode.observe(this) {
            if (it != 200) {
                viewModel.snackText.observe(this@LoginActivity) { text ->
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnLogin.setOnClickListener { login ->
                emailEt.clearFocus()
                passwordEt.clearFocus()
                val imm =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(login.windowToken, 0)
                viewModel.isLoading.observe(this@LoginActivity) { loading ->
                    showLoading(loading)
                }
                val email = emailEt.text.toString().trim()
                val password = passwordEt.text.toString().trim()
                viewModel.loginData(email, password)
                viewModel.responseCode.observe(this@LoginActivity) { response ->
                    when {
                        email.isEmpty() -> {
                            emailEt.error = "Email is required"
                            emailEt.requestFocus()
                        }
                        password.isEmpty() -> {
                            passwordEt.error = "Password is required"
                            passwordEt.requestFocus()
                        }
                        response == 200 -> {
                            viewModel.userLogin.observe(this@LoginActivity) { user ->
                                viewModel.saveUser(
                                    UserModel(
                                        user.userId,
                                        user.name,
                                        user.token,
                                        true
                                    )
                                )
                                NEW_API_TOKEN = user.token
                            }
                            Log.d(LoginActivity.toString(), "cek token: $NEW_API_TOKEN")
                            startActivity(
                                Intent(this@LoginActivity, MainActivity::class.java)
                                    .also { intent ->
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                            finish()
                        }
                    }
                }
            }
        }
        binding.createOne.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.loadingPanel.visibility = if (state) View.VISIBLE else View.GONE
    }

    companion object {
        var NEW_API_TOKEN = ""
    }
}
