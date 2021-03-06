package com.leopold.mvp.ui.main

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.leopold.mvp.R
import com.leopold.mvp.model.repository.Repository
import com.leopold.mvp.ui.widget.recycler.BaseRecyclerAdapter
import com.leopold.mvp.ui.widget.recycler.BaseViewHolder
import com.leopold.mvp.ui.widget.recycler.OnItemClickListener
import com.leopold.mvp.ui.widget.recycler.RecyclerViewType
import kotlinx.android.synthetic.main.layout_repository_item.view.*

/**
 * @author Leopold
 */
class RepositoryRecyclerAdapter constructor(context: Context, repositories: ArrayList<Repository>) : BaseRecyclerAdapter<Repository>(context, repositories) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            RecyclerViewType.VIEW_TYPE_ITEM -> {
                RepositoryViewHolder(inflater.inflate(R.layout.layout_repository_item, parent, false), listener)
            }

            else -> throw UnsupportedOperationException("Unsupported viewType : $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == RecyclerViewType.VIEW_TYPE_ITEM) {
            onBindRepositoryViewHolder(holder as RepositoryViewHolder, position)
        }
    }

    private fun onBindRepositoryViewHolder(holder: RepositoryViewHolder, position: Int) {
        getItem(position).run {
            setName(holder, position, this)
            setDescription(holder, this)
        }
    }

    private fun setName(holder: RepositoryViewHolder, position: Int, repository: Repository) {
        holder.name.text = position.inc().toString().plus(". ").plus(repository.name)
    }

    private fun setDescription(holder: RepositoryViewHolder, repository: Repository) {
        if (repository.description.isNullOrEmpty()) {
            holder.description.text = ""
        } else {
            holder.description.text = repository.description
        }
    }

    class RepositoryViewHolder(view: View, listener: OnItemClickListener?) : BaseViewHolder(view) {
        val name: TextView = view.repository_item_name
        val description: TextView = view.repository_item_description

        init {
            setOnItemClickListener(view, listener)
        }
    }
}