package app.swapper.com.swapper.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import app.swapper.com.swapper.R
import app.swapper.com.swapper.databinding.UserGalleryItemBinding
import app.swapper.com.swapper.dto.Item

/**
 * Created by Deividas on 2018-04-07.
 */
class UserHorizontalGalleryAdapter
    : RecyclerView.Adapter<UserHorizontalGalleryAdapter.DataBindingViewHolder>() {

    private var inflater: LayoutInflater? = null
    private lateinit var binding: UserGalleryItemBinding

    private var dataList: List<Item>? = null
    private val selectedItems = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.user_gallery_item, parent, false)
        return DataBindingViewHolder(binding)
    }

    fun setDataList(data: List<Item>) {
        dataList = data
        notifyDataSetChanged()
    }

    override fun onViewDetachedFromWindow(holder: DataBindingViewHolder) {
        super.onViewDetachedFromWindow(holder)
        binding.unbind()
    }

    override fun onBindViewHolder(viewHolder: DataBindingViewHolder, position: Int) {
        dataList?.let {
            viewHolder.viewBinding.userItem = it[position]
            viewHolder.viewBinding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        dataList?.let {
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
        dataList?.let {
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



///-----------------------------------------------------




//class UserHorizontalGalleryAdapter(private val context : Context, private val dataList: List<Item>)
//    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val selectedItems = mutableListOf<Int>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_gallery_item, parent, false)
//        return ScanItemViewHolder(v)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val data = dataList[position]
//        val listViewHolder = holder as ScanItemViewHolder
//        listViewHolder.bind(data, selectedItems.contains(position))
//    }
//
//    override fun getItemCount(): Int {
//        return dataList.size
//    }
//
//    inner class ScanItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private var rootView : RelativeLayout = itemView.findViewById(R.id.rootLayout)
//        private var userGalleryCard : CardView = rootView.findViewById(R.id.userGalleryCard)
//        private var img: ImageView = userGalleryCard.findViewById(R.id.userGalleryImg)
//
//        internal var createdAt: TextView? = null
//
//        init {
//            userGalleryCard.setOnClickListener {
//                if (isExist(adapterPosition)) {
//                    selectedItems.remove(adapterPosition);
//                    rootView.background = null;
//                } else {
//                    rootView.setBackgroundResource(R.drawable.shadow)
//
//                }
//            }
//        }
//
//        fun bind(item: Item, isSelected: Boolean) {
//            item.images?.let {
//                if (it.isNotEmpty()) {
//                    Glide.with(context).load(Constants.serverAddress + "api/image" + File.separator + it[0]).into(img)
//                } else {
//                    Glide.with(context).load("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/No_image_3x4.svg/1024px-No_image_3x4.svg.png").into(img)
//                }
//            }
//            if (isSelected) {
//                rootView.setBackgroundResource(R.drawable.shadow)
//            } else {
//                rootView.background = null
//            }
//        }
//    }
//
//    private fun isExist(position : Int) : Boolean {
//        selectedItems.forEach {
//            if (it == position) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    fun getSelectedItemsIds() : List<Long> {
//        val list = mutableListOf<Long>()
//        selectedItems.forEach {
//            list.add(dataList[it].id)
//        }
//        return list
//    }
//
//    fun resetAllSelectableStates() {
//        selectedItems.clear()
//        notifyDataSetChanged()
//    }
//}