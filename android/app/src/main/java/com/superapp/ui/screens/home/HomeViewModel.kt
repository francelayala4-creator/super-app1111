package com.superapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.superapp.data.api.dto.ShoppingListDto
import com.superapp.data.api.dto.StoreDto
import com.superapp.data.repository.ChainsRepository
import com.superapp.data.repository.ListRepository
import com.superapp.util.LocationProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val userLat: Double? = null,
    val userLng: Double? = null,
    val stores: List<StoreDto> = emptyList(),
    val lists: List<ShoppingListDto> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val lists: ListRepository,
    private val chains: ChainsRepository,
    private val location: LocationProvider,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        val loc = runCatching { location.lastKnown() }.getOrNull()
        val (lat, lng) = (loc?.latitude ?: 19.4326) to (loc?.longitude ?: -99.1332)
        val nearby = runCatching { chains.nearby(lat, lng) }.getOrDefault(emptyList())
        val my = runCatching { lists.myLists() }.getOrDefault(emptyList())
        _state.value = HomeState(userLat = lat, userLng = lng, stores = nearby, lists = my, loading = false)
    }
}
