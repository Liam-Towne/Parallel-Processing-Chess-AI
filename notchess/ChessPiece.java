import java.util.ArrayList;
import javax.swing.JButton;

/**
 * The ChessPiece abstract class. This class extends JButton and defines the framework 
 * for each type of ChessPiece (Pawn, Rook, Knight, Bishop, Queen, and King)
 * 
 * @author Matthew Warner & Liam Towne
 * @version Spring 2023
 */
 
public abstract class ChessPiece extends JButton {
    public char type; // Each piece has a type 'P', 'R', 'N', 'B', 'Q', 'K'
    public char color; // 'W' or 'B'
    
    public ArrayList<String> validMoves = new ArrayList<String>(); // an array of valid moves
    public ArrayList<String> captureMoves = new ArrayList<String>(); //an array of capture moves
    
    //These constants are just used to hold the pathnames of each icon
    public static final String WHITE_PAWN_ICON_PATH = "../../../../images/white_pawn.png"; 
    public static final String WHITE_ROOK_ICON_PATH = "../../../../images/white_rook.png"; 
    public static final String WHITE_KNIGHT_ICON_PATH = "../../../../images/white_knight.png"; 
    public static final String WHITE_BISHOP_ICON_PATH = "../../../../images/white_bishop.png"; 
    public static final String WHITE_QUEEN_ICON_PATH = "../../../../images/white_queen.png"; 
    public static final String WHITE_KING_ICON_PATH = "../../../../images/white_king.png"; 
    public static final String BLACK_PAWN_ICON_PATH = "../../../../images/black_pawn.png"; 
    public static final String BLACK_ROOK_ICON_PATH = "../../../../images/black_rook.png"; 
    public static final String BLACK_KNIGHT_ICON_PATH = "../../../../images/black_knight.png"; 
    public static final String BLACK_BISHOP_ICON_PATH = "../../../../images/black_bishop.png"; 
    public static final String BLACK_QUEEN_ICON_PATH = "../../../../images/black_queen.png"; 
    public static final String BLACK_KING_ICON_PATH = "../../../../images/black_king.png"; 


    /**
     * Superconstructor for a ChessPieces
     * 
     * @param type the type of piece it is (ex. P, B, R, etc.)
     * @param color the color of the piece
     */
    public ChessPiece(char type, char color) {            
        this.type = type;
        this.color = color;  
    }


    //Abstract methods that will be overidden
    public abstract ChessPiece copy();
    public abstract void getValidMoves(ChessPiece[][] buttons, String move);


    /**
     * Sets the symbol of a ChessPiece object
     * 
     * @param s the symbol to be set to
     */
    public void setSymbol (char s) {
        type = s;
    }

    /**
     * Sets the color of a ChessPiece object
     * 
     * @param c the color to be set to
     */
    public void setColor (char c) {
        color = c;
    }
    
    /**
     * Checks a move against a ChessPiece object's arraylist of valid moves
     * 
     * @param move the String form of the move
     * @returns if the move was valid or not
     */
    public boolean isValidMove(String move) {
        if (this.validMoves.contains(move)) {
            return true;
        }
        return false;
    }

    /**
     * Gets the symbol of a ChessPiece
     * 
     * @return the symbol of the ChessPiece
     */
    public char getSymbol() {
        return type;
    }

    /**
     * Returns the ChessPiece's arraylist of valid moves
     * 
     * @returns the ChessPiece's arraylist of valid moves
     */
    public ArrayList<String> getValidArrayList() {
        return validMoves;
    }

    /**
     * Returns the ChessPiece's arraylist of capture moves
     * 
     * @returns the ChessPiece's arraylist of capture moves
     */
    public ArrayList<String> getCaptureArrayList() {
        return captureMoves;
    }
    
    /**
     * Gets the color of a ChessPiece object
     * 
     * @returns the ChessPiece object's color
     */
    public char getColor() {
        return color;
    }
    
  
    
}
    
