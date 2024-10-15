@file:Suppress("unused")

package com.rosyid.testnavdicovent.ui.finished

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rosyid.testnavdicovent.adapter.EventAdapter
import com.rosyid.testnavdicovent.databinding.FragmentFinishedBinding
import com.rosyid.testnavdicovent.ui.detail.DetailEventActivity

@Suppress("DEPRECATION")
class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventsAdapter: EventAdapter
    private val finishedViewModel: FinishedViewModel by viewModels()

    companion object {
        private const val TAG = "FinishedFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()


        if (isInternetAvailable()) {
            finishedViewModel.getFinishedEvents()
        } else {

            Toast.makeText(requireContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        eventsAdapter = EventAdapter { eventId ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, eventId)
            startActivity(intent)
        }

        binding.rvActive.apply {
            adapter = eventsAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = finishedViewModel.filterEvents(newText ?: "")
                eventsAdapter.submitList(filteredList)
                return true
            }
        })
    }

    private fun observeViewModel() {
        finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            eventsAdapter.submitList(events)
        }

        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        finishedViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.e(TAG, it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
