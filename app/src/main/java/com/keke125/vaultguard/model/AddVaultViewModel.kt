package com.keke125.vaultguard.model

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keke125.vaultguard.data.Folder
import com.keke125.vaultguard.data.FoldersRepository
import com.keke125.vaultguard.data.Vault
import com.keke125.vaultguard.data.VaultsRepository
import com.keke125.vaultguard.screen.AddVaultDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class AddVaultViewModel(
    savedStateHandle: SavedStateHandle,
    private val vaultsRepository: VaultsRepository,
    private val foldersRepository: FoldersRepository
) : ViewModel() {

    private val folderId: Int = checkNotNull(savedStateHandle[AddVaultDestination.FOLDERID])
    var vaultUiState by mutableStateOf(
        if (folderId == 0) {
            AddVaultUiState(vaultDetails = VaultDetails(folderUid = null))
        } else {
            AddVaultUiState(vaultDetails = VaultDetails(folderUid = folderId))
        }
    )
        private set

    private val _folderUiState = MutableStateFlow(FolderUiState())
    val folderUiState: StateFlow<FolderUiState> get() = _folderUiState

    init {
        viewModelScope.launch {
            if (folderId != 0) {
                foldersRepository.getFolderByUid(folderId).collect {
                    _folderUiState.value = FolderUiState(it)
                }
            } else {
                _folderUiState.value = FolderUiState()
            }
        }
    }

    var foldersUiState: StateFlow<FoldersUiState> = foldersRepository.getAllFolders().map {
        FoldersUiState(folderList = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = FoldersUiState()
    )

    fun updateUiState(vaultDetails: VaultDetails) {
        vaultUiState = AddVaultUiState(vaultDetails = vaultDetails)
    }

    fun updateFolderUiState(folderId: Int) {
        viewModelScope.launch {
            if (folderId != 0) {
                foldersRepository.getFolderByUid(folderId).collect {
                    _folderUiState.value = FolderUiState(it)
                }
            } else {
                _folderUiState.value = FolderUiState()
            }
        }
    }

    suspend fun saveVault() {
        val timeStamp: String
        if (Build.VERSION.SDK_INT >= 26) {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            timeStamp = LocalDateTime.now().format(formatter).toString()
        } else {
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
            timeStamp = simpleDateFormat.format(Date())
        }
        updateUiState(vaultUiState.vaultDetails.copy(createdDateTime = timeStamp))
        updateUiState(vaultUiState.vaultDetails.copy(lastModifiedDateTime = timeStamp))
        vaultsRepository.insertVault(vaultUiState.vaultDetails.toVault())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class AddVaultUiState(
    val vaultDetails: VaultDetails = VaultDetails()
)

data class VaultDetails(
    val uid: Int = 0,
    val name: String = "",
    val username: String = "",
    val password: String = "",
    val urlList: List<String> = emptyList(),
    val notes: String = "",
    val totp: String = "",
    val createdDateTime: String = "",
    val lastModifiedDateTime: String = "",
    val folderUid: Int? = null
)

fun VaultDetails.toVault(): Vault = Vault(
    uid = uid,
    name = name,
    username = username,
    password = password,
    urlList = urlList,
    notes = notes,
    totp = totp,
    createdDateTime = createdDateTime,
    lastModifiedDateTime = lastModifiedDateTime,
    folderUid = folderUid
)

fun Vault.toVaultDetails(): VaultDetails = VaultDetails(
    uid = uid,
    name = name,
    username = username,
    password = password,
    urlList = urlList,
    notes = notes,
    totp = totp,
    createdDateTime = createdDateTime,
    lastModifiedDateTime = lastModifiedDateTime,
    folderUid = folderUid
)

data class FolderUiState(
    val folder: Folder? = null
)
