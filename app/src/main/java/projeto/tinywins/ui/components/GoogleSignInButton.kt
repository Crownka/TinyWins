package projeto.tinywins.ui.auth.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember // IMPORT ADICIONADO
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import projeto.tinywins.R
import projeto.tinywins.ui.viewmodel.LoginUiState

@Composable
fun GoogleSignInButton(
    state: LoginUiState,
    onTokenReceived: (String) -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    // Configura as opções de login do Google para pedir o ID Token
    val googleSignInOptions = remember {
        val webClientId = context.getString(R.string.default_web_client_id)
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    // Prepara o launcher que vai receber o resultado da tela de login do Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    onTokenReceived(token) // Envia o token para o ViewModel
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Falha no login com Google: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Efeito para iniciar o fluxo de login quando o estado do ViewModel solicitar
    LaunchedEffect(state) {
        if (state is LoginUiState.Loading) {
            // A lógica de loading já é tratada pelo botão principal,
            // aqui garantimos que o fluxo do google seja iniciado.
            // Se quiséssemos um estado específico para o Google, criaríamos um aqui.
        }
    }

    val isLoading = state is LoginUiState.Loading

    Button(
        onClick = {
            onClick() // Informa ao ViewModel que o processo começou
            launcher.launch(googleSignInClient.signInIntent)
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text("ENTRAR COM O GOOGLE")
        }
    }
}