package com.example.sw0b_001.Models.Messages

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Modals.PlatformComposers.ComposeHandlers
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms._PlatformsHandler
import com.example.sw0b_001.Modules.Helpers.formatDate
import com.example.sw0b_001.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class MessagesRecyclerAdapter : RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder>() {
    val mDiffer: AsyncListDiffer<EncryptedContent> = AsyncListDiffer(this,
            EncryptedContent.DIFF_CALLBACK)

    var messageOnClickListener: MutableLiveData<EncryptedContent> = MutableLiveData<EncryptedContent>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_cardlist_recents, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val encryptedContent = mDiffer.currentList[position]
        holder.bind(encryptedContent, messageOnClickListener)
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card = itemView.findViewById<MaterialCardView>(R.id.recents_card_layout)
        private val date = itemView.findViewById<MaterialTextView>(R.id.recent_date)
        private val body = itemView.findViewById<MaterialTextView>(R.id.encryptedTextSnippet)
        private val subject = itemView.findViewById<MaterialTextView>(R.id.homepage_subject)
        private val recipient = itemView.findViewById<MaterialTextView>(R.id.homepage_recipient)
        private val platformLogo = itemView.findViewById<ImageView>(R.id.recents_platform_logo)
        fun bind(messages: EncryptedContent,
                 messageOnClickListener: MutableLiveData<EncryptedContent>) {
            val platforms = _PlatformsHandler.getPlatform(itemView.context,
                    messages.getPlatformName())

            val decomposed = ComposeHandlers.decompose(messages.getEncryptedContent(), platforms)

            body.text = decomposed.body

            val dateStr = formatDate(itemView.context, messages.getDate())
            date.text = dateStr

            try {
                platformLogo.setImageResource(_PlatformsHandler.hardGetLogoByName(platforms.name))

                when(platforms.type) {
                    Platforms.TYPE_EMAIL -> {
                        subject.text = decomposed.subject
                        recipient.text = decomposed.recipient

                        subject.visibility = View.VISIBLE
                        recipient.visibility = View.VISIBLE
                    } else -> {
                        subject.visibility = View.GONE
                        recipient.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            card.setOnClickListener {
                messageOnClickListener.value = messages
            }
        }
    }
}
