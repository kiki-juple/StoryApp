package com.juple.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.juple.storyapp.data.local.database.StoryEntity
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
        val story = intent.getParcelableExtra<StoryEntity>("USER") as StoryEntity
        binding.apply {
            detailName.text = story.name
            detailTime.text = (story.createdAt).toDate()?.formatTo("dd MMM, YYYY 'at' HH:MM")
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .into(detailPhoto)
            detailDesc.text = story.description
        }
        supportActionBar?.title = story.name
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}