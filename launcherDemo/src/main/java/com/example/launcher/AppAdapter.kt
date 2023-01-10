package com.example.launcher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.launcher.databinding.AdapterAppBinding

/**
 * @create zhl
 * @date 2023/1/10 15:28
 * @description
 *
 * @update
 * @date
 * @description
 **/
class AppAdapter(val context: Context) : RecyclerView.Adapter<AppViewHolder>() {

    private var data: List<Package>? = null
    private val pm = context.packageManager

    fun setNewData(data: List<Package>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(AdapterAppBinding.inflate(LayoutInflater.from(context), parent, false).root)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.vb.apply {
            val it = data!![position]
            iv.setImageDrawable(it.icon)
            tv.text = it.label
            itemView.setOnClickListener { _ ->
                pm.LaunchIntent(it.appId)?.also {
                    context.startActivity(it)
                }
            }
        }
    }

    override fun getItemCount(): Int = data?.size ?: 0

}

class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val vb: AdapterAppBinding = AdapterAppBinding.bind(itemView)
}

