import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Application View.
 *
 * @author Connor Wong
 */
public class SetCardGameView extends JFrame {
    private ArrayList<JLabel> hintBoxes, selectBoxes, isSetBorderBoxes, isNotSetBorderBoxes;
    private ArrayList<SetCard> select;
    private SetCardGameModel game;
    private int buttonPosX, buttonPosY, setsExisting, setsFound, yourScorePosX, yourScorePosY;
    private JButton[] boardCards;
    private JLabel setCount, time, found, title, gOver, yourScore, gamePaused;
    private JButton checkSet, newGame, help, hint, start;
    private JPanel frame, mainMenu, gameOver, pause;
    private double timeLeft, borderTimeLeft;
    private Timer timer, borderTimer;
    private ActionListener countDown, showBorder;
    private AudioInputStream audioStream;
    private Clip foundClip, errorClip;

    final static int NO_SETS_ON_BOARD = 0;
    final static int SET_BONUS_TIME = 5000;
    final static int SET_HINT_PENALTY = 10000;
    final static int SET_SIZE = 3;
    final static int SET_TIME = 300000;
    final static int BORDER_TIME = 500;
    final static int TIME_DECREMENT = 100;
    final static String IS_SET_SOUND = "sounds/ding.wav";
    final static String NOT_SET_SOUND = "sounds/error.wav";

