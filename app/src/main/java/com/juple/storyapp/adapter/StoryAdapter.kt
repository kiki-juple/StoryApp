package com.juple.storyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.juple.storyapp.databinding.ListStoryBinding
import com.juple.storyapp.remote.User
import com.juple.storyapp.ui.DetailActivity
import com.juple.storyapp.utils.formatTo
import com.juple.storyapp.utils.toDate


class StoryAdapter : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private var oldStoryList = emptyList<User>()

    inner class DiffCallback(
        private val oldList: List<User>,
        private val newList: List<User>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition].name == newList[newItemPosition].name -> {
                    false
                }
                oldList[oldItemPosition].description == newList[newItemPosition].description -> {
                    false
                }
                oldList[oldItemPosition].photoUrl == newList[newItemPosition].photoUrl -> {
                    false
                }
                oldList[oldItemPosition].createdAt == newList[newItemPosition].createdAt -> {
                    false
                }
                else -> true
            }
        }
    }

    fun updateList(newStoryList: List<User>) {
        val diffUtil = DiffCallback(oldStoryList, newStoryList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldStoryList = newStoryList
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private var binding: ListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                tvName.text = user.name
                Glide.with(binding.root)
                    .load(user.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
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
        holder.bind(oldStoryList[position])
    }

    override fun getItemCount(): Int = oldStoryList.size

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}