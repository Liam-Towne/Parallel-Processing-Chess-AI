import java.awt.Color;
import javax.swing.ImageIcon;

/**
*   The class for the Rook object which extends ChessPiece
*
*   @author Matthew Warner & Liam Towne
*   @version Spring 2023
*
*/
public class Rook extends ChessPiece {

    /**
     * Constructor for a Rook object
     * 
     * @param type the type of piece it is (ex. P, B, R, etc.)
     * @param color the color of the piece
     */
    public Rook(char type, char color) {
        super(type, color);
        ImageIcon icon;
        if (color== 'W') {
            this.setForeground(Color.WHITE);
            icon = new ImageIcon(WHITE_ROOK_ICON_PATH);
        } else {
            this.setForeground(Color.BLACK);
            icon = new ImageIcon(BLACK_ROOK_ICON_PATH);
        }
        setIcon(icon);
    }

    /**
     * Gets and returns a copy of the given Rook
     * 
     * @return a copy of the given Rook
     */
    @Override
    public Rook copy() {
        Rook copiedPiece = new Rook (this.type, this.color);
        return copiedPiece;
    }

    /**
     * Gets all valid moves that a single Rook can make
     * 
     * @param buttons matrix of buttons used to determine where other pieces are
     * @param move the string form of the current move (used to get the source piece and source destination)
     */
    @Override
    public void getValidMoves(ChessPiece[][] buttons, String move) {
        this.validMoves.clear(); // Clear any previous valid moves.
        this.captureMoves.clear();
        
        char startCol = move.charAt(1);
        int startRow = Character.getNumericValue(move.charAt(2));
        
        int colIndex = startCol - 'A';
        int rowIndex = startRow - 1;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // to move in different directions
        int newRow;
        int newCol;
        
        for (int[] direction : directions) {
            newRow = rowIndex + direction[0];
            newCol = colIndex + direction[1];

            // Continue moving in the current direction until an occupied square or the edge of the board is reached
            while (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                ChessPiece target = buttons[newRow][newCol];

                // If the square is empty, add it as a valid move
                if (target.getSymbol() == 'E') {
                    String newMove = "R" + startCol + startRow + (char) ('A' + newCol) + (newRow + 1);
                    this.validMoves.add(newMove);
                } else {
                    // If the square is occupied by an enemy piece, add it as a capture move and break the loop
                    if (target.getColor() != this.color) {
                        String newMove = "R" + startCol + startRow + (char) ('A' + newCol) + (newRow + 1);
                        this.captureMoves.add(newMove);
                        this.validMoves.add(newMove);
                    }
                    break;
                }

                newRow += direction[0];
                newCol += direction[1];                   
            }
        }            
             
    }

}