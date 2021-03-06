/*
 * Copyright 2020 Farouq Afghani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.haizo.generaladapter.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.haizo.generaladapter.loadmore.LoadMoreAdapter
import com.haizo.generaladapter.model.ListItem
import java.util.ArrayList

abstract class BaseRecyclerAdapter<M : ListItem, VH : RecyclerView.ViewHolder>(context: Context?) :
    LoadMoreAdapter<M, VH>() {

    protected var mItems: ArrayList<M> = ArrayList()
    protected var mInflater: LayoutInflater = LayoutInflater.from(context)
    protected var mItemWidth: Float? = null

    /**
     * Clear all the list and reset page number of the LoadMore
     */
    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
        resetPageNumber()
    }

    /**
     * Add item to the adapter
     * @param listItem
     */
    fun add(listItem: M) {
        mItems.add(listItem)
        notifyItemInserted(mItems.size - 1)
    }

    /**
     * Add item to the adapter in a specific index
     * @param index
     * @param listItem
     */
    fun add(index: Int, listItem: M) {
        if (index in 0 until mItems.size) {
            mItems.add(index, listItem)
            notifyItemInserted(index)
        } else add(listItem)
    }

    /**
     * Add list of items to the adapter
     * @param listItems
     */
    fun addAll(listItems: List<M>) {
        mItems.addAll(listItems)
        notifyItemRangeInserted(mItems.size, mItems.size)
    }

    /**
     * Add list of items to the adapter in a specific index
     * @param index
     * @param listItems
     */
    fun addAll(index: Int, listItems: List<M>) {
        if (index in 0 until mItems.size) {
            mItems.addAll(index, listItems)
            notifyItemRangeInserted(index, listItems.size)
        } else addAll(listItems)
    }

    /**
     * Update a specific index
     * @param index
     * @param listItem
     */
    operator fun set(index: Int, listItem: M) {
        if (index in 0 until mItems.size) {
            mItems[index] = listItem
            notifyItemChanged(index)
        }
    }

    /**
     * Update the whole list with a new list
     * @param listItems
     */
    fun updateList(listItems: List<M>?) {
        clear()
        mItems.addAll(listItems ?: ArrayList())
        notifyDataSetChanged()
    }

    /**
     * Remove listItem at index
     * @param [index]
     */
    fun remove(index: Int) {
        if (index in 0 until mItems.size) {
            mItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    /**
     * Remove the passed listItem from the main list
     * @param [listItem]
     */
    fun remove(listItem: M) {
        val indexOfItem = indexOf(listItem)
        if (indexOfItem != -1) {
            mItems.remove(listItem)
            notifyItemRemoved(indexOfItem)
        }
    }

    /**
     * Remove the passed listItems list from the main list
     * @param [items]: ListItems
     * TODO: Enhance: in case the list spreads in the main list (not ordered)
     */
    fun removeAll(items: List<M>) {
        if (items.isNotEmpty()) {
//            val startIndex = mItems.indexOf(items[0])
//            if (startIndex != -1){
//                mItems.removeAll(items)
//                notifyItemRangeRemoved(startIndex, items.count())
//            }
            mItems.removeAll(items)
            notifyDataSetChanged()
        }
    }

    /**
     * @return index of the passed item (-1 if not found)
     * @param [listItem]
     */
    fun indexOf(listItem: M): Int {
        return mItems.indexOf(listItem)
    }

    /**
     * @return true if the main list contains a specific listItem
     * @param [listItem]
     */
    fun contains(listItem: M): Boolean {
        return mItems.contains(listItem)
    }

    /**
     * perform move item from index to index
     * @param from
     * @param to
     */
    fun moveItem(from: Int, to: Int): Boolean {
        return try {
            mItems.removeAt(from)
            if (to < from) {
                mItems.add(to, mItems[from])
            } else {
                mItems.add(to - 1, mItems[from])
            }
            notifyItemMoved(from, to)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * return the Item at index
     * @param [index]
     */
    fun getItem(index: Int): M? {
        return mItems[index]
    }

    /**
     * Set the number of the items that will fit in the screen (Horizontally), so for ex, 1.5 will show 1 and (half item/quarter of 2 items).
     * Note: Any added margin to the view will not be counted in the formula
     * @param context
     * @param itemsToFit
     */
    fun setItemsToFitInScreen(context: Context, itemsToFit: Float) {
        try {
            val dm = context.resources.displayMetrics
            mItemWidth = dm.widthPixels / itemsToFit
        } catch (e: ArithmeticException) {
            e.printStackTrace()
        }
    }

    /**
     * Set the item width percentage for the screen width
     */
    fun setItemWidthPercentage(context: Context, percentage: Float) {
        val dm = context.resources.displayMetrics
        mItemWidth = dm.widthPixels * percentage
    }

    override fun getItemViewType(index: Int): Int {
        if (mItems[index].listItemType == null) {
            val message = "The listItemType in (${mItems[index].javaClass}) is NULL. Item index $index"
            Log.e(this.javaClass.name, "======== $message ======== ")
        }
        return mItems[index].listItemType?.mItemViewType ?: 0
    }

    override val items: MutableList<M>
        get() = mItems

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    // TODO: to be enhanced
    private fun enableMoveItem() {
        val itemTouchHelper =
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    return moveItem(fromPos, toPos) // true if moved, false otherwise
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // remove from adapter
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
