import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

public class JogoSnakeColorido extends JPanel implements Runnable, KeyListener {

    private static final int LARGURA_JANELA = 300;
    private static final int ALTURA_JANELA = 300;
    private static final int TAMANHO_BLOCO = 20;
    private static final int DELAY_INICIAL = 200;
    private static final int PONTUACAO_INICIAL = 0;
    private static final int PONTUACAO_MAXIMA = 10;
    private static final int FASES_TOTAL = 5;
    private static final int VELOCIDADE_INICIAL = 1;
    private static final int VELOCIDADE_MAXIMA = 5;
    private static final Color[] CORES = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.DARK_GRAY, Color.MAGENTA, Color.PINK};

    private Thread thread;
    private boolean emJogo;
    private int pontuacao;
    private int faseAtual;
    private LinkedList<Point> cobra;
    private LinkedList<Color> coresCobra;
    private int direcao;
    private Point maca;
    private Color corMaca;
    private int velocidade;

    public JogoSnakeColorido() {
        setPreferredSize(new Dimension(LARGURA_JANELA, ALTURA_JANELA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        iniciarJogo();
    }

    private void iniciarJogo() {
        emJogo = true;
        pontuacao = PONTUACAO_INICIAL;
        faseAtual = 1;
        cobra = new LinkedList<>();
        coresCobra = new LinkedList<>();
        cobra.add(new Point(0, 0));
        coresCobra.add(Color.WHITE);
        direcao = KeyEvent.VK_RIGHT;
        gerarMaca();
        velocidade = VELOCIDADE_INICIAL;
        thread = new Thread(this);
        thread.start();
    }

    private void gerarMaca() {
        Random random = new Random();
        int x = random.nextInt(LARGURA_JANELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        int y = random.nextInt(ALTURA_JANELA / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        maca = new Point(x, y);

        if (pontuacao == 0) {
        corMaca = Color.WHITE;  // Atribuir a cor branca apenas para a primeira maçã
    } else {
        corMaca = obterCorMacaAleatoria();
    }
    }

    private Color obterCorMacaAleatoria() {
        Random random = new Random();
        return CORES[random.nextInt(CORES.length)];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        desenhar(g);
    }

    private void desenhar(Graphics g) {
        if (emJogo) {
            desenharCobra(g);
            desenharMaca(g);
        } else {
            exibirMensagemFimJogo(g);
        }
    }

    private void desenharCobra(Graphics g) {
        for (int i = 0; i < cobra.size(); i++) {
            Point ponto = cobra.get(i);
            Color cor = coresCobra.get(i);
            g.setColor(cor);
            g.fillRect(ponto.x, ponto.y, TAMANHO_BLOCO, TAMANHO_BLOCO);
        }
        // Desenhar cabeça da cobra (branca)
        g.setColor(Color.WHITE);
        g.fillRect(cobra.getLast().x, cobra.getLast().y, TAMANHO_BLOCO, TAMANHO_BLOCO);
    }


    private void desenharMaca(Graphics g) {
        g.setColor(corMaca);
        g.fillRect(maca.x, maca.y, TAMANHO_BLOCO, TAMANHO_BLOCO);
    }

    private void exibirMensagemFimJogo(Graphics g) {
        String mensagem = "Fim de Jogo - Pontuação: " + pontuacao;
        Font fonte = new Font("Arial", Font.BOLD, 20);
        FontMetrics metrics = getFontMetrics(fonte);

        g.setColor(Color.WHITE);
        g.setFont(fonte);
        g.drawString(mensagem, (LARGURA_JANELA - metrics.stringWidth(mensagem)) / 2, ALTURA_JANELA / 2);
    }

    private void mover() {
        Point cabeca = new Point(cobra.getLast());
        switch (direcao) {
            case KeyEvent.VK_UP:
                cabeca.y -= TAMANHO_BLOCO;
                break;
            case KeyEvent.VK_DOWN:
                cabeca.y += TAMANHO_BLOCO;
                break;
            case KeyEvent.VK_LEFT:
                cabeca.x -= TAMANHO_BLOCO;
                break;
            case KeyEvent.VK_RIGHT:
                cabeca.x += TAMANHO_BLOCO;
                break;
          
        }
    
        if (verificarColisao()) {
            emJogo = false;
            return;
        }
    
        if (cobra.getLast().equals(maca)) {
            pontuacao++;
            if (pontuacao == PONTUACAO_MAXIMA) {
                avancarFase();
            }
            gerarMaca();
            coresCobra.addLast(corMaca);
        } else {
            cobra.removeFirst();
        }
        
        cobra.addLast(cabeca);
    }
    

    private boolean verificarColisao() {
        Point cabeca = cobra.getLast();

        if (cabeca.x < 0 || cabeca.x >= LARGURA_JANELA || cabeca.y < 0 || cabeca.y >= ALTURA_JANELA) {
            return true;
        }

        for (int i = 0; i < cobra.size() - 1; i++) {
            if (cabeca.equals(cobra.get(i))) {
                return true;
            }
        }

        return false;
    }

    private void avancarFase() {
        if (faseAtual < FASES_TOTAL) {
            faseAtual++;
            velocidade++;
        }
    }

    @Override
    public void run() {
        while (emJogo) {
            mover();
            repaint();

            try {
                Thread.sleep(DELAY_INICIAL / velocidade);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int tecla = e.getKeyCode();

        if ((tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_RIGHT)
                && Math.abs(tecla - direcao) != 2) {
            direcao = tecla;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Jogo Snake Colorido");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new JogoSnakeColorido());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
