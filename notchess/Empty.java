
/**
*   The class for the Empty button object which extends ChessPiece
*
*   @author Matthew Warner & Liam Towne
*   @version Spring 2023
*
*/
public class Empty extends ChessPiece {

    /**
     * Constructor for a Empty object (Basically just buttons with no real functionality)
     * 
     * @param type the type of piece it is (ex. P, B, R, etc.)
     * @param color the color of the piece
     */
    public Empty(char type, char color) {
        super(type, color);
        
    }


    /**
     * This is just to satisfy the abstract class. It has no functionality
     * 
     * @param buttons matrix of buttons used to determine where other pieces are
     * @param move the string form of the current move (used to get the source piece and source destination)
     */
    @Override
    public void getValidMoves(ChessPiece[][] buttons, String move) {
        
    }

    /**
     * Gets and returns a copy of the given Empty button
     * 
     * @return a copy of the given Empty button
     */
    @Override
    public Empty copy() {
        Empty copiedPiece = new Empty (this.type, this.color);
        return copiedPiece;
    }
}