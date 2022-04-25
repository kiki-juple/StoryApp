package com.juple.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.juple.storyapp.databinding.ActivityRegisterBinding
import com.juple.storyapp.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.responseCode.observe(this) {
            viewModel.snackText.observe(this) { text ->
                if (it != 200) {
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                        .setAction("Login") {
                            Intent(this@RegisterActivity, LoginActivity::class.java)
                                .also { login ->
                                    startActivity(login)
                                }
                        }.show()
                    binding.nameEt.text.clear()
                    binding.emailEt.text.clear()
                    binding.passwordEt.text.clear()
                }
            }
        }

        setupView()
        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            btnRegister.setOnClickListener { it ->
                val name = nameEt.text.toString().trim()
                val email = emailEt.text.toString().trim()
                val password = passwordEt.text.toString().trim()
                when {
                    name.isEmpty() -> {
                        nameEt.error = "Name is required"
                    }
                    email.isEmpty() -> {
                        emailEt.error = "Email is required"
                    }
                    password.isEmpty() -> {
                        passwordEt.error = "Password is required"
                    }
                    else -> {
                        viewModel.isLoading.observe(this@RegisterActivity) { showLoading(it) }
                        viewModel.registerUser(name, email, password)
                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(it.windowToken, 0)
                        nameEt.clearFocus()
                        emailEt.clearFocus()
                        passwordEt.clearFocus()
                    }
                }
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.loadingPanel.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun setupView() {
        supportActionBar?.hide()
        binding.apply {
            passwordEt.apply {
                this.filterMinLength(6)
                this.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        val password = binding.passwordEt.text
                        binding.btnRegister.isEnabled =
                            password.toString().isNotBlank() && password != null
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }
                })
            }

        }
    }
}