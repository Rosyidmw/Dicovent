@file:Suppress("unused")

package com.rosyid.testnavdicovent.ui.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rosyid.testnavdicovent.adapter.EventAdapter
import com.rosyid.testnavdicovent.databinding.FragmentHomeBinding
import com.rosyid.testnavdicovent.ui.detail.DetailEventActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var eventAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventAdapter = EventAdapter { eventId ->
            navigateToDetail(eventId)
        }

        finishedAdapter = EventAdapter { eventId ->
            navigateToDetail(eventId)
        }

        setupRecyclerView()
        observeViewModel()

        if (isInternetAvailable()) {
            homeViewModel.loadUpcomingEvents()
            homeViewModel.loadFinishedEvents()
        } else {
            Toast.makeText(requireContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = eventAdapter
        }

        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedAdapter
        }
    }

    private fun observeViewModel() {
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events.take(5))
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events.take(5))
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun navigateToDetail(eventId: String) {
        val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
            putExtra(DetailEventActivity.EXTRA_EVENT_ID, eventId)
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
