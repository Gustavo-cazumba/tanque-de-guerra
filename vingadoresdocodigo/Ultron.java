// Mesmo pacote da equipe para garantir integração e cumprimento da estrutura de entrega
package vingadoresdocodigo;

import robocode.*;
import java.awt.Color;

/**
 * Classe Ultron - Papel: Tanque Caótico / Linha de Frente (Brawler).
 * Herda de TeamRobot para receber alertas do Jarvis e coordenar ataques concentrados.3
 */
public class Ultron extends TeamRobot {

    // Atributo privado (encapsulamento) que armazena o nome do inimigo marcado pelo Jarvis
    private String alvoPrioritario = null;

    /**
     * Comportamento principal de movimentação agressiva do Ultron.
     */
    public void run() {
        // Define as cores visuais do Ultron: Corpo Vermelho, Canhão Cinza Escuro e Radar Preto
        setColors(Color.BLACK);

        // Permite girar canhão e radar de forma independente da rotação do chassi
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Loop infinito com movimentação em zigue-zague para dificultar a mira adversária
        while (true) {
            ahead(120);           // Avança rapidamente
            turnRight(60);        // Muda o ângulo para a direita
            turnRadarRight(360);  // Escaneia a arena
            ahead(120);           // Continua avançando
            turnLeft(90);         // Muda bruscamente para a esquerda
            turnRadarRight(360);  // Escaneia novamente
        }
    }

    /**
     * Evento exclusivo de TeamRobot: disparado quando o parceiro (Jarvis) envia uma mensagem.
     */
    public void onMessageReceived(MessageEvent e) {
        // Verifica se a mensagem recebida é um texto (nome do inimigo) e atualiza o alvo prioritário
        if (e.getMessage() instanceof String) {
            alvoPrioritario = (String) e.getMessage();
        }
    }

    /**
     * Reação ao detectar um robô na arena.
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        // 1. PREVENÇÃO DE FOGO AMIGO: Ignora o parceiro Jarvis
        if (isTeammate(e.getName())) {
            return;
        }

        // 2. COORDENAÇÃO DE FOCO (2x1): Se houver um alvo prioritário definido pelo Jarvis,
        // ignora outros inimigos distantes para concentrar fogo e eliminar um adversário por vez
        if (alvoPrioritario != null && !e.getName().equals(alvoPrioritario) && e.getDistance() > 200) {
            return;
        }

        // 3. ALINHAMENTO DE MIRA: Calcula o ângulo para apontar o canhão diretamente ao alvo
        double anguloInimigo = getHeading() + e.getBearing() - getGunHeading();
        turnGunRight(normalizarAngulo(anguloInimigo));

        // 4. ATAQUE PESADO (Linha de Frente): Dispara com alta potência para pressionar o oponente
        if (e.getDistance() < 250) {
            fire(3);     // Curta distância: dano máximo
        } else {
            fire(1.5);   // Média/longa distância: mantém pressão constante
        }

        // 5. PERSEGUIÇÃO: Vira o corpo na direção do inimigo e avança para encurtar distância
        turnRight(normalizarAngulo(e.getBearing()));
        ahead(80);
    }

    /**
     * Reação evasiva ao levar um tiro.
     */
    public void onHitByBullet(HitByBulletEvent e) {
        // Gira perpendicularmente ao tiro e acelera para escapar da linha de fogo
        turnLeft(normalizarAngulo(e.getBearing() + 90));
        ahead(120);
    }

    /**
     * Reação ao bater na parede da arena.
     */
    public void onHitWall(HitWallEvent e) {
        // Dá ré e gira em ângulo aberto para retornar ao centro da batalha
        back(70);
        turnLeft(110);
    }

    /**
     * Método auxiliar privado para normalizar ângulos entre -180 e 180 graus (giro otimizado).
     */
    private double normalizarAngulo(double angulo) {
        while (angulo > 180) angulo -= 360;
        while (angulo < -180) angulo += 360;
        return angulo;
    }
}