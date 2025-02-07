package com.PeuStone.ui.custom.screen;

import com.PeuStone.model.Space;
import com.PeuStone.service.BoardService;
import com.PeuStone.service.NotifierService;
import com.PeuStone.ui.custom.button.CheckGameStatus;
import com.PeuStone.ui.custom.button.FinishGame;
import com.PeuStone.ui.custom.button.Reset;
import com.PeuStone.ui.custom.frame.MainFrame;
import com.PeuStone.ui.custom.input.NumberText;
import com.PeuStone.ui.custom.panel.MainPanel;
import com.PeuStone.ui.custom.panel.SudokuSector;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.PeuStone.service.EventEnum.CLEAR_SPACE;
import static javax.swing.JOptionPane.*;

public class MainScreen {

    private final static Dimension dimension = new Dimension(600,600);

    private final BoardService boardService;
    private final NotifierService notifierService;

    private JButton reset;
    private JButton checkGameStatus;
    private JButton finishGame;

    public MainScreen(final Map<String, String> gameConfig) {
        this.boardService = new BoardService(gameConfig);
        this.notifierService = new NotifierService();
    }

    public void buildMainScreen(){
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        for (int r = 0; r < 9; r+=3) {
            var endRow = r + 2;
            for (int c = 0; c < 9; c+=3) {
                var endCol = c + 2;
                var spaces = getSpacesFromSector(boardService.getSpace(), c, endCol, r , endRow);
                JPanel sector = generateSection(spaces);
                mainPanel.add(sector);
            }
        }
        addResetButton(mainPanel);
        addCheckStatusButton(mainPanel);
        addFinishButton(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private List<Space> getSpacesFromSector(final List<List<Space>> spaces, final int initCol, final int endCol, final int initRow, final int endRow){
        
        List<Space> spaceSector = new ArrayList<>();
        for (int r = initRow; r <= endRow; r++) {
            for (int c = initCol; c <= endCol; c++) {
                spaceSector.add(spaces.get(c).get(r));
            }
        }
        return spaceSector;
    }

    private JPanel generateSection(final List<Space> spaces){
        List<NumberText> fields = new ArrayList<>(spaces.stream().map(NumberText::new).toList());
        fields.forEach(t -> notifierService.subscribe(CLEAR_SPACE, t));
        return new SudokuSector(fields);
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
                notifierService.notify(CLEAR_SPACE);
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
            showMessageDialog(null, message);
        });
        mainPanel.add(checkGameStatus);
    }

    private void addFinishButton(final JPanel mainPanel) {
        finishGame = new FinishGame(e -> {
           if (boardService.gameIsFinished()){
               showMessageDialog(null,"Parabéns você concluiu o jogo");
               reset.setEnabled(false);
               checkGameStatus.setEnabled(false);
               finishGame.setEnabled(false);
           } else {
               showMessageDialog(null, "Seu jogo tem alguma inconsistência, ajuste e tente novamente");
           }
        });
        mainPanel.add(finishGame);
    }
}
