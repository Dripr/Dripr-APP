package com.dripr.dripr.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dripr.dripr.R
import com.dripr.dripr.entities.Event
import kotlinx.android.synthetic.main.event_vertical_item.view.*


class EventVerticalViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.event_vertical_item, parent, false)) {

    fun bind(event: Event, click: ((event: Event) -> Unit)) {
        itemView.eventTitle.text = event.title

        Glide
            .with(itemView)
            .load(event.cover)
            .into(itemView.eventCover)

        itemView.eventCard.setOnClickListener {
            click(event)
        }
    }
}