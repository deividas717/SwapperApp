package app.swapper.com.swapper.adapter

import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import app.swapper.com.swapper.R
import app.swapper.com.swapper.databinding.UserGalleryItemBinding
import app.swapper.com.swapper.dto.Item

/**
 * Created by Deividas on 2018-04-07.
 */
class UserItemsHorizontalAdapter
    : RecyclerView.Adapter<UserItemsHorizontalAdapter.DataBindingViewHolder>(), TestInterface {

    private var inflater: LayoutInflater? = null
    private lateinit var binding: UserGalleryItemBinding

    private var data: List<Item>? = null
    val selectedItems: ObservableArrayList<Int> = ObservableArrayList()

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

            if (selectedItems.contains(position)) {
                viewHolder.viewBinding.rootLayout.setBackgroundResource(R.drawable.shadow)
            } else {
                viewHolder.viewBinding.rootLayout.background = null
            }
        }
    }

    override fun getItemCount(): Int {
        data?.let {
            return it.size
        }
        return 0
    }

    private fun isExist(position : Int) : Boolean {
        selectedItems.forEach {
            if (it == position) return true
        }
        return false
    }

    fun getSelectedItemsIds() : List<Long> {
        val list = mutableListOf<Long>()
        data?.let {
            val item = it
            selectedItems.forEach {
                list.add(item[it].id)
            }
        }
        return list
    }

    fun resetAllSelectableStates() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    inner class DataBindingViewHolder(val viewBinding: UserGalleryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        init {
            viewBinding.userGalleryCard.setOnClickListener {
                if (isExist(adapterPosition)) {
                    selectedItems.remove(adapterPosition)
                    viewBinding.rootLayout.background = null
                } else {
                    selectedItems.add(adapterPosition)
                    viewBinding.rootLayout.setBackgroundResource(R.drawable.shadow)
                }
            }
        }
    }
}