package com.keke125.vaultguard.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.SearchVaultByFolderUidDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SearchVaultByFolderUidViewModel(
    savedStateHandle: SavedStateHandle, private val vaultsRepository: VaultsRepository
) : ViewModel() {

    private val folderId: Int =
        checkNotNull(savedStateHandle[SearchVaultByFolderUidDestination.FOLDERID])

    var searchVaultByFolderUidUiState: StateFlow<SearchVaultUiState> =
        vaultsRepository.getAllVaultsFilteredByFolderUid("", if (folderId == 0) null else folderId)
            .filterNotNull().map {
                SearchVaultUiState(vaultList = it)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SearchVaultUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


    fun updateKeyword(keyword: String) {
        searchVaultByFolderUidUiState = vaultsRepository.getAllVaultsFilteredByFolderUid(
            keyword, if (folderId == 0) null else folderId
        ).filterNotNull().map {
            SearchVaultUiState(vaultList = it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SearchVaultUiState()
        )
    }
}
