package com.keke125.vaultguard.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchVaultViewModel(private val vaultsRepository: VaultsRepository) : ViewModel() {

    private val _searchKeyword = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchVaultUiState: StateFlow<SearchVaultUiState> = _searchKeyword
        .flatMapLatest { keyword ->
            vaultsRepository.getAllVaultsFiltered(keyword)
        }
        .map { SearchVaultUiState(vaultList = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SearchVaultUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }
}


data class SearchVaultUiState(val vaultList: List<Vault> = listOf())
