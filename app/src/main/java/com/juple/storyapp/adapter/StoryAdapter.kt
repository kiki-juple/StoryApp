package com.juple.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.juple.storyapp.data.remote.User
import com.juple.storyapp.databinding.ListStoryBinding
import com.juple.storyapp.ui.DetailActivity
import com.juple.storyapp.utils.formatTo
import com.juple.storyapp.utils.toDate


class StoryAdapter :
    PagingDataAdapter<User, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val onItemClickCallback: OnItemClickCallback? = null
        fun bind(user: User) {
            binding.apply {
                tvName.text = user.name
                Glide.with(binding.root)
                    .load(user.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgStory)
                tvDesc.text = user.description
                tvTime.text = (user.createdAt).toDate()?.formatTo("dd MMM, YYYY 'at' HH:MM")
            }
            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(user)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.tvName, "name"),
                        Pair(binding.tvTime, "time"),
                        Pair(binding.imgStory, "image"),
                        Pair(binding.tvDesc, "desc")
                    )

                val context = binding.root.context
                Intent(context, DetailActivity::class.java).also {
                    it.putExtra("USER", user)
                    context.startActivity(it, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListStoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}