// Define o pacote da equipe em letras minúsculas e sem espaços, conforme padrão obrigatório
package vingadoresdocodigo;

// Importa as classes principais do Robocode, cores e tratamento de erro para envio de mensagens
import robocode.*;
import java.awt.Color; //função de cores para personalizar o robô
import java.io.IOException; // necessário para tratar possíveis falhas de transmissão de mensagens entre robôs


 // Classe Jarvis - Papel: Sniper / Estrategista.
public class Jarvis extends TeamRobot {

    /**
     * Método principal (sobrescrito da classe pai) que define o comportamento padrão do tanque.
     */
    public void run() {
        // Define as cores visuais do Jarvis: Corpo Azul, Canhão Amarelo e Radar Branco
        setColors(Color.RED);

        // Desacopla o movimento do canhão e do radar em relação ao giro do corpo do robô
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Loop infinito de patrulha enquanto o robô estiver vivo na batalha
        while (true) {
            ahead(150);           // Avança 150 pixels para manter movimentação ampla
            turnRadarRight(360);  // Gira o radar em 360 graus para mapear toda a arena
            turnRight(45);        // Gira o corpo em 45 graus para não andar em linha reta previsível             // Dispara com potência máxima para eliminar inimigos rapidamente
        }
    }

    /**
     * Evento disparado automaticamente sempre que o radar detecta outro tanque no mapa.
     */
    public void onScannedRobot(ScannedRobotEvent e) {

        // 1. PREVENÇÃO DE FOGO AMIGO: Verifica se o robô detectado é do mesmo time (Ultron)
        if (isTeammate(e.getName())) {
            return; // Ignora o parceiro e encerra a execução deste evento
        }

        // 2. COMUNICAÇÃO EM EQUIPE: Transmite o nome do inimigo detectado para o parceiro focar o ataque
        try {
            broadcastMessage(e.getName());
        } catch (IOException ex) {
            ex.printStackTrace(); // Trata possíveis falhas de transmissão de mensagem
        }

        // 3. CÁLCULO DE MIRA: Calcula o ângulo exato entre a direção atual do canhão e a posição do inimigo
        double anguloInimigo = getHeading() + e.getBearing() - getGunHeading(); // Determina o ângulo necessário para alinhar o canhão ao inimigo
        turnGunRight(normalizarAngulo(anguloInimigo)); // Gira o canhão pelo caminho mais curto

        // 4. ESTRATÉGIA SNIPER (Controle de Energia): Ajusta a potência do tiro conforme a distância
        if (e.getDistance() < 150) {
            fire(3);   // Inimigo muito próximo: disparo de potência máxima
        } else if (e.getDistance() < 400) {
            fire(2);   // Média distância: disparo equilibrado
        } else {
            fire(1);   // Longa distância: tiro leve para economizar energia
        }
    }

    /**
     * Evento disparado quando o robô é atingido por um projétil inimigo.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // Evasão perpendicular: gira 90 graus em relação à origem do tiro e acelera para sair da mira
        turnRight(normalizarAngulo(e.getBearing() + 90));
        ahead(100);
    }

    /**
     * Evento disparado quando o robô colide com uma das paredes da arena.
     */
    public void onHitWall(HitWallEvent e) {
        // Recua e gira para não ficar encurralado na borda da arena
        back(60);
        turnRight(90);
    }

    /**
     * Método auxiliar privado (encapsulamento) que converte qualquer ângulo para a faixa
     * entre -180 e +180 graus, garantindo que o robô sempre gire pelo lado mais rápido.
     */
    private double normalizarAngulo(double angulo) {
        while (angulo > 180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}