package com.nextinger.binancer.ui.tradehistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextinger.binancer.client.binance.dto.Trade
import com.nextinger.binancer.databinding.ItemTradeBinding

class TradeItemAdapter : RecyclerView.Adapter<TradeItemAdapter.ViewHolder>() {

    private var trades: List<Trade> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetBalance = trades[position]
        holder.bind(assetBalance)
    }

    override fun getItemCount(): Int = trades.size


    class ViewHolder(private val binding : ItemTradeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trade: Trade) {
            binding.trade = trade
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTradeBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun setTrades(newData: List<Trade>) {
        val tradeDiffUtil = TradeDiffUtil(trades, newData)
        val diffUtilResult = DiffUtil.calculateDiff(tradeDiffUtil)
        trades = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }


    class TradeDiffUtil(
        private val oldList: List<Trade>,
        private val newList: List<Trade>
    ): DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] === newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return false
        }
    }

}