package br.com.entrevizinhos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.entrevizinhos.data.repository.AnuncioRepository
import br.com.entrevizinhos.data.repository.AuthRepository
import br.com.entrevizinhos.data.repository.UsuarioRepository
import br.com.entrevizinhos.model.Anuncio
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val anuncioRepository = AnuncioRepository()
    private val usuarioRepository = UsuarioRepository()

    private val _vendedor = MutableLiveData<Usuario?>()
    val vendedor: LiveData<Usuario?> = _vendedor

    // --- DADOS DO USUÁRIO ---
    private val _dadosUsuario = MutableLiveData<Usuario>()
    val dadosUsuario: LiveData<Usuario> = _dadosUsuario

    // --- ESTADO DO LOGOUT ---
    private val _estadoLogout = MutableLiveData<Boolean>()
    val estadoLogout: LiveData<Boolean> = _estadoLogout

    // --- ESTADO DE CARREGAMENTO ---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // --- ANUNCIOS ---
    private val _meusAnuncios = MutableLiveData<List<Anuncio>>()
    val meusAnuncios: LiveData<List<Anuncio>> = _meusAnuncios

    // [CORREÇÃO 1] Refatorado para usar Coroutines (sem callback)
    fun carregarDados() {
        viewModelScope.launch {
            _isLoading.value = true
            // A execução "pausa" aqui até o dado chegar, sem travar a UI
            val usuario = repository.carregarDadosUsuario()

            if (usuario != null) {
                _dadosUsuario.value = usuario
            }
            _isLoading.value = false
        }
    }

    // [CORREÇÃO 2] Refatorado para usar Coroutines (sem callback)
    fun salvarPerfil(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true

            // Chama a função suspend e espera o retorno booleano
            val sucesso = repository.salvarPerfil(usuario)

            _isLoading.value = false
            if (sucesso) {
                _dadosUsuario.value = usuario
            }
        }
    }

    // Desloga do Firebase
    fun deslogar() {
        repository.signOut()
        _estadoLogout.value = true
    }

    fun carregarMeusAnuncios() {
        val usuarioAtual = repository.getCurrentUser()

        if (usuarioAtual == null) {
            _meusAnuncios.value = emptyList()
            return
        }

        viewModelScope.launch {
            val lista = anuncioRepository.buscarAnunciosPorVendedor(usuarioAtual.uid)
            _meusAnuncios.value = lista
        }
    }

    fun carregarVendedor(id: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.getUsuario(id)
            _vendedor.value = usuario
        }
    }

    // [OBSERVAÇÃO TÉCNICA]
    // Idealmente, esta lógica de Firestore deveria estar no AnuncioRepository,
    // mas mantive aqui para não quebrar seu fluxo atual.
    fun deletarAnuncio(
        anuncioId: String,
        callback: (Boolean) -> Unit,
    ) {
        val db = FirebaseFirestore.getInstance()

        db
            .collection("anuncios")
            .document(anuncioId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
    }
}
