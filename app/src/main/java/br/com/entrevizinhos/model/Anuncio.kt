package br.com.entrevizinhos.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Anuncio(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val categoria: String = "",
    val preco: Double = 0.0,
    val cidade: String = "",
    val fotos: List<String> = emptyList(),
    val vendedorId: String = "",
    val dataPublicacao: Date = Date(),
) : Parcelable
