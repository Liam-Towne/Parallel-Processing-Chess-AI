import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
*   This program creates the panel containing the move display label, the entire chessboard and pieces, and
*   the text move input area. This program also provides game functionality for the game of Not Chess.
*
*   @author Liam Towne & Sean Omalley
*   @version Fall 2024
*
*/
public class notchess implements Runnable, ActionListener {
    private JLabel moveLabel; // label for move at top of board
    public ChessPiece[][] buttons; // array of buttons for grid
    public JTextField moveField; // test box for inputting moves at bottom of board
    private JPanel gridPanel; // panel for containing buttons and column and row labels
    private boolean firstClick = true; // true when clicking on source button, false when clicking on destination button
    private ChessPiece selected; // keep track of the selected button when moving
    private JFrame frame;
    private JComboBox modeComboBox;
    private JSpinner threadsSpinner;
    private JSpinner nodesSpinner;
    
    public boolean computerMode = true;    
    public Stack<String> moves = new Stack(); //list of all moves made
    public char turn = 'W'; //determines whose turn it is
    public int bPiecesLeft = 16, wPiecesLeft = 16; //the amount of pieces each player has left
    public boolean isInCheck = false;
    public char gamemode = 'H'; //H = human C = computer

    //command line args
    int maxNodes;
    int numThreads;


    /**
     * The run method to set up the graphical user interface
     */    
    @Override
    public void run() {
        createInitialGUI();
        
    }
    
    public void createInitialGUI() {
        frame = new JFrame("notChess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        /*String[] modes = {"Normal", "Computer Mode"};
        modeComboBox = new JComboBox<>(modes);
        modeComboBox.setSelectedIndex(0);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        panel.add(modeComboBox, c);*/

        threadsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 128, 1));
        panel.add(threadsSpinner);

        nodesSpinner = new JSpinner(new SpinnerNumberModel(100, 100, 100000, 100));
        panel.add(nodesSpinner);

        frame.add(panel, BorderLayout.CENTER);

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*if (modeComboBox.getSelectedItem().equals(modes[1])) {
                    computerMode = true;
                }*/
                maxNodes = (Integer)nodesSpinner.getValue();
                numThreads = (Integer)threadsSpinner.getValue();
                createMainGUI();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(doneButton);
        frame.add(buttonPanel, BorderLayout.PAGE_END);

