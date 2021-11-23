package com.nextinger.binancer.ui.ticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nextinger.binancer.data.objects.Asset
import com.nextinger.binancer.databinding.ItemTickerBinding

class AssetItemAdapter : RecyclerView.Adapter<AssetItemAdapter.ViewHolder>() {

    private var assetList: List<Asset> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val assetBalance = assetList[position]
        holder.bind(assetBalance)
    }

    override fun getItemCount(): Int = assetList.size


    class ViewHolder(private val binding: ItemTickerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asset: Asset) {
            binding.asset = asset
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTickerBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    fun setAssetList(newData: List<Asset>) {
        val diffUtilResult = DiffUtil.calculateDiff(
            AssetDiffUtil(assetList, newData)
        )
        assetList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    class AssetDiffUtil(
        private val oldList: List<Asset>,
        private val newList: List<Asset>
    ) : DiffUtil.Callback() {

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