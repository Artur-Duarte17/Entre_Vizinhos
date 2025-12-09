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
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val anuncioRepository = AnuncioRepository()
    private val usuarioRepository = UsuarioRepository()

    private val _vendedor = MutableLiveData<Usuario?>()
    val vendedor: LiveData<Usuario?> = _vendedor

    // --- DADOS DO USUÁRIO ---
    // Pequena correção de tipo aqui para evitar warnings de cast
    private val _dadosUsuario = MutableLiveData<Usuario?>()
    val dadosUsuario: LiveData<Usuario?> = _dadosUsuario

    // --- ESTADO DO LOGOUT ---
    private val _estadoLogout = MutableLiveData<Boolean>()
    val estadoLogout: LiveData<Boolean> = _estadoLogout

    // --- ESTADO DE CARREGAMENTO ---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // --- ANUNCIOS ---
    private val _meusAnuncios = MutableLiveData<List<Anuncio>>()
    val meusAnuncios: LiveData<List<Anuncio>> = _meusAnuncios

    fun carregarDados() {
        viewModelScope.launch {
            _isLoading.value = true
            val usuario = repository.carregarDadosUsuario()
            if (usuario != null) {
                _dadosUsuario.value = usuario
            }
            _isLoading.value = false
        }
    }

    fun salvarPerfil(usuario: Usuario) {
        viewModelScope.launch {
            _isLoading.value = true
            val sucesso = repository.salvarPerfil(usuario)
            _isLoading.value = false
            if (sucesso) {
                _dadosUsuario.value = usuario
            }
        }
    }

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
            _isLoading.value = true
            val lista = anuncioRepository.buscarAnunciosPorVendedor(usuarioAtual.uid)
            _meusAnuncios.value = lista
            _isLoading.value = false
        }
    }

    fun carregarVendedor(id: String) {
        viewModelScope.launch {
            val usuario = usuarioRepository.getUsuario(id)
            _vendedor.value = usuario
        }
    }

    suspend fun deletarAnuncio(anuncioId: String): Boolean {
        val sucesso = anuncioRepository.deletarAnuncio(anuncioId)

        // Se deu certo, atualizamos a lista local imediatamente para a UI reagir
        if (sucesso) {
            val listaAtual = _meusAnuncios.value.orEmpty().toMutableList()
            listaAtual.removeAll { it.id == anuncioId }
            _meusAnuncios.postValue(listaAtual)
        }

        return sucesso
    }
}