        frame.setVisible(true);
    }
    
    public void createMainGUI () {
        frame.getContentPane().removeAll();
        frame.repaint();
        
        //the usual JFrame setup steps
        frame.setTitle("Not Chess");
        frame.setPreferredSize(new Dimension(700, 700));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel boardPanel = new JPanel(new BorderLayout());

        //Where the moves will be displayed (or INVALID, NEEDS TO CAPTURE, etc.)
        JPanel movePanel = new JPanel();
        moveLabel = new JLabel("Move: ");
        movePanel.add(moveLabel);
        boardPanel.add(movePanel, BorderLayout.NORTH);
        
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(9,9)); // 9x9 grid for labels


        // Add empty label for top-left corner
        gridPanel.add(new JLabel(""));

        // Add column labels (A-H)
        for (char c = 'A'; c <= 'H'; c++) {
            JLabel label = new JLabel(Character.toString(c));
            label.setHorizontalAlignment(JLabel.CENTER); // center text horizontally
            gridPanel.add(label);
        }
        
        // Add empty label for top-right corner
        gridPanel.add(new JLabel(""));

        // Add row labels (8-1)
        buttons = new ChessPiece[8][8];
        for (int r = 7; r >= 0; r--) {

            JLabel label = new JLabel(Integer.toString(r + 1));
            label.setHorizontalAlignment(JLabel.CENTER);
            gridPanel.add(label);

            // Buttons for each column in this row
            for (int c = 0; c < 8; c++) {

                ChessPiece piece = null;
                Color backgroundColor;

                //for the checkerboard background colors
                if ((r + c) % 2 == 0) {
                    backgroundColor = Color.LIGHT_GRAY;
                } else {
                    backgroundColor = Color.GRAY;
                }

                //all of the black pieces
                if (r == 7) {
                    // Back rank
                    switch (c) {
                        //cases for the black Rooks
                        case 0:
                        case 7:
                            piece = new Rook('R', 'B'); // Rooks
                            break;
                        //cases for the black Knights
                        case 1:
                        case 6:
                            piece = new Knight('N', 'B'); // Knights
                            break;
                        //cases for the black Bishops
                        case 2:
                        case 5:
                            piece = new Bishop('B', 'B'); // Bishops
                            break;
                        //cases for the black Queen
                        case 3:
                            piece = new Queen('Q', 'B'); // Queen
                            break;
                        //cases for the black King
                        case 4:
                            piece = new King('K', 'B'); // King
                            break;
                    }
                } 
                //all of the white pieces
                else if (r == 0) {
                    switch (c) {
                        //cases for the white Rooks
                        case 0:
                        case 7:
                            piece = new Rook('R', 'W'); // Rooks
                            break;
                        //cases for the white Knights
                        case 1:
                        case 6:
                            piece = new Knight('N', 'W'); // Knights
                            break;
                        //cases for the white Bishops
                        case 2:
                        case 5:
                            piece = new Bishop('B', 'W'); // Bishops
                            break;
                        //cases for the white Queen
                        case 3:
                            piece = new Queen('Q', 'W'); // Queen
                            break;
                        //cases for the white King
                        case 4:
                            piece = new King('K', 'W'); // King
                            break;
                    }
                } 
                //this row is where all the white Pawns start out
                else if (r == 1) {
                    piece = new Pawn('P', 'W');
                } 
                //this row is where all the black Pawns start out
                else if (r == 6) {
                    // Pawns
                    piece = new Pawn('P', 'B');
                }

                //if the piece if not null, set its background, add an actionlistener to it, add it to the
                //buttons arraylist, and add it to the gridPanel
                if (piece != null) {
                    piece.setBackground(backgroundColor);
                    piece.addActionListener(this);
                    buttons[r][c] = piece;
                    gridPanel.add(piece);
                } 
                //else it is an Empty button, do the same things with an Empty button object
                else {
                    Empty button = new Empty('E', 'E');
                    button.setBackground(backgroundColor);
                    button.addActionListener(this);
                    buttons[r][c] = button;
                    gridPanel.add(button);
                }
            }
            gridPanel.add(new JLabel(""));
        }
        
        
        
        // Panel for move input
        JPanel bottomPanel = new JPanel();
        JLabel enterMoveLabel = new JLabel("Enter Move:");
        moveField = new JTextField(10);
        JButton moveButton = new JButton("Move");
        moveButton.addActionListener(new ActionListener() {

            /**
             * This gets the input text form of th move and processes it as it needs to
             * 
             * @param e the action event
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String move = moveField.getText(); //get the inputted text move
                char col = move.charAt(1); //get the column
                int row = Character.getNumericValue(move.charAt(2)); //get the row

                //ChessPiece movePiece = getPiece(col, row); //get the piece at that row/column location

                //if it is a valid move, proccess it
                if (isValidMove(move)) {
                    processMove(move);
                    //moveField.setText("");
                    //moveLabel.setText("Move: " + move);
                } else {
                    moveLabel.setText("Move: INVALID!");
                }
            }
            
        });

        bottomPanel.add(enterMoveLabel);
        bottomPanel.add(moveField);
        bottomPanel.add(moveButton);
        boardPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        boardPanel.add(gridPanel);
        frame.add(boardPanel);
        
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Process what buttons the user presses and calls the appropriate methods the validate the move 
     * and perform it
     * 
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //if this is the first click of a move
        if (firstClick) {
            selected = (ChessPiece) e.getSource();
            
            if (selected.getColor() != turn) {
                firstClick = true;
            }
            else {
                firstClick = false;
            }            
        } else { //else it is the second click of a move
            ChessPiece dest = (ChessPiece) e.getSource();
            String move = getMoveString(selected, dest);

            //if the move is valid, process it
            if (isValidMove(move)) {
                processMove(move);
            } 
            else {
                moveField.setText("");
                firstClick = true;
            }
        }
    }
    
    /**
     * Get the ChessPiece object at a specific location on the chessboard/buttons array
     * 
     * @param col the specified column
     * @param row the specified row
     * @return the ChessPiece at the column and row
     */
    private ChessPiece getPiece (char col, int row) {
        
        int rowIndex = row - 1;
        int colIndex = col - 'A';

        //make sure we are within the boundaries
        if (rowIndex < 0 || rowIndex > 7 || colIndex < 0 || colIndex > 7) {
            return null;
        }

        //get the chesspiece at that row and column
        ChessPiece piece = buttons[rowIndex][colIndex];
    
        return piece;
    }    

    /**
     * This method updates the board. It uses the clears the JPanel and uses the Buttons 2d array to 
     * recreate all of the buttons in the JPanel with the correct type, color, and location as shown 
     * in the Buttons 2d array
     */
    private void updateBoard() {
        gridPanel.removeAll();

        // Add empty label for top-left corner
        gridPanel.add(new JLabel(""));

        // Add column labels (A-H)
        for (char c = 'A'; c <= 'H'; c++) {
            JLabel label = new JLabel(Character.toString(c));
            label.setHorizontalAlignment(JLabel.CENTER); // center text horizontally
            gridPanel.add(label);
        }
        
        // Add empty label for top-right corner
        gridPanel.add(new JLabel(""));

        // Add row labels (8-1)
        for (int r = 7; r >= 0; r--) {
            JLabel label = new JLabel(Integer.toString(r + 1));
            label.setHorizontalAlignment(JLabel.CENTER);
            gridPanel.add(label);
            // Buttons for each column in this row
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = null;
                
                Color backgroundColor;
                if ((r + c) % 2 == 0) {
                    backgroundColor = Color.LIGHT_GRAY;
                } else {
                    backgroundColor = Color.GRAY;
                }

                //get the respective piece from the buttons array
                piece = buttons[r][c];

                //if it isnt null, set its background color and add it to the JPanel object
                if (piece != null) {
                    piece.setBackground(backgroundColor);
                    gridPanel.add(piece);
                }
            }
            gridPanel.add(new JLabel(""));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }
    
    public boolean isKingInCheck(char color) {
        ChessPiece king = findKing(color);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = buttons[r][c];
                if (piece.getColor() != color && piece.getColor() != 'E') {
                    String tmpMove = getMoveString(piece, piece);
                    piece.getValidMoves(buttons, tmpMove);
                    if (piece.getCaptureArrayList().contains(getMoveString(piece, king))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChessPiece findKing(char color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = buttons[r][c];
                if (piece.getSymbol() == 'K' && piece.getColor() == color) {
                    return piece;
                }
            }
        }
        return null;
    }    
    
    /**
     * Check if a move is valid
     * @param move The String representation of the move to check
     * @return True if the move is valid, false otherwise
     */
    public boolean isValidMove(String move) {
        //make sure the move is generally in the correct format
        if (move == null || move.length() != 5) {
            return false;
        }
        //get each piece of the String move
        char pieceChar = move.charAt(0);
        char startCol = move.charAt(1);
        int startRow = Character.getNumericValue(move.charAt(2));
        char destCol = move.charAt(3);
        int destRow = Character.getNumericValue(move.charAt(4));
        
        ChessPiece piece = buttons[startRow - 1][startCol - 'A'];
        
        //make sure the start column is within the correct range
        switch (startCol) {
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
                break;
            default:
                return false;
        }
        
        //make sure the desination column is within the correct range
        switch (destCol) {
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
                break;
            default:
                return false;
        }
        
        //make the locations given in the String move are within the correct boundaries of the chessboard
        if (startRow < 0 || destRow < 0 || startRow > 8 || destRow > 8) {
            return false;
        }

        //arraylist to store all valid moves of all pieces of a certain color
        ArrayList<String> allCaptureMoves = new ArrayList<String>();
        
        //go through every piece, if it is the correct color, add all of its valid moves to the appropriate arraylist
        for (int r = 0; r < 8; r++) {
           for (int c = 0; c < 8; c++) {
                ChessPiece tmp = buttons[r][c];
                if (tmp.getColor() == turn) {
                    String tmpMove = getMoveString(tmp, tmp);
                    buttons[r][c].getValidMoves(buttons, tmpMove);
                    allCaptureMoves.addAll(buttons[r][c].getCaptureArrayList());
                }
            }
        }
        
        piece.getValidMoves(buttons, move); //call this to update the pieces personal valid moves arraylist        
        
        if (!piece.isValidMove(move)) {
            return false;
        }
        
        ChessPiece originalSource = buttons[startRow - 1][startCol - 'A'];
        ChessPiece originalDest = buttons[destRow - 1][destCol - 'A'];        

        // Simulate the move
        ChessPiece emptyPiece = new Empty('E','E');
        buttons[startRow - 1][startCol - 'A'] = emptyPiece;
        buttons[destRow - 1][destCol - 'A'] = piece;

        boolean wouldBeInCheck = isKingInCheck(turn);

        // Undo the move
        buttons[startRow - 1][startCol - 'A'] = originalSource;
        buttons[destRow - 1][destCol - 'A'] = originalDest;

        // Update the action listeners back
        //buttons[startRow - 1][startCol - 'A'].addActionListener(this);
        //buttons[destRow - 1][destCol - 'A'].addActionListener(this);

        if (wouldBeInCheck) {
            return false;
        }

        return true;
    }

     /**
     * Perform a move
     * 
     * @param move The move to perform
     * @return True if the move was successful, false otherwise
     */
    public boolean processMove(String move) {

        //get the individual parts of the String move
        char piece = move.charAt(0);
        char startCol = move.charAt(1);
        int startRow = Character.getNumericValue(move.charAt(2));
        char destCol = move.charAt(3);
        int destRow = Character.getNumericValue(move.charAt(4));

        // Find the source and destination buttons
        ChessPiece sourceButton = buttons[startRow - 1][startCol - 'A'];
        ChessPiece destButton = buttons[destRow - 1][destCol - 'A'];

        //get the color of the piece that is attempting to move
        char curMover = sourceButton.getColor();

        //if it is the correct persons turn
        if (curMover == turn) {

            // Check if source button contains the expected piece
            if (!(sourceButton.getSymbol() == piece)) {
                moveLabel.setText("Move: INVALID!");
                return false;
            }
            
            if (destButton.getColor() != sourceButton.getColor() && destButton.getColor() != 'E') {
                if (destButton.getColor() == 'W') wPiecesLeft--;
                if (destButton.getColor() == 'B') bPiecesLeft--;
            }

            // Update board and GUI
            ChessPiece emptyPiece = new Empty('E','E');
            emptyPiece.addActionListener(this);
            buttons[startRow - 1][startCol - 'A'] = emptyPiece;     

            ChessPiece newPiece = sourceButton.copy(); // Create a copy of the source piece
            buttons[destRow - 1][destCol - 'A'] = newPiece;
            buttons[destRow - 1][destCol - 'A'].addActionListener(this);
            
            destButton.setSymbol(newPiece.getSymbol());
            destButton.setColor(newPiece.getColor());
            Color tmp;
            if (newPiece.getColor() == 'W') {
                tmp = Color.WHITE;
            } else {
                tmp = Color.BLACK;
            }
            destButton.setForeground(tmp);
                
            if (piece == 'P') {
                Pawn pawnPiece = (Pawn) newPiece;
                if (pawnPiece.isCrowned(destRow - 1, destCol - 'A')) {
                    newPiece = new Queen('Q', newPiece.getColor());
                    buttons[destRow - 1][destCol - 'A'] = newPiece;
                    buttons[destRow - 1][destCol - 'A'].addActionListener(this);

                    destButton.setSymbol(newPiece.getSymbol());
                    destButton.setColor(newPiece.getColor());
                }
            }
            
            

            //Reset selection state
            selected = null;
            firstClick = true;
            
            moveField.setText("");
            moveLabel.setText("Move: " + move);
            
            moves.push(move);
            
            updateBoard(); //call the updateBoard() method to update the board

            //if white just went, it is now blacks turn
            if (curMover == 'W') {
                turn = 'B';
            }
            //if black just went, it is now whites turn
            else if (curMover == 'B') {
                turn = 'W';
            }

            if (isKingInCheck(turn)) {
                if (!canAnyMovePreventCheck(turn)) {
                    moveLabel.setText("Move: CHECKMATE! " + ((turn == 'W') ? "Black" : "White") + " wins!");
                    turn = 'O'; // Game over
                    return true;
                } else {
                    moveLabel.setText("Move: " + ((turn == 'W') ? "White" : "Black") + " is in CHECK!");
                }
            }  
        
            if (computerMode && turn == 'B') {
                notChessAI ai = new notChessAI(this);
                String aiMove = ai.getMove();
                if (isValidMove(aiMove) )processMove(aiMove);                
            }
            
            return true;
        }
        //if the turn is 'O', then the game is over, do nothing
        else if (turn == 'O') {

        }
        //else the move is invalid, display it is invalid and set firstclick back to true
        else {
            moveLabel.setText("Move: INVALID!");

            firstClick = true;
            
            return false;
        }     
        
        return false;
    }
    
    public boolean processMoveAI(String move) {

        //get the individual parts of the String move
        char piece = move.charAt(0);
        char startCol = move.charAt(1);
        int startRow = Character.getNumericValue(move.charAt(2));
        char destCol = move.charAt(3);
        int destRow = Character.getNumericValue(move.charAt(4));

        // Find the source and destination buttons
        ChessPiece sourceButton = buttons[startRow - 1][startCol - 'A'];
        ChessPiece destButton = buttons[destRow - 1][destCol - 'A'];

        //get the color of the piece that is attempting to move
        char curMover = sourceButton.getColor();

        //if it is the correct persons turn
        if (curMover == turn) {

            // Check if source button contains the expected piece
            if (!(sourceButton.getSymbol() == piece)) {
                return false;
            }
            
            if (destButton.getColor() != sourceButton.getColor() && destButton.getColor() != 'E') {
                if (destButton.getColor() == 'W') wPiecesLeft--;
                if (destButton.getColor() == 'B') bPiecesLeft--;
            }

            // Update board and GUI
            ChessPiece emptyPiece = new Empty('E','E');
            buttons[startRow - 1][startCol - 'A'] = emptyPiece;     

            ChessPiece newPiece = sourceButton.copy(); // Create a copy of the source piece
            buttons[destRow - 1][destCol - 'A'] = newPiece;
            
            destButton.setSymbol(newPiece.getSymbol());
            destButton.setColor(newPiece.getColor());
                
            if (piece == 'P') {
                Pawn pawnPiece = (Pawn) newPiece;
                if (pawnPiece.isCrowned(destRow - 1, destCol - 'A')) {
                    newPiece = new Queen('Q', newPiece.getColor());
                    buttons[destRow - 1][destCol - 'A'] = newPiece;

                    destButton.setSymbol(newPiece.getSymbol());
                    destButton.setColor(newPiece.getColor());
                }
            }

            //Reset selection state
            selected = null;
            firstClick = true;
            
            moves.push(move);

            //if white just went, it is now blacks turn
            if (curMover == 'W') {
                turn = 'B';
            }
            //if black just went, it is now whites turn
            else if (curMover == 'B') {
                turn = 'W';
            }

            if (isKingInCheck(turn)) {
                if (!canAnyMovePreventCheck(turn)) {
                    turn = 'O'; // Game over
                    return true;
                }
            }
            
            return true;
        }
        //if the turn is 'O', then the game is over, do nothing
        else if (turn == 'O') {

        }
        //else the move is invalid, display it is invalid and set firstclick back to true
        else {
            firstClick = true;
            
            return false;
        }     
        
        return false;
    }
    
    public boolean canAnyMovePreventCheck(char color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessPiece piece = buttons[r][c];
                if (piece.getColor() == color) {
                    String tmpMove = getMoveString(piece, piece);
                    piece.getValidMoves(buttons, tmpMove);
                    ArrayList<String> validMoves = piece.getValidArrayList();
                    for (String move : validMoves) {
                        char startCol = move.charAt(1);
                        int startRow = Character.getNumericValue(move.charAt(2));
                        char destCol = move.charAt(3);
                        int destRow = Character.getNumericValue(move.charAt(4));

                        ChessPiece originalSource = buttons[startRow - 1][startCol - 'A'];
                        ChessPiece originalDest = buttons[destRow - 1][destCol - 'A'];

                        // Simulate the move
                        ChessPiece emptyPiece = new Empty('E','E');
                        buttons[startRow - 1][startCol - 'A'] = emptyPiece;
                        buttons[destRow - 1][destCol - 'A'] = piece;

                        boolean stillInCheck = isKingInCheck(color);

                        // Undo the move
                        buttons[startRow - 1][startCol - 'A'] = originalSource;
                        buttons[destRow - 1][destCol - 'A'] = originalDest; 

                        if (!stillInCheck) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Returns a string representation of the move
     * 
     * @param from the source button of the move
     * @param to the destination button of the move
     * @return a string representation of the move
     */
    private String getMoveString(ChessPiece from, ChessPiece to) {
        int rowFrom = -1, colFrom = -1, rowTo = -1, colTo = -1;
        for (int r = 0; r < 8; r++) {
           for (int c = 0; c < 8; c++) {
                //find the row and column of the source button
                if (from == buttons[r][c]) {
                   rowFrom = r;
                   colFrom = c;
                }
                //find the row and column of the destination button
                if (to == buttons[r][c]) {
                    rowTo = r;
                    colTo = c;
                }
           }
        }

        rowFrom++;
        rowTo++;

        char piece = from.getSymbol();
        String colFromL = "";
        String colToL = "";

        //find which column is the source
        switch (colFrom) {
            case 0:
                colFromL = "A";
                break;
            case 1:
                colFromL = "B";
                break;
            case 2:
                colFromL = "C";
                break;
            case 3:
                colFromL = "D";
                break;
            case 4:
                colFromL = "E";
                break;
            case 5:
                colFromL = "F";
                break;
            case 6:
                colFromL = "G";
                break;
            case 7:
                colFromL = "H";
        }
        //find which column is the destination
        switch (colTo) {
            case 0:
                colToL = "A";
                break;
            case 1:
                colToL = "B";
                break;
            case 2:
                colToL = "C";
                break;
            case 3:
                colToL = "D";
                break;
            case 4:
                colToL = "E";
                break;
            case 5:
                colToL = "F";
                break;
            case 6:
                colToL = "G";
                break;
            case 7:
                colToL = "H";
        }
        
        return piece + colFromL + rowFrom + colToL + rowTo;
    }
    
    /**
     * main method create and run the notchess game
     * 
     * @param args[] contains the gamemode the user chose to play
     */
    public static void main(String args[]) {

        javax.swing.SwingUtilities.invokeLater(new notchess());
    }
}