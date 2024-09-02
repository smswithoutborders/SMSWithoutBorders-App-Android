package com.example.sw0b_001.Models.Platforms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class AccountsRecyclerAdapter : RecyclerView.Adapter<AccountsRecyclerAdapter.ViewHolder>() {

    val mDiffer: AsyncListDiffer<StoredPlatformsEntity> = AsyncListDiffer( this,
        PlatformsRecyclerAdapter.STORED_DIFF_CALLBACK )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context);
        val view = inflater.inflate(R.layout.layout_accounts, parent, false);
        return ViewHolder(view);
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val platform = mDiffer.currentList[position]
        holder.bind(platform)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val identifier = itemView.findViewById<MaterialTextView>(R.id.accounts_identifier)
        private val name = itemView.findViewById<MaterialTextView>(R.id.accounts_name)
        var card = itemView.findViewById<MaterialCardView>(R.id.accounts_layout_card)

        fun bind(storedPlatformsEntity: StoredPlatformsEntity) {
            identifier.text = storedPlatformsEntity.account
            name.text = storedPlatformsEntity.name
        }
    }
}