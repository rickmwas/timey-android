package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.Venue
import com.example.core.model.VenueStatus
import com.example.data.repository.TimyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VenuesUiState(
    val query: String = "",
    val selectedStatusFilter: VenueStatus? = null,
    val venues: List<Venue> = emptyList(),
    val filteredVenues: List<Venue> = emptyList(),
    val selectedVenue: Venue? = null,
    val isRefreshing: Boolean = false
)

class VenuesViewModel(
    private val repository: TimyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VenuesUiState())
    val uiState: StateFlow<VenuesUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadVenues()
    }

    private fun loadVenues() {
        viewModelScope.launch {
            repository.getAllVenues().collect { venuesList ->
                _uiState.update { current ->
                    current.copy(
                        venues = venuesList,
                        filteredVenues = filterVenues(venuesList, current.query, current.selectedStatusFilter)
                    )
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(250) // Debounce search
            val current = _uiState.value
            _uiState.update {
                it.copy(filteredVenues = filterVenues(current.venues, newQuery, current.selectedStatusFilter))
            }
        }
    }

    fun filterByStatus(status: VenueStatus?) {
        _uiState.update { current ->
            current.copy(
                selectedStatusFilter = status,
                filteredVenues = filterVenues(current.venues, current.query, status)
            )
        }
    }

    fun selectVenue(venue: Venue?) {
        _uiState.update { it.copy(selectedVenue = venue) }
    }

    fun refreshVenues() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            repository.refreshVenues()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private fun filterVenues(
        venues: List<Venue>,
        query: String,
        status: VenueStatus?
    ): List<Venue> {
        val q = query.trim().lowercase()
        return venues.filter { venue ->
            val matchesQuery = if (q.isBlank()) true else {
                venue.code.lowercase().contains(q) ||
                    venue.name.lowercase().contains(q) ||
                    venue.building.lowercase().contains(q)
            }
            val matchesStatus = if (status != null) venue.status == status else true
            matchesQuery && matchesStatus
        }.sortedBy { it.code }
    }
}
