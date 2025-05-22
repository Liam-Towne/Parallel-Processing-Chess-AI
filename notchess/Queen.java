import java.awt.Color;
import javax.swing.ImageIcon;

/**
*   The class for the Queen object which extends ChessPiece
*
*   @author Matthew Warner & Liam Towne
*   @version Spring 2023
*
*/
public class Queen extends ChessPiece {

    /**
     * Constructor for a Queen object
     * 
     * @param type the type of piece it is (ex. P, B, R, etc.)
     * @param color the color of the piece
     */
    public Queen(char type, char color) {
        super(type, color);
        ImageIcon icon;
        if (color== 'W') {
            this.setForeground(Color.WHITE);
            icon = new ImageIcon(WHITE_QUEEN_ICON_PATH);
        } else {
            this.setForeground(Color.BLACK);
            icon = new ImageIcon(BLACK_QUEEN_ICON_PATH);
        }
        setIcon(icon);
    }


    /**
     * Gets and returns a copy of the given Queen
     * 
     * @return a copy of the given Queen
     */
    @Override
    public Queen copy() {
        Queen copiedPiece = new Queen (this.type, this.color);
        return copiedPiece;
    }


    /**
     * Gets all valid moves that a single Queen can make
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

        // Possible movements (diagonal, horizontal, and vertical)
        int[][] directions = {
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}, // Diagonal
            {0, -1}, {0, 1}, {-1, 0}, {1, 0} // Horizontal and Vertical
        };

        for (int[] direction : directions) {
            int newRow = rowIndex;
            int newCol = colIndex;

            while (true) {
                newRow += direction[0];
                newCol += direction[1];

                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    ChessPiece target = buttons[newRow][newCol];

                    // If the square is empty, add it as a valid move
                    if (target.getSymbol() == 'E') {
                        String newMove = "Q" + startCol + startRow + (char) ('A' + newCol) + (newRow + 1);
                        this.validMoves.add(newMove);
                    } else {
                        // If the square is occupied by an enemy piece, add it as a valid move and stop
                        if (target.getColor() != this.color) {
                            String newMove = "Q" + startCol + startRow + (char) ('A' + newCol) + (newRow + 1);
                            this.validMoves.add(newMove);
                            this.captureMoves.add(newMove);
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
        }
    }


}