    public SetCardGameView() {
        initTimer();
        timer = new Timer(TIME_DECREMENT, countDown);
        init();
        setCount.setText("Number of sets left: " + game.getCount());
        setContentPane(mainMenu);
        setTitle("SET!");
        setLayout(null);
        pack();
        frame.setBackground(Color.white);
        mainMenu.setBackground(Color.white);
        pause.setBackground(Color.white);
        gameOver.setBackground(Color.white);
        setSize(800, 765);
        setResizable(false);
        setVisible(true);
        loadButtonsInMain();
        help();
        newGame();
        hint();
        checkSet();
        startButton();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int x = JOptionPane.showConfirmDialog(null, "Are you sure you want to close the window?", "Really Closing?", JOptionPane.YES_NO_OPTION);
                if (x == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                if (x == JOptionPane.NO_OPTION || x == JOptionPane.CLOSED_OPTION) {
                    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public void init() {
        hintBoxes = new ArrayList<>();
        selectBoxes = new ArrayList<>();
        isSetBorderBoxes = new ArrayList<>();
        isNotSetBorderBoxes = new ArrayList<>();
        select = new ArrayList<>();
        game = new SetCardGameModel();
        buttonPosX = 32;
        buttonPosY = 20;
        boardCards = new JButton[game.getBoard().size()];
        setCount = new JLabel();
        checkSet = new JButton("SET!");
        newGame = new JButton("New Game");
        help = new JButton("Help");
        hint = new JButton("Hint");
        frame = new JPanel();
        setsExisting = game.getCount();
        time = new JLabel();
        timeLeft = SET_TIME;
        borderTimeLeft = BORDER_TIME;
        setsFound = 0;
        found = new JLabel("Sets Found: " + setsFound);
        mainMenu = new JPanel();
        start = new JButton("Play!");
        title = new JLabel("SET!");
        gameOver = new JPanel();
        gOver = new JLabel("GAME OVER");
        yourScore = new JLabel();
        pause = new JPanel();
        yourScorePosX = 0;
        yourScorePosY = 0;
        gamePaused = new JLabel("PAUSED");
    }

    public void loadPaused() {
        gamePaused.setBounds(236, 300, 328, 100);
        gamePaused.setFont(gamePaused.getFont().deriveFont(72f));
        pause.add(gamePaused);
        revalidate();
        repaint();
    }

    public void loadButtonsInMain() {
        title.setBounds(309, 300, 183, 100);
        title.setFont(title.getFont().deriveFont(72f));
        mainMenu.add(title);
        start.setBounds(300, 437, 200, 50);
        start.setBackground(Color.white);
        mainMenu.add(start);
        help.setBounds(300, 507, 200, 50);
        help.setBackground(Color.white);
        mainMenu.add(help);
    }

    public void loadButtonsInFrame() {
        checkSet.setBounds(703, 682, 65, 20);
        checkSet.setBackground(Color.white);
        frame.add(checkSet);
        newGame.setBounds(32, 682, 130, 20);
        newGame.setBackground(Color.white);
        frame.add(newGame);
        help.setBounds(175, 682, 70, 20);
        help.setBackground(Color.white);
        frame.add(help);
        hint.setBounds(625, 682, 65, 20);
        hint.setBackground(Color.white);
        frame.add(hint);
        checkSet.setEnabled(false);
        setCount.setBounds(288, 685, 175, 10);
        time.setBounds(288, 700, 175, 10);
        frame.add(time);
        found.setBounds(473, 685, 175, 10);
        frame.add(found);
        frame.add(setCount);
    }

    public void startButton() {
        start.addActionListener(e -> {
                    setContentPane(frame);
                    setLayout(null);
                    if (!game.boardHasSets()) {
                        newBoard();
                    }
                    loadCards();
                    loadButtonsInFrame();
                    revalidate();
                    repaint();
                    timer.start();
                }
        );
    }

    public void help() {
        help.addActionListener(e -> {
                    if (getContentPane().equals(mainMenu)) {
                        helpDialog();
                    }
                    if (getContentPane().equals(frame)) {
                        timer.stop();
                        loadPaused();
                        pause.setLayout(null);
                        setContentPane(pause);
                        repaint();
                        revalidate();
                        helpDialog();
                        repaint();
                        revalidate();
                        timer.start();
                    }
                }
        );
    }

    public void checkSet() {
        checkSet.addActionListener(e -> {
                    try {
                        audioStream = AudioSystem.getAudioInputStream(SetCardGameView.class.getResource(IS_SET_SOUND));
                        foundClip = AudioSystem.getClip();
                        foundClip.open(audioStream);

                        audioStream = AudioSystem.getAudioInputStream(SetCardGameView.class.getResource(NOT_SET_SOUND));
                        errorClip = AudioSystem.getClip();
                        errorClip.open(audioStream);
                    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException error) {
                        System.out.println("Could not open file");
                    }
                    for (int i = 0; i < selectBoxes.size(); i++) {
                        selectBoxes.get(i).setVisible(false);
                        frame.revalidate();
                        frame.repaint();
                    }
                    if (game.wasFound(select)) {
                        select = new ArrayList<>();
                        JOptionPane.showMessageDialog(null, "You already found this set...");
                    } else if (game.isSet(select)) {
                        foundClip.start();
                        setsFound++;
                        found.setText("Sets Found: " + setsFound);
                        timeLeft += SET_BONUS_TIME;
                        game.updateSetsFound(select);
                        for (int j = 0; j < select.size(); j++) {
                            isSetBorderBoxes.get(game.getBoard().indexOf(select.get(j))).setVisible(true);
                        }
                        select = new ArrayList<>();
                        //JOptionPane.showMessageDialog(null, "It's a set!");
                        setsExisting--;
                        hint.setEnabled(true);
                        setCount.setText("Number of sets left: " + setsExisting);
                        frame.repaint();
                        frame.revalidate();
                        borderTimer = new Timer(TIME_DECREMENT, initBorderTimer(isSetBorderBoxes));
                        borderTimer.start();
                        if (setsExisting == NO_SETS_ON_BOARD) {
                            newBoard();
                        }
                    } else {
                        errorClip.start();
                        for (int k = 0; k < select.size(); k++) {
                            isNotSetBorderBoxes.get(game.getBoard().indexOf(select.get(k))).setVisible(true);
                        }
                        select = new ArrayList<>();
                        borderTimer = new Timer(TIME_DECREMENT, initBorderTimer(isNotSetBorderBoxes));
                        borderTimer.start();
                        //JOptionPane.showMessageDialog(null, "Not a set...");
                    }
                    checkSet.setEnabled(false);
                }
        );
    }

    public void hint() {
        hint.addActionListener(e -> {
                    timeLeft -= SET_HINT_PENALTY;
                    int rand = (int) (Math.random() * SET_SIZE);
                    boolean cardNotFound = false;
                    int i = 0;
                    while (!cardNotFound) {
                        if (!game.wasFound(game.getPossibleSets().get(i))) {
                            hintBoxes.get(game.getBoard().indexOf(game.getPossibleSets().get(i).get(rand))).setVisible(true);
                            cardNotFound = true;
                            hint.setEnabled(false);
                        }
                        i++;
                    }
                }
        );
    }

    public void newGame() {
        newGame.addActionListener(e -> {
                    if (JOptionPane.showConfirmDialog(null, "Are you sure you want to start a new game?", "Restart", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        if (getContentPane().equals(gameOver)) {
                            setContentPane(frame);
                        }
                        restart();
                    }
                }
        );
    }

    public void loadCards() {
        for (int i = 0; i < game.getBoard().size(); i++) {
            boardCards[i] = new JButton(new ImageIcon(game.getFileNames().get(i)));
        }
        for (JButton tempButton : boardCards) {
            if (buttonPosX > 544) {
                buttonPosX = 32;
                buttonPosY += 167;
            }
            if (buttonPosX <= 544 && buttonPosY <= 682) {
                tempButton.setBounds(buttonPosX, buttonPosY, 224, 135);
                tempButton.setBackground(Color.white);
                frame.add(tempButton);
            }
            buttonPosX += 256;
            JLabel selected = new JLabel(new ImageIcon(SetCardGameView.class.getResource("images/highlight.png")));
            JLabel hinted = new JLabel(new ImageIcon(SetCardGameView.class.getResource("images/hinted.png")));
            JLabel isSetBorder = new JLabel(new ImageIcon(SetCardGameView.class.getResource("images/isset.png")));
            JLabel isNotSetBorder = new JLabel(new ImageIcon(SetCardGameView.class.getResource("images/isnotset.png")));
            hinted.setBounds(tempButton.getX() - 5, tempButton.getY() - 5, 234, 145);
            selected.setBounds(tempButton.getX() - 5, tempButton.getY() - 5, 234, 145);
            isSetBorder.setBounds(tempButton.getX() - 5, tempButton.getY() - 5, 234, 145);
            isNotSetBorder.setBounds(tempButton.getX() - 5, tempButton.getY() - 5, 234, 145);
            frame.add(hinted);
            frame.add(selected);
            frame.add(isSetBorder);
            frame.add(isNotSetBorder);
            hinted.setVisible(false);
            selected.setVisible(false);
            isSetBorder.setVisible(false);
            isNotSetBorder.setVisible(false);
            hintBoxes.add(hinted);
            selectBoxes.add(selected);
            isSetBorderBoxes.add(isSetBorder);
            isNotSetBorderBoxes.add(isNotSetBorder);
            tempButton.addActionListener(e -> {
                        int cardIndex = Arrays.asList(boardCards).indexOf(tempButton);
                        if (!select.contains(game.getBoard().get(cardIndex))) {
                            select.add(game.getBoard().get(cardIndex));
                            hinted.setVisible(false);
                            selected.setVisible(true);
                            frame.revalidate();
                            frame.repaint();
                        } else {
                            selected.setVisible(false);
                            select.remove(game.getBoard().get(cardIndex));
                            frame.revalidate();
                            frame.repaint();
                        }
                        if (select.size() == SET_SIZE) {
                            checkSet.setEnabled(true);
                        }
                        if (select.size() != SET_SIZE) {
                            checkSet.setEnabled(false);
                        }
                    }
            );
        }
    }

    public void helpDialog() {
        JOptionPane.showMessageDialog(null, "Welcome to Set! This is a card game where you are supposed\n" +
                "to find a set. A set consists of three cards. Each card has\n" +
                "4 properties: The number of shapes, the color, the type of\n" +
                "shading, and the type of shape. There are 3 variations of\n" +
                "each property. A set consists of three cards which, for\n" +
                "each property, have either all of the same or all different.\n" +
                "For example:\n" +
                "          1 Red Striped Diamond\n" +
                "          2 Red Solid Ovals\n" +
                "          3 Red Open Squiggles\n" +
                "would be a set. On the other hand,\n" +
                "          1 Red Striped Diamond\n" +
                "          1 Red Solid Oval\n" +
                "          2 Red Open Squiggles\n" +
                "would not be, because one of the cards has 2 shapes and\n" +
                "the other two have 1. Also, for all pairs of cards, there\n" +
                "is always one that makes a set with them. Think you got it?\n" +
                "Try it out!", "How to Play", JOptionPane.CLOSED_OPTION);
        if (getContentPane().equals(pause)) {
            setContentPane(frame);
        }
        frame.revalidate();
        frame.repaint();
    }

    public void restart() {
        timeLeft = SET_TIME;
        setsFound = 0;
        time.setForeground(Color.darkGray);
        found.setText("Sets Found: " + setsFound);
        frame.setLayout(null);
        newBoard();
        timer.start();
    }

    public void newBoard() {
        do {
            boardCards = new JButton[game.getBoard().size()];
            selectBoxes = new ArrayList<>();
            hintBoxes = new ArrayList<>();
            isSetBorderBoxes = new ArrayList<>();
            isNotSetBorderBoxes = new ArrayList<>();
            select = new ArrayList<>();
            buttonPosX = 32;
            buttonPosY = 20;
            hint.setEnabled(true);
            frame.removeAll();
            game.reset();
            setsExisting = game.getCount();
            loadCards();
            loadButtonsInFrame();
            setCount.setText("Number of sets left: " + game.getCount());
            revalidate();
            repaint();
        }
        while (!game.boardHasSets());
    }

    public void initTimer() {
        countDown = e -> {
            timeLeft -= TIME_DECREMENT;
            SimpleDateFormat df = new SimpleDateFormat("mm:ss");
            if(timeLeft <= 10000) {
                time.setForeground(Color.red);
            }
            time.setText(df.format(timeLeft));
            if (timeLeft <= 0) {
                timer.stop();
                timeLeft = SET_TIME;
                setContentPane(gameOver);
                setLayout(null);
                gOver.setBounds(207, 300, 387, 100);
                gOver.setFont(gameOver.getFont().deriveFont(64f));
                gameOver.add(gOver);
                if (setsFound >= 0 && setsFound < 10) {
                    yourScorePosX = 318;
                    yourScorePosY = 166;
                }
                if (setsFound >= 10 && setsFound < 100) {
                    yourScorePosX = 305;
                    yourScorePosY = 191;
                }
                if (setsFound >= 100 && setsFound < 1000) {
                    yourScorePosX = 293;
                    yourScorePosY = 216;
                }
                if (setsFound >= 1000) {
                    yourScorePosX = 280;
                    yourScorePosY = 241;
                }
                yourScore.setBounds(yourScorePosX, 450, yourScorePosY, 100);
                yourScore.setText("Score: " + setsFound);
                yourScore.setFont(yourScore.getFont().deriveFont(36f));
                newGame.setBounds(336, 682, 130, 20);
                gameOver.add(newGame);
                gameOver.add(yourScore);
                gameOver.repaint();
                gameOver.revalidate();
            }
        };
    }

    public ActionListener initBorderTimer(ArrayList<JLabel> borderBox) {
        showBorder = e -> {
            borderTimeLeft -= TIME_DECREMENT;
            if (borderTimeLeft <= 0) {
                for (int j = 0; j < borderBox.size(); j++) {
                    borderBox.get(j).setVisible(false);
                }
                borderTimer.stop();
                borderTimeLeft = BORDER_TIME;
            }
        };
        return showBorder;
    }
}