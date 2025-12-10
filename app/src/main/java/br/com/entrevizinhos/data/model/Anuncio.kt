package br.com.entrevizinhos.data.model

import java.io.Serializable
import java.util.Date

data class Anuncio(
    val id: String = "", // ID único gerado pelo Firestore
    val titulo: String = "", // Nome do produto/serviço anunciado
    val descricao: String = "", // Detalhes e características do item
    val categoria: String = "", // Classificação (Móveis, Eletrônicos, Serviços, etc)
    val preco: Double = 0.0, // Valor em reais (R$)
    val cidade: String = "", // Cidade onde está localizado o vendedor
    val fotos: List<String> = emptyList(), // Lista de imagens em Base64 ou URLs
    val vendedorId: String = "", // Referência ao usuário proprietário
    val dataPublicacao: Date = Date(), // Timestamp de quando foi criado
    val entrega: String = "", // Modalidade: "Retirada", "Entrega", "Ambos"
    val formasPagamento: String = "", // Métodos aceitos: "Dinheiro, PIX, Cartão"
) : Serializable // Permite passar objeto entre telas
