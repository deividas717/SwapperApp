package app.swapper.com.swapper.adapter

import android.arch.paging.PagedListAdapter
import android.databinding.DataBindingUtil
import android.location.Location
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import app.swapper.com.swapper.BR
import app.swapper.com.swapper.R
import app.swapper.com.swapper.databinding.HistoryItemBinding
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.events.OnCardClickedEvent
import app.swapper.com.swapper.events.OnItemsDelete
import kotlinx.android.synthetic.main.history_item.view.*
import org.greenrobot.eventbus.EventBus

class HistoryItemsAdapter: PagedListAdapter<Item, HistoryItemsAdapter.ItemViewHolder>(Diff) {

    private var inflater: LayoutInflater? = null
    private lateinit var binding: HistoryItemBinding
    val selectedItems = mutableListOf<Long?>()
    var isDeleteMode = false
    var location: Location? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val inf = inflater
        if (inf != null) {
            binding = DataBindingUtil.inflate(inf, R.layout.history_item, parent, false)
        }
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
        if (getItem(position)?.id in selectedItems) {
            viewHolder.viewBinding.card.checkImage.visibility = View.VISIBLE
            viewHolder.viewBinding.card.scaleX = 0.9f
            viewHolder.viewBinding.card.scaleY = 0.9f
        } else {
            viewHolder.viewBinding.card.checkImage.visibility = View.GONE
            viewHolder.viewBinding.card.scaleX = 1f
            viewHolder.viewBinding.card.scaleY = 1f
        }

        viewHolder.viewBinding.setVariable(BR.location, location)
        viewHolder.viewBinding.setVariable(BR.item, getItem(position))
        viewHolder.viewBinding.executePendingBindings()
    }

    inner class ItemViewHolder(val viewBinding : HistoryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.card.setOnClickListener {
                val itemId = viewBinding.item?.id
                if (isDeleteMode) {
                    animationSet(itemId, it)
                } else {
                    if (itemId != null) {
                        EventBus.getDefault().post(OnCardClickedEvent(itemId))
                    }
                }
            }

            viewBinding.card.setOnLongClickListener {
                isDeleteMode = true
                val itemId = viewBinding.item?.id
                animationSet(itemId, it)
                true
            }
        }
    }

    private fun animationSet(itemId: Long?, cardView: View) {
        cardView.card.scaleX = 1f
        cardView.card.scaleY = 1f
        if (itemId in selectedItems) {
            val fadeOut = ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            fadeOut.duration = 100
            fadeOut.fillAfter = true

            cardView.startAnimation(fadeOut)
            selectedItems.remove(itemId)
            cardView.checkImage.visibility = View.GONE
        } else {
            val fadeIn = ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            fadeIn.duration = 100
            fadeIn.fillAfter = true

            cardView.startAnimation(fadeIn)
            selectedItems.add(itemId)
            cardView.checkImage.visibility = View.VISIBLE
        }
        isDeleteMode = !selectedItems.isEmpty()
        EventBus.getDefault().post(OnItemsDelete(selectedItems.isEmpty()))
    }

    fun removeDataFromList() {
        selectedItems.forEach {

        }
    }

    companion object Diff : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.title == newItem.title && oldItem.description == newItem.description && oldItem.lat == newItem.lat && oldItem.lng == newItem.lng
        }
    }
}