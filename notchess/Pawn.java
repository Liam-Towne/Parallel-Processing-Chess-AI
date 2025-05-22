import java.awt.Color;
import javax.swing.ImageIcon;

/**
*   The class for the Pawn object which extends ChessPiece
*
*   @author Matthew Warner & Liam Towne
*   @version Spring 2023
*
*/
public class Pawn extends ChessPiece {
        
    /**
     * Constructor for a Pawn object
     * 
     * @param type the type of piece it is (ex. P, B, R, etc.)
     * @param color the color of the piece
     */
    public Pawn(char type, char color) {
        super(type, color);
        ImageIcon icon;
        if (color== 'W') {
            this.setForeground(Color.WHITE);
            icon = new ImageIcon(WHITE_PAWN_ICON_PATH);
        } else {
            this.setForeground(Color.BLACK);
            icon = new ImageIcon(BLACK_PAWN_ICON_PATH);
        }
        setIcon(icon);
    }

    /**
     * Gets and returns a copy of the given Pawn
     * 
     * @return a copy of the given Pawn
     */
    @Override
    public Pawn copy() {
        Pawn copiedPiece = new Pawn (this.type, this.color);
        return copiedPiece;
    }

    /**
     * Gets all valid moves that a single Pawn can make
     * 
     * @param buttons matrix of buttons used to determine where other pieces are
     * @param move the string form of the current move (used to get the source piece and source destination)
     */
    @Override
    public void getValidMoves(ChessPiece[][] buttons, String move) {
        
        this.validMoves.clear(); // Clear any previous valid moves.
        this.captureMoves.clear();

        String newMove;

        char startCol = move.charAt(1);
        int startRow = Character.getNumericValue(move.charAt(2));

        int colIndex = startCol - 'A';
        int rowIndex = startRow - 1;
        
        // Move forward one square (make sure we are looking in the correct direction)
        if (color == 'W') {
            rowIndex = rowIndex + 1;
        }
        else if (color == 'B') {
            rowIndex = rowIndex - 1;
        }

        //if the button in front is an empty button
        if (rowIndex >= 0 && rowIndex < 8 && buttons[rowIndex][colIndex].getSymbol() == 'E') {
            rowIndex++;
            
            newMove = "P" + startCol + startRow + startCol + rowIndex;
            this.validMoves.add(newMove);
            rowIndex--;
            
            // Move forward two squares on the first move.
            if ((startRow == 2 && color == 'W') || (startRow == 7 && color == 'B')) {
                
                if (color == 'W') {
                    rowIndex = rowIndex + 1;
                }
                else if (color == 'B') {
                    rowIndex = rowIndex - 1;
                }

                if (rowIndex >= 0 && rowIndex < 8 && buttons[rowIndex][colIndex].getSymbol() == 'E') {
                    rowIndex++;
                    newMove = "P" + startCol + startRow + startCol + rowIndex;
                    this.validMoves.add(newMove);
                    rowIndex--;
                }
            }
        }
        
        rowIndex = startRow - 1;
        
        // Capturing diagonally (make sure we are looking in the correct direction)
        if (color == 'W') {
            rowIndex = rowIndex + 1;
        }
        else if (color == 'B') {
            rowIndex = rowIndex - 1;
        }

        //Capture diagonally forward. The for loop is used to switch directions.
        for (int i = -1; i <= 1; i += 2) {
            int captureColIndex = colIndex + i;

            //if the diagonal location is within the correct bounds 
            if (captureColIndex >= 0 && captureColIndex < 8 && rowIndex >= 0 && rowIndex < 8) {
                ChessPiece target = buttons[rowIndex][captureColIndex]; //get that as a piece
                //System.out.println(target.getSymbol());
                if (target.getColor() != color && target.getSymbol() != 'E') {
                    rowIndex++;
                    newMove = "P" + startCol + startRow + (char) (startCol + i) + rowIndex;
                    this.captureMoves.add(newMove);
                    this.validMoves.add(newMove);
                    rowIndex--;
                }
            }

        }

    }



    /**
     * checks to see if a Pawn has reached the other side of the board
     * so that it can be turned into a queen
     * 
     * @param row the row of the pawn
     * @param col the column of the pawn
     * @return whether or not a Pawn has reached the other side
     */
    public boolean isCrowned (int row, int col) {
        if (color == 'W' && row == 7 || color == 'B' && row == 0) {
            return true;
        }
        return false;
    }
}