package com.dripr.dripr.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dripr.dripr.R
import com.dripr.dripr.entities.Event
import kotlinx.android.synthetic.main.event_map_item.view.*


class EventMapViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.event_map_item, parent, false)) {

    fun bind(event: Event, click: ((event: Event) -> Unit)) {
        itemView.eventTitle.text = event.title
        itemView.eventAddress.text = event.address.placeName

        Glide
            .with(itemView)
            .load(event.cover)
            .into(itemView.eventCover)

        itemView.eventCard.setOnClickListener {
            click(event)
        }
    }
}