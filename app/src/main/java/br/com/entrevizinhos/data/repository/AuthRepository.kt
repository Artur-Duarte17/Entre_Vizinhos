package br.com.entrevizinhos.data.repository

import android.util.Log
import br.com.entrevizinhos.model.Usuario
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await // [IMPORTANTE] Permite usar .await() nas Tasks do Firebase

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
    }

    /**
     * Busca dados do usuário de forma assíncrona.
     * Retorna o Usuario ou null se não estiver logado.
     * Em caso de erro, retorna um usuário básico para não travar o app.
     */
    suspend fun carregarDadosUsuario(): Usuario? {
        val userId = auth.currentUser?.uid ?: return null

        return try {
            // Await suspende a execução até o Firestore responder, sem travar a UI
            val document =
                db
                    .collection("usuarios")
                    .document(userId)
                    .get()
                    .await()

            if (document.exists()) {
                document.toObject(Usuario::class.java)
            } else {
                // Usuário sem dados no banco, cria objeto com dados do Auth
                criarUsuarioBasico(userId)
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro ao carregar dados: ${e.message}")
            // Fallback em caso de erro (ex: sem internet)
            criarUsuarioBasico(userId)
        }
    }

    /**
     * Salva ou atualiza o perfil do usuário.
     */
    suspend fun salvarPerfil(usuario: Usuario): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            db
                .collection("usuarios")
                .document(userId)
                .set(usuario)
                .await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro ao salvar perfil", e)
            false
        }
    }

    suspend fun loginAnonymously(): Boolean =
        try {
            auth.signInAnonymously().await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro login anônimo", e)
            false
        }

    /**
     * Realiza login com Google e cria o documento no Firestore se não existir.
     */
    suspend fun loginComGoogle(credential: AuthCredential): Boolean {
        Log.d("AuthRepo", "Iniciando login com credencial...")
        return try {
            // 1. Autentica no Firebase Auth
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return false
            val userId = user.uid
            Log.d("AuthRepo", "Login Auth OK! ID: $userId")

            // 2. Verifica se o usuário já existe no Firestore
            val userDocRef = db.collection("usuarios").document(userId)
            val document = userDocRef.get().await()

            if (!document.exists()) {
                // 3. Se não existe, cria o registro inicial
                val novoUsuario =
                    Usuario(
                        id = userId,
                        nome = user.displayName ?: "Novo Vizinho",
                        email = user.email ?: "",
                        fotoUrl = user.photoUrl?.toString() ?: "",
                    )
                // O await aqui garante que só retornamos true após salvar
                userDocRef.set(novoUsuario).await()
                Log.d("AuthRepo", "Novo usuário salvo no Firestore")
            } else {
                Log.d("AuthRepo", "Usuário já existia no Firestore")
            }

            true // Sucesso total
        } catch (e: Exception) {
            Log.e("AuthRepo", "Erro fatal na autenticação/banco", e)
            false
        }
    }

    // Helper privado para evitar duplicação de código
    private fun criarUsuarioBasico(userId: String): Usuario {
        val email = auth.currentUser?.email ?: ""
        val nome = auth.currentUser?.displayName ?: "Vizinho Visitante"
        val foto = auth.currentUser?.photoUrl?.toString() ?: ""
        return Usuario(id = userId, email = email, nome = nome, fotoUrl = foto)
    }
}
