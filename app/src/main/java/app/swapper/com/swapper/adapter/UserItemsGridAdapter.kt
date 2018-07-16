package app.swapper.com.swapper.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import app.swapper.com.swapper.BR
import app.swapper.com.swapper.R
import app.swapper.com.swapper.databinding.UserGalleryDetailItemBinding
import app.swapper.com.swapper.dto.Item


/**
 * Created by Deividas on 2018-05-01.
 */
class UserItemsGridAdapter: RecyclerView.Adapter<UserItemsGridAdapter.DataBindingViewHolder>(), DataPresenterInterface {

    private var inflater: LayoutInflater? = null
    private lateinit var binding: UserGalleryDetailItemBinding

    private var data: List<Item>? = null

    var test = false

    val selectedItems = mutableListOf<Long?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val inf = inflater
        if (inf != null) {
            binding = DataBindingUtil.inflate(inf, R.layout.user_gallery_detail_item, parent, false)
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
            viewHolder.viewBinding.setVariable(BR.userItem, it[position])
            viewHolder.viewBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        data?.let {
            return it.size
        }
        return 0
    }

    inner class DataBindingViewHolder(val viewBinding: UserGalleryDetailItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.squareView.setOnClickListener({
                val fadeIn = ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                val fadeOut = ScaleAnimation(0.8f, 1f, 0.8f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                fadeIn.duration = 100
                fadeIn.fillAfter = true

                fadeOut.duration = 100
                fadeOut.fillAfter = true
                if (data!![adapterPosition].id in selectedItems) {
                    selectedItems.remove(data!![adapterPosition].id)
                    it.startAnimation(fadeOut)
                } else {
                    selectedItems.add(data!![adapterPosition].id)
                    it.startAnimation(fadeIn)
                }
            })
        }
    }
}