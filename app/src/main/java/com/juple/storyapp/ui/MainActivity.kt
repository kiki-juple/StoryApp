package com.juple.storyapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.juple.storyapp.R
import com.juple.storyapp.adapter.LoadingStateAdapter
import com.juple.storyapp.adapter.StoryAdapter
import com.juple.storyapp.data.local.preferences.UserPreference
import com.juple.storyapp.databinding.ActivityMainBinding
import com.juple.storyapp.viewmodel.MainViewModel
import com.juple.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.actionMaps -> {
            Intent(this, MapsActivity::class.java).also {
                startActivity(it)
            }
            true
        }

        R.id.actionLogout -> {
            viewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        supportActionBar?.title = getString(R.string.app_title)
        showLoading(true)

        val adapter = StoryAdapter()
        binding.apply {
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter { adapter.retry() }
            )
        }
        viewModel.listStory.observe(this) {
            if (it == null) {
                Snackbar.make(
                    View(this),
                    "Check your internet connection",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("Retry") {
                        adapter.retry()
                    }
                    .show()
            }
            adapter.submitData(lifecycle, it)
            showLoading(false)
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
}