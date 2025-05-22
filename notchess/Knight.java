import java.awt.Color;
import javax.swing.ImageIcon;

/**
*   The class for the Knight object which extends ChessPiece
*
*   @author Matthew Warner & Liam Towne
*   @version Spring 2023
*
*/
public class Knight extends ChessPiece {

    /**
     * Constructor for a Knight object
     * 
     * @param type the type of piece it is (ex. P, B, R, etc.)
     * @param color the color of the piece
     */
    public Knight(char type, char color) {
        super(type, color);
        ImageIcon icon;
        if (color== 'W') {
            this.setForeground(Color.WHITE);
            icon = new ImageIcon(WHITE_KNIGHT_ICON_PATH);
        } else {
            this.setForeground(Color.BLACK);
            icon = new ImageIcon(BLACK_KNIGHT_ICON_PATH);
        }
        setIcon(icon);
    }

    /**
     * Gets and returns a copy of the given Knight
     * 
     * @return a copy of the given Knight
     */
    @Override
    public Knight copy() {
        Knight copiedPiece = new Knight (this.type, this.color);
        return copiedPiece;
    }

    /**
     * Gets all valid moves that a single Knight can make
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

        // Possible knight movements
        int[][] knightMoves = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] knightMove : knightMoves) {
            int newRow = rowIndex + knightMove[0];
            int newCol = colIndex + knightMove[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                ChessPiece target = buttons[newRow][newCol];

                // If the square is empty or occupied by an enemy piece, add it as a valid move
                if (target.getSymbol() == 'E') {
                    String newMove = "N" + startCol + startRow + (char) ('A' + newCol) + (newRow + 1);
                    this.validMoves.add(newMove);
                } else if (target.getColor() != this.color) {
                    String newMove = "N" + startCol + startRow + (char) ('A' + newCol) + (newRow + 1);
                    this.validMoves.add(newMove);
                    this.captureMoves.add(newMove);
                }
            }
        }

    }

}