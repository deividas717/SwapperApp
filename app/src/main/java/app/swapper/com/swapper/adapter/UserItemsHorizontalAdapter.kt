package app.swapper.com.swapper.adapter

import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.swapper.com.swapper.R
import app.swapper.com.swapper.State
import app.swapper.com.swapper.databinding.UserGalleryItemBinding
import app.swapper.com.swapper.dto.Item
import app.swapper.com.swapper.events.SelectionEvent
import kotlinx.android.synthetic.main.user_gallery_item.view.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Deividas on 2018-04-07.
 */
class UserItemsHorizontalAdapter : RecyclerView.Adapter<UserItemsHorizontalAdapter.DataBindingViewHolder>(),
        DataPresenterInterface {

    private var inflater: LayoutInflater? = null
    private lateinit var binding: UserGalleryItemBinding

    private var data: List<Item>? = null
    val selectedItems: ObservableArrayList<Int> = ObservableArrayList()
    val selectedItemsTest: ObservableArrayList<Int> = ObservableArrayList()

    val selectedItemsId: List<Long>
        get() {
            val list = mutableListOf<Long>()
            data?.let { item ->
                val collection = selectedItems
                collection.forEach {
                    list.add(item[it].id)
                }
            }
            return list
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val inf = inflater
        if (inf != null) {
            binding = DataBindingUtil.inflate(inf, R.layout.user_gallery_item, parent, false)
        }
        return DataBindingViewHolder(binding)
    }

    override fun setDataList(data: List<Item>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onViewDetachedFromWindow(holder: DataBindingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        binding.unbind()
    }

    override fun onBindViewHolder(viewHolder: DataBindingViewHolder, position: Int) {
        data?.let {
            viewHolder.viewBinding.userItem = it[position]
            viewHolder.viewBinding.executePendingBindings()
            val isExistInMainCollection = position in selectedItems
            if (isExistInMainCollection) {
                viewHolder.viewBinding.rootLayout.setBackgroundResource(R.drawable.select_shadow)
            } else {
                viewHolder.viewBinding.rootLayout.background = null
            }
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    private fun isExist(position : Int, collection: List<Int>) : Boolean {
        return position in collection
    }

    fun resetAllSelectableStates() {
        if (selectedItems.isNotEmpty()) {
            selectedItems.clear()
            notifyDataSetChanged()
        }
    }

    fun changeSelectedItemsBackground() {
        notifyDataSetChanged()
       // updateFabColor(null)
    }

    inner class DataBindingViewHolder(val viewBinding: UserGalleryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.userGalleryCard.setOnClickListener {
                val isExistInMainCollection = isExist(adapterPosition, selectedItems)
                if (isExistInMainCollection) {
                    selectedItems.remove(adapterPosition)
                    viewBinding.rootLayout.background = null
                } else {
                    selectedItems.add(adapterPosition)
                    viewBinding.rootLayout.setBackgroundResource(R.drawable.select_shadow)
                }
                EventBus.getDefault().post(SelectionEvent(selectedItems.isNotEmpty()))
            }
        }
    }
}