package com.juple.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.juple.storyapp.R
import com.juple.storyapp.adapter.StoryAdapter
import com.juple.storyapp.databinding.ActivityMainBinding
import com.juple.storyapp.local.UserPreference
import com.juple.storyapp.viewmodel.MainViewModel
import com.juple.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.actionLogout -> {
            viewModel.logout()
            startActivity(
                Intent(this, LoginActivity::class.java).also {
                    it.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        supportActionBar?.title = "Dicoding Story"

        adapter = StoryAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStory.layoutManager = layoutManager
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.snackText.observe(this) {
            snackBarText(it)
        }

        viewModel.listStory.observe(this) {
            if (it != null) adapter.updateList(it)
        }
    }

    private fun setupAction() {
        binding.fabAdd.setOnClickListener {
            Intent(this, UploadActivity::class.java).run {
                startActivity(this)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.loadingPanel.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun snackBarText(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }
}