package com.juple.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.juple.storyapp.data.remote.User
import com.juple.storyapp.databinding.ActivityDetailBinding
import com.juple.storyapp.utils.formatTo
import com.juple.storyapp.utils.toDate

class DetailActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setData()
    }

    private fun setData() {
        val user = intent.getParcelableExtra<User>("USER") as User
        binding.apply {
            detailName.text = user.name
            detailTime.text = (user.createdAt).toDate()?.formatTo("dd MMM, YYYY 'at' HH:MM")
            Glide.with(applicationContext)
                .load(user.photoUrl)
                .into(detailPhoto)
            detailDesc.text = user.description
        }
        supportActionBar?.title = user.name
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}