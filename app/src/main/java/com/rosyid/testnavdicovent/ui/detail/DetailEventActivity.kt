@file:Suppress("unused", "DEPRECATION")

package com.rosyid.testnavdicovent.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.rosyid.testnavdicovent.R
import com.rosyid.testnavdicovent.databinding.ActivityDetailEventBinding

class DetailEventActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailEventBinding
    private val detailViewModel : DetailViewModel by viewModels()

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: ""
        setupObeserver()
        detailViewModel.getDetailEvent(eventId)

        val actionBar = supportActionBar
        actionBar!!.title = "Detail Event"

        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun setupObeserver() {
        detailViewModel.eventDetail.observe(this) { response ->
            response?.event?.let { event ->
                Glide.with(this).load(event.mediaCover).into(binding.imgCover)
                binding.tvEventName.text = event.name
                binding.tvOwnerName.text = "Penyelenggara: " +event.ownerName
                binding.tvBegin.text = "Waktu Mulai: " + event.beginTime
                binding.tvEnd.text = "Waktu Selesai: " + event.endTime
                val remainingQuota = event.quota?.minus(event.registrants!!)
                binding.tvQuota.text = "Kuota: $remainingQuota dari ${event.quota}"
                binding.tvDescription.text = HtmlCompat.fromHtml(
                    event.description ?: "",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                binding.btnLink.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    startActivity(intent)
                }
            }
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}