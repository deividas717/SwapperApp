package app.swapper.com.swapper.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import app.swapper.com.swapper.R
import app.swapper.com.swapper.dto.Item
import com.bumptech.glide.Glide

/**
 * Created by Deividas on 2018-04-07.
 */
class UserHorizontalGalleryAdapter(private val context : Context, private val dataList: List<Item>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val selectedItems = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_gallery_item, parent, false)
        return ScanItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = dataList[position]
        val listViewHolder = holder as ScanItemViewHolder
        listViewHolder.bind(data, selectedItems.contains(position))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ScanItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var rootView : RelativeLayout = itemView.findViewById(R.id.rootLayout)
        private var userGalleryCard : CardView = rootView.findViewById(R.id.userGalleryCard)
        private var img: ImageView = userGalleryCard.findViewById(R.id.userGalleryImg)

        internal var createdAt: TextView? = null

        init {
            userGalleryCard.setOnClickListener {
                if (isExist(adapterPosition)) {
                    selectedItems.remove(adapterPosition);
                    rootView.background = null;
                } else {
                    rootView.setBackgroundResource(R.drawable.shadow)
                    selectedItems.add(adapterPosition);
                }
            }
        }

        fun bind(item: Item, isSelected: Boolean) {
            if (item.images?.isNotEmpty()!!) {
                Glide.with(context).load("http://192.168.1.103:8080/api/image/" + item.images[0]).into(img)
            }
            if (isSelected) {
                rootView.setBackgroundResource(R.drawable.shadow)
            } else {
                rootView.background = null
            }
        }
    }

    private fun isExist(position : Int) : Boolean {
        selectedItems.forEach {
            if (it == position) {
                return true;
            }
        }
        return false;
    }

    fun getSelectedItemsIds() : List<Long> {
        val list = mutableListOf<Long>()
        selectedItems.forEach {
            list.add(dataList[it].id)
        }
        return list
    }

    fun resetAllSelectableStates() {
        selectedItems.clear()
        notifyDataSetChanged()
    }
}