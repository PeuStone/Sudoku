package com.PeuStone.ui.custom.button;

import javax.swing.*;
import java.awt.event.ActionListener;

public class Reset extends JButton {

    public Reset(final ActionListener actionListener) {
        this.setText("Recomeçar jogo");
        this.addActionListener(actionListener);
    }
}
