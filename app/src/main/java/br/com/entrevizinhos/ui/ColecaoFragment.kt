package br.com.entrevizinhos.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.entrevizinhos.databinding.FragmentColecaoBinding
import br.com.entrevizinhos.ui.adapter.AnuncioAdapter
import br.com.entrevizinhos.viewmodel.LerAnuncioViewModel

class ColecaoFragment : Fragment() {
    private var bindingNullable: FragmentColecaoBinding? = null
    private val binding get() = bindingNullable!!

    // Compartilha o ViewModel com outras telas
    private val lerAnuncioViewModel: LerAnuncioViewModel by activityViewModels()

    private lateinit var adapter: AnuncioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        bindingNullable = FragmentColecaoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter =
            AnuncioAdapter(
                listaAnuncios = emptyList(),
                favoritosIds = lerAnuncioViewModel.favoritosIds.value ?: emptySet(),
                onAnuncioClick = { anuncio ->
                    // ===== NAVEGAR PARA DETALHES =====
                    val action = ColecaoFragmentDirections.actionColecaoToDetalhesAnuncio(anuncio)
                    findNavController().navigate(action)
                },
                onFavoritoClick = { anuncio ->
                    // Delegar toggle para o ViewModel
                    lerAnuncioViewModel.onFavoritoClick(anuncio.id)
                },
            )

        binding.rvColecao.layoutManager = LinearLayoutManager(requireContext())
        binding.rvColecao.adapter = adapter
    }

    private fun setupObservers() {
        // Observa anúncios e favoritos e mostra apenas os favoritos
        lerAnuncioViewModel.anuncios.observe(viewLifecycleOwner) { lista ->
            val favoritos = lerAnuncioViewModel.favoritosIds.value ?: emptySet()
            val listaFiltrada = lista.filter { it.id in favoritos }
            adapter.atualizarLista(listaFiltrada, favoritos)
        }

        lerAnuncioViewModel.favoritosIds.observe(viewLifecycleOwner) { favoritos ->
            val lista = lerAnuncioViewModel.anuncios.value ?: emptyList()
            val listaFiltrada = lista.filter { it.id in favoritos }
            adapter.atualizarLista(listaFiltrada, favoritos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bindingNullable = null
    }
}
