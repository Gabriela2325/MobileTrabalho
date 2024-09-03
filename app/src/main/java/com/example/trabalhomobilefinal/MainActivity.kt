package com.example.trabalhomobilefinal

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import kotlin.random.Random

import android.health.connect.datatypes.SleepSessionRecord

import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.trabalhomobilefinal.Stage.COMPLETE
import com.example.trabalhomobilefinal.Stage.FINAL
import com.example.trabalhomobilefinal.Stage.GIVE_UP
import com.example.trabalhomobilefinal.Stage.INITIAL
import com.example.trabalhomobilefinal.Stage.MID
import com.example.trabalhomobilefinal.ui.theme.TrabalhoMobileFinalTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrabalhoMobileFinalTheme {
                JourneyApp(onExit = { finish() })
            }
        }
    }
}

// ------------------

@Composable
fun JourneyApp(onExit: () -> Unit) {
    // Declaração de variáveis de estado usando 'remember' para persistir o valor durante recomposições
    var clicks by remember { mutableStateOf (0) } // Conta os cliques do usuário
    var targetClicks by remember { mutableStateOf(Random.nextInt(1, 51)) } // Define um número aleatório de cliques alvo
    var stage by remember { mutableStateOf(Stage.INITIAL) } // Controla o estágio atual do jogo
    var showDialog by remember { mutableStateOf(false) } // Controla a exibição do diálogo de vitória
    var showGiveUpDialog by remember { mutableStateOf(false) } // Controla a exibição do diálogo de desistência

    // Define a estrutura da UI como uma coluna centralizada
    Column(
        modifier = Modifier.fillMaxSize(), // Ocupa todo o espaço disponível
        verticalArrangement = Arrangement.Center, // Alinha verticalmente ao centro
        horizontalAlignment = Alignment.CenterHorizontally // Alinha horizontalmente ao centro
    ) {
        // Calcula o progresso baseado nos cliques do usuário
        val progress = clicks.toDouble() / targetClicks
        stage = when {
            progress >= 1.0 -> COMPLETE // Se o progresso for igual ou maior que 100%, estágio completo
            progress >= 0.66 -> FINAL // Se o progresso for entre 66% e 99%, estágio final
            progress >= 0.33 -> MID // Se o progresso for entre 33% e 65%, estágio intermediário
            else -> INITIAL // Se o progresso for menor que 33%, estágio inicial
        }

        // Exibe a imagem correspondente ao estágio atual
        when (stage) {
            INITIAL -> {
                ImageStage(R.drawable.baufechado, "Começo da Jornada")
            }
            MID -> {
                ImageStage(R.drawable.bauvazio, "Progresso Intermediário")
            }
            FINAL -> {
                ImageStage(R.drawable.baucheio, "Próximo da Conquista")
            }
            COMPLETE -> {
                ImageStage(R.drawable.baumuitocheio, "Conquista Completa!")
                if (!showDialog) { // Se o estágio for completo e o diálogo ainda não foi mostrado
                    showDialog = true // Exibe o diálogo de vitória
                }
            }
            GIVE_UP -> {
                ImageStage(R.drawable.baumal, "Desistência")
                if (!showGiveUpDialog) { // Se o estágio for de desistência e o diálogo ainda não foi mostrado
                    showGiveUpDialog = true // Exibe o diálogo de desistência
                }
                
                Text(text = "Textosiohjioasdhfbsdjibfjoisdbfrsdjiofgbojsdbuof")
                
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaço entre os elementos

        // Exibe os botões "Avançar" e "Desistir" se o estágio não for de conclusão ou desistência
        if (stage != COMPLETE && stage != GIVE_UP) {
            Button(onClick = {
                clicks++ // Incrementa o número de cliques quando o botão "Avançar" é pressionado
            }) {
                Text("${clicks}") // Texto exibido no botão
            }

            Spacer(modifier = Modifier.width(8.dp)) // Espaço horizontal entre os botões

            Button(onClick = {
                stage = GIVE_UP // Define o estágio como desistência
                showGiveUpDialog = true // Exibe o diálogo de desistência
            }) {
                Text("Desistir") // Texto exibido no botão
            }
        }

        // Exibe o diálogo de parabéns se o estágio for completo
        if (showDialog) {
            CongratulationDialog(
                onPlayAgain = {
                    // Reseta o jogo ao clicar em "Jogar Novamente"
                    clicks = 0
                    targetClicks = Random.nextInt(1, 51)
                    showDialog = false
                    stage = INITIAL
                }
            )
        }

        // Exibe o diálogo de desistência se o estágio for desistência
        if (showGiveUpDialog) {
            GiveUpDialog(
                onPlayAgain = {
                    // Reseta o jogo ao clicar em "Sim" no diálogo de desistência
                    clicks = 0
                    targetClicks = Random.nextInt(1, 51)
                    stage = INITIAL
                    showGiveUpDialog = false
                },
                onExit = onExit
            )
        }
    }
}

@Composable
fun GiveUpDialog(onPlayAgain: () -> Unit, onExit: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Você desistiu!") },
        text = { Text(text = "Novo jogo?")
            Image(
                painter = painterResource(id = R.drawable.baumal),
                contentDescription = "Imagem de Desistência",
                modifier = Modifier.size(100.dp)
            )},
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text("Recomeçar")
            }
        },
        dismissButton = {
            Button(onClick = onExit) {
                Text("Sair")
            }
        }
    )
}

@Composable
fun ImageStage(imageResId: Int, description: String) {
    Image(
        painter = painterResource(id = imageResId), // Recurso de imagem a ser exibido
        contentDescription = description, // Descrição da imagem para acessibilidade
        modifier = Modifier
            .size(200.dp) // Define o tamanho da imagem
            .clickable { } // A imagem pode ser clicada, mas não faz nada aqui
    )
}

@Composable
fun CongratulationDialog(onPlayAgain: () -> Unit) {

    val context = LocalContext.current
    val activity = context as? Activity

    AlertDialog(
        onDismissRequest = {}, // Não permite que o diálogo seja descartado ao clicar fora
        title = { Text(text = "Parabéns!") }, // Título do diálogo
        text = { Text("Você completou a jornada!") }, // Texto principal do diálogo
        confirmButton = {
            Button(onClick = onPlayAgain) {
                Text("Jogar Novamente") // Botão para jogar novamente
            }
        },
        dismissButton = {
            Button(onClick = {activity?.finish()}) {
                Text("Sair") // Botão para sair ou encerrar o aplicativo
            }
        }
    )
}




enum class Stage {
    INITIAL, MID, FINAL, COMPLETE, GIVE_UP
}
