package com.nextinger.binancer.ui.spotwallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextinger.binancer.data.objects.Asset
import com.nextinger.binancer.databinding.ItemBalanceBinding

class BalanceItemAdapter : RecyclerView.Adapter<BalanceItemAdapter.ViewHolder>() {

    private var assetBalanceList: List<Asset> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetBalance = assetBalanceList[position]
        holder.bind(assetBalance)
    }

    override fun getItemCount(): Int = assetBalanceList.size


    class ViewHolder(private val binding : ItemBalanceBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(asset: Asset) {
            binding.asset = asset
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBalanceBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun setAssetBalanceList(newData: List<Asset>) {
        val assetBalanceDiffUtil = AssetBalanceDiffUtil(assetBalanceList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(assetBalanceDiffUtil)
        assetBalanceList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }


    class AssetBalanceDiffUtil(
        private val oldList: List<Asset>,
        private val newList: List<Asset>
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