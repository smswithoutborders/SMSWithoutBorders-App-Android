package com.example.sw0b_001.Models.Messages

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sw0b_001.Modals.PlatformComposers.ComposeHandlers
import com.example.sw0b_001.Models.Platforms.AvailablePlatforms
import com.example.sw0b_001.Models.Platforms.Platforms
import com.example.sw0b_001.Models.Platforms.StoredPlatformsEntity
import com.example.sw0b_001.Models.Platforms._PlatformsHandler
import com.example.sw0b_001.Modules.Helpers.formatDate
import com.example.sw0b_001.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class MessagesRecyclerAdapter(private val availablePlatforms: List<AvailablePlatforms>) :
    RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder>() {
    val mDiffer: AsyncListDiffer<EncryptedContent> = AsyncListDiffer(this,
            EncryptedContent.DIFF_CALLBACK)

    var messageOnClickListener: MutableLiveData<EncryptedContent> =
        MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_cardlist_recents, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val encryptedContent = mDiffer.currentList[position]
        availablePlatforms.forEach {
            if(it.name == encryptedContent.platformName) {
                holder.bind(encryptedContent, it)
                return@forEach
            }
        }
        holder.card.setOnClickListener {
            messageOnClickListener.value = encryptedContent
        }
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.recents_card_layout)
        private val date = itemView.findViewById<MaterialTextView>(R.id.encryptedTextDate)
        private val body = itemView.findViewById<MaterialTextView>(R.id.encryptedTextSnippet)
        private val subject = itemView.findViewById<MaterialTextView>(R.id.homepage_subject)
        private val recipient = itemView.findViewById<MaterialTextView>(R.id.homepage_recipient)
        private val platformLogo = itemView.findViewById<ImageView>(R.id.recents_platform_logo)

        fun bind(messages: EncryptedContent, platforms: AvailablePlatforms) {
            val decomposed = ComposeHandlers.decompose(messages.getEncryptedContent(), platforms)

            body.text = decomposed.body

            val dateStr = formatDate(itemView.context, messages.getDate())
            date.text = dateStr

            try {
                if (platforms.logo != null) {
                    val bitmap = BitmapFactory.decodeByteArray( platforms.logo, 0,
                        platforms.logo!!.size )
                    platformLogo.setImageBitmap(bitmap)
                }

                when(platforms.service_type!!) {
                    Platforms.Type.EMAIL.type -> {
                        subject.text = decomposed.subject
                        recipient.text = decomposed.recipient

                        subject.visibility = View.VISIBLE
                        recipient.visibility = View.VISIBLE
                    }
                    Platforms.Type.MESSAGE.type -> {
                        subject.visibility = View.VISIBLE
                        subject.text = decomposed.subject

                        recipient.visibility = View.GONE
                    } else -> {
                        subject.visibility = View.GONE
                        recipient.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
