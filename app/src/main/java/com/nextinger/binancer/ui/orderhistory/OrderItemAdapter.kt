package com.nextinger.binancer.ui.orderhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextinger.binancer.client.binance.dto.Order
import com.nextinger.binancer.databinding.ItemOrderBinding

class OrderItemAdapter : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    private var orders: List<Order> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetBalance = orders[position]
        holder.bind(assetBalance)
    }

    override fun getItemCount(): Int = orders.size


    class ViewHolder(private val binding : ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.order = order
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemOrderBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun setOrders(newData: List<Order>) {
        val assetBalanceDiffUtil = OrderDiffUtil(orders, newData)
        val diffUtilResult = DiffUtil.calculateDiff(assetBalanceDiffUtil)
        orders = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    class OrderDiffUtil(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ): DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return false
        }
    }
}