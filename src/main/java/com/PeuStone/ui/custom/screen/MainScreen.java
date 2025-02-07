package com.PeuStone.ui.custom.screen;

import com.PeuStone.service.BoardService;
import com.PeuStone.ui.custom.button.CheckGameStatus;
import com.PeuStone.ui.custom.button.FinishGame;
import com.PeuStone.ui.custom.button.Reset;
import com.PeuStone.ui.custom.frame.MainFrame;
import com.PeuStone.ui.custom.panel.MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600,600);

    private final BoardService boardService;

    private JButton reset;
    private JButton checkGameStatus;
    private JButton finishGame;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
    }

    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for (int r = 0; r < 9; r+=3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c+=3) {
                var endCol = c + 2;
            }
        }
        addResetButton(mainPanel);
        addCheckStatusButton(mainPanel);
        addFinishButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void addResetButton(final JPanel mainPanel) {
        reset = new Reset(e -> {
            var dialogResult = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente reiniciar o jogo?",
                    "Limpar o jogo",
                    YES_NO_OPTION,
                    QUESTION_MESSAGE
            );
            if (dialogResult == 0){
                boardService.reset();
            }
        });
        mainPanel.add(reset);
    }

    private void addCheckStatusButton(final JPanel mainPanel) {
        checkGameStatus = new CheckGameStatus(e -> {
            var hasErrors = boardService.hasErrors();
            var gameStatus = boardService.getStatus();
            var message = switch (gameStatus){
                case NON_STARTED -> "O jogo não foi iniciado!";
                case INCOMPLETE -> "O jogo está incompleto";
                case COMPLETE -> "O jogo está completo";
            };
            message += hasErrors ? " e contém erros" : " e não contém erros";
            JOptionPane.showMessageDialog(null, message);
        });
        mainPanel.add(checkGameStatus);
    }

    private void addFinishButton(final JPanel mainPanel) {
        finishGame = new FinishGame(e -> {
           if (boardService.gameIsFinished()){
               JOptionPane.showMessageDialog(null,"Parabéns você concluiu o jogo");
               reset.setEnabled(false);
               checkGameStatus.setEnabled(false);
               finishGame.setEnabled(false);
           } else {
               JOptionPane.showMessageDialog(null, "Seu jogo tem alguma inconsistência, ajuste e tente novamente");
           }
        });
        mainPanel.add(finishGame);
    }
}
