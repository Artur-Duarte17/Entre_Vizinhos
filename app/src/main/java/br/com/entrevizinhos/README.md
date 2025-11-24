Markdown

# 🏘️ Entre Vizinhos - Marketplace Local

Aplicativo Android nativo para compra, venda e doações locais na região de Urutaí-GO, com foco em contato direto via WhatsApp.

## 🚀 Status do Projeto

O projeto está em sua **Fase 1 (Estruturação de UI e Navegação)**.
Atualmente, a estrutura base do aplicativo foi implementada seguindo o padrão **MVVM**, mas os dados exibidos ainda são estáticos (Mocks/Dados Falsos) para fins de teste de layout. O Firebase ainda não está conectado.

### ✅ O que já foi feito:
* **Arquitetura:** Estrutura de pastas organizada em MVVM (`model`, `ui`, `data`, `viewmodel`).
* **Navegação:** Implementada com **Navigation Component** e **BottomNavigationView**.
    * Aba Início (Feed)
    * Aba Coleção (Favoritos)
    * Aba Perfil (Usuário)
* **Listagem (Feed):** Implementação de `RecyclerView` com `CardView` personalizado (`item_anuncio.xml`).
* **ViewBinding:** Ativado e utilizado em todas as telas para manipulação segura das Views.
* **Modelos de Dados:** Classes `Anuncio` e `Usuario` criadas implementando `Serializable`.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Kotlin
* **Layout:** XML (ConstraintLayout, Material Components)
* **Arquitetura:** MVVM (Model-View-ViewModel)
* **Navegação:** Jetpack Navigation Component (Single Activity Architecture)
* **Listas:** RecyclerView + Adapter
* **Build:** Gradle Kotlin DSL

---

## ⚙️ Como Executar o Projeto
Pré-requisitos
Android Studio Koala ou superior.

JDK 17 ou superior.

Dispositivo ou Emulador Android (API 24+ recomendada).

1. Clonar o repositório:

Bash

git clone [https://github.com/SEU_USUARIO/EntreVizinhos.git](https://github.com/SEU_USUARIO/EntreVizinhos.git)

2. Abrir no Android Studio:

Abra o Android Studio -> Open -> Selecione a pasta do projeto.

3. Sincronizar o Gradle:

O Android Studio deve pedir automaticamente. Se não, clique em File -> Sync Project with Gradle Files.

4. Executar:

Selecione o emulador e clique no botão ▶️ (Run) ou pressione Shift + F10.

---

## 📂 Estrutura do Projeto

O código está organizado dentro de `br.com.entrevizinhos`:

```text
📂 br.com.entrevizinhos
 ┣ 📂 model        # Classes de dados (Anuncio.kt, Usuario.kt)
 ┣ 📂 ui           # Camada de Apresentação
 ┃ ┣ 📂 adapter    # Adaptadores para listas (AnuncioAdapter.kt)
 ┃ ┣ 📜 MainActivity.kt    # Activity container (Gerencia o Menu Inferior)
 ┃ ┣ 📜 FeedFragment.kt    # Tela Principal (Lista de produtos)
 ┃ ┣ 📜 ColecaoFragment.kt # Tela de Favoritos
 ┃ ┗ 📜 PerfilFragment.kt  # Tela de Perfil do Usuário
 ┣ 📂 data         # (Vazio) Futura implementação dos repositórios Firebase
 ┗ 📂 viewmodel    # (Vazio) Futura lógica de conexão UI <-> Data
