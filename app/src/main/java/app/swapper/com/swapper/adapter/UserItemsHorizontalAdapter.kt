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

    var state: State = State.SEND
        set(value)  {
            field = value
            if (value == State.EDIT) {
                Log.d("ASDUASDSDSd", "ATNAUJINAMA " + selectedItemsTest.size + " " + selectedItems.size)
                selectedItems.removeAll(selectedItemsTest)

                selectedItems.forEach {
                    Log.d("ASDUASDSDSd", "selectedItems " + it)
                }
            }
            selectedItemsTest.clear()
        }

    val selectedItemsId: List<Long>
        get() {
            val list = mutableListOf<Long>()
            data?.let { item ->
                val collection = if (state != State.SEND) selectedItemsTest else selectedItems
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

//            when (state) {
//                State.EDIT -> {
//                    val isExistInMainCollection = position in selectedItems
//                    if (isExistInMainCollection) {
//                        viewHolder.viewBinding.rootLayout.foregroundImg.visibility = View.VISIBLE
//                        viewHolder.viewBinding.rootLayout.background = null
//                    } else {
//                        viewHolder.viewBinding.rootLayout.foregroundImg.visibility = View.GONE
//                        viewHolder.viewBinding.rootLayout.background = null
//                    }
//
//                    val isExistInSecondCollection = position in selectedItemsTest
//                    if (isExistInSecondCollection) {
//                        viewHolder.viewBinding.rootLayout.setBackgroundResource(R.drawable.select_shadow)
//                    } else {
//                        viewHolder.viewBinding.rootLayout.background = null
//                    }
//                }
//                State.DELETE -> {
//                    val isExistInMainCollection = isExist(position, selectedItems)
//                    if (!isExistInMainCollection) {
//                        viewHolder.viewBinding.rootLayout.foregroundImg.visibility = View.VISIBLE
//                        viewHolder.viewBinding.rootLayout.background = null
//                    } else {
//                        viewHolder.viewBinding.rootLayout.foregroundImg.visibility = View.GONE
//                        viewHolder.viewBinding.rootLayout.background = null
//                    }
//                    val isExistInSecondCollection = isExist(position, selectedItemsTest)
//                    if (isExistInSecondCollection) {
//                        viewHolder.viewBinding.rootLayout.setBackgroundResource(R.drawable.unselect_shadow)
//                    } else {
//                        viewHolder.viewBinding.rootLayout.background = null
//                    }
//                }
//                State.SEND -> {
//                    val isExistInMainCollection = isExist(position, selectedItems)
//                    if (isExistInMainCollection) {
//                        viewHolder.viewBinding.rootLayout.setBackgroundResource(R.drawable.select_shadow)
//                    } else {
//                        viewHolder.viewBinding.rootLayout.background = null
//                    }
//                    viewHolder.viewBinding.rootLayout.foregroundImg.visibility = View.GONE
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        data?.let {
            return it.size
        }
        return 0
    }

    private fun isExist(position : Int, collection: List<Int>) : Boolean {
        return position in collection
    }

    fun resetAllSelectableStates() {
        if (selectedItems.isNotEmpty()) {
            selectedItems.clear()
            notifyDataSetChanged()
            updateFabColor(null)
        }
    }

    fun changeSelectedItemsBackground() {
        notifyDataSetChanged()
        updateFabColor(null)
    }

    inner class DataBindingViewHolder(val viewBinding: UserGalleryItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.userGalleryCard.setOnClickListener {
                when (state) {
                    State.EDIT -> {
                        val isExistInMainCollection = isExist(adapterPosition, selectedItems)
                        if (isExistInMainCollection) return@setOnClickListener

                        val isExistInSecondCollection = isExist(adapterPosition, selectedItemsTest)

                        //if (isExistInSecondCollection) return@setOnClickListener

                        if (isExistInSecondCollection) {
                            selectedItemsTest.remove(adapterPosition)
                            viewBinding.rootLayout.background = null

                        } else {
                            selectedItemsTest.add(adapterPosition)
                            viewBinding.rootLayout.setBackgroundResource(R.drawable.select_shadow)
                        }

                        updateFabColor(selectedItemsTest)
                    }
                    State.DELETE -> {
                        val isExistInMainCollection = isExist(adapterPosition, selectedItems)
                        if (!isExistInMainCollection) return@setOnClickListener

                        val isExistInSecondCollection = isExist(adapterPosition, selectedItemsTest)

                        if (isExistInSecondCollection) {
                            selectedItemsTest.remove(adapterPosition)
                            viewBinding.rootLayout.background = null
                        } else {
                            selectedItemsTest.add(adapterPosition)
                            viewBinding.rootLayout.setBackgroundResource(R.drawable.unselect_shadow)
                        }

                        updateFabColor(selectedItemsTest)
                    }
                    State.SEND -> {
                        if (isExist(adapterPosition, selectedItems)) {
                            selectedItems.remove(adapterPosition)
                            viewBinding.rootLayout.background = null
                        } else {
                            selectedItems.add(adapterPosition)
                            viewBinding.rootLayout.setBackgroundResource(R.drawable.select_shadow)
                        }

                        updateFabColor(selectedItems)
                    }
                }
            }
        }
    }

    private fun updateFabColor(collection: ObservableArrayList<Int>?) {
        if (collection == null) {
            EventBus.getDefault().post(SelectionEvent(true, state))
            return
        }
        EventBus.getDefault().post(SelectionEvent(collection.isEmpty(), state))
    }
}