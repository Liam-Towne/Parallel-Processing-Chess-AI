import java.util.ArrayList;
import java.util.Random;

/**
 * An AI to run the computer mode in notchess
 * 
 * @author Liam Towne & Sean Omalley
 * @version Fall 2024
 */
public class notChessAI {

    private notchess board; // a reference to the notchess object
    private char color = 'B'; // which color the computer runs

    // a constructer which takes a notchess object as parameter
    public notChessAI(notchess board) {
        this.board = board;
    }
    
    // gets the next move the computer will make 
    public String getMove() {
        long a = System.currentTimeMillis();
        Tree treeSearch = new Tree(board, board.numThreads, board.maxNodes);
        notchess nextBoard;
        if (board.numThreads > 1){ 
        nextBoard = treeSearch.findBestMoveParallel();
        }
        else{
            nextBoard = treeSearch.findBestMove();
        }

        String nextMove = nextBoard.moves.peek();
        long b = System.currentTimeMillis();
        System.out.println("Time: " + (b - a) + "ms\n");
        return nextMove;
    }

    //generates all possible moves the computer could make
    public ArrayList<String> generateMoves() {
        ArrayList<String> moves = new ArrayList<String>();
        
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board.buttons[row][col];
                if (piece.getColor() == board.turn) {
                    int rowMove = row + 1;
                    String colMove;
                    switch (col) {
                        case 0:
                            colMove = "A";
                            break;
                        case 1:
                            colMove = "B";
                            break;
                        case 2:
                            colMove = "C";
                            break;
                        case 3:
                            colMove = "D";
                            break;
                        case 4:
                            colMove = "E";
                            break;
                        case 5:
                            colMove = "F";
                            break;
                        case 6:
                            colMove = "G";
                            break;
                        case 7:
                            colMove = "H";
                            break;
                        default:
                            colMove = "";
                    }
                    String curMove = piece.getSymbol() + colMove + Integer.toString(rowMove) + colMove + Integer.toString(rowMove);
                    piece.getValidMoves(board.buttons, curMove);
                                       
                    moves.addAll(piece.getCaptureArrayList());              
                    moves.addAll(piece.getValidArrayList());                    
                }
            }
        }
        return moves;
    }

    // attempts to generate the best move the computer could make 
    public String pickMove(ArrayList<String> moves) {
        if (moves.size() == 0) {
            return null;
        }
        int bestScore = Integer.MIN_VALUE; 
        String bestMove = null;
        ArrayList<String> bestMoves = new ArrayList<String>(); // an array of the moves tied for best
        
        // score each possible move
        for (String move : moves) {
            int score = heuristic(move);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        // randomly select a move from the moves tied for best
        Random rand = new Random();
        bestMove = bestMoves.get(rand.nextInt(bestMoves.size()));
        return bestMove;
    }
    
    public String pickMoveRandom(ArrayList<String> moves) {
        if (moves.size() == 0) {
            return null;
        }
        
        Random rand = new Random();
        String newMove = moves.get(rand.nextInt(moves.size()));
        return newMove;
    }

    // evaluates the effectiveness of a given move based on what kind of piece it is taking 
    private int heuristic(String move) {
        int score = 0;

        char startCol = move.charAt(1);
        int startRow = Character.getNumericValue(move.charAt(2));
        char destCol = move.charAt(3);
        int destRow = Character.getNumericValue(move.charAt(4));

        ChessPiece sourcePiece = board.buttons[startRow - 1][startCol - 'A'];
        ChessPiece destPiece = board.buttons[destRow - 1][destCol - 'A'];

        if (destPiece.getColor() != 'E') {
            score += getPieceValue(destPiece);
        }

        return score;
    }
    
    // evaluates the value a piece has when taken, 
    // higher for pieces with less capture options, 
    // and lower score for pieces with more capture options.
    public int getPieceValue(ChessPiece piece) {
        if (piece == null || piece.getColor() == 'E') {
            return 0;
        }
    
        int value;
        switch (piece.getSymbol()) {
            case 'P': // Pawn
                value = 1;
                break;
            case 'N': // Knight
                value = 3;
                break;
            case 'B': // Bishop
                value = 5;
                break;
            case 'R': // Rook
                value = 5;
                break;
            case 'Q': // Queen
                value = 10;
                break;
            case 'K': // King
                value = 10; 
                break;
            default:
                value = 0;
                break;
        }
    
        return value;
    }

}
