import java.util.ArrayList;
import java.util.Stack;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * n-ary tree implementation for notchess object (and "value" of each node)
 */

public class Tree {
    Node root;
    private AtomicInteger numNodes = new AtomicInteger(1);
    private AtomicInteger simulations = new AtomicInteger(0);
    private static int MAX_NODES; //not sure if this is a good amount, change if necessary
    int numThreads;
    private BagOfTasks  bag;

    public Tree(notchess board) {
        root = new Node(board);
    }
    
    public Tree(notchess board, int numThreads, int maxNodes){
        root = new Node(board);
        this.numThreads = numThreads;
        MAX_NODES = maxNodes;
        
        bag = new BagOfTasks();
        bag.add(root);
    }    

    public void addNumNodes(){
        numNodes.incrementAndGet();
    }
    
    public void addSimulation() {
        simulations.incrementAndGet();
    }
    
    public int getNumNodes() {
        return numNodes.get();
    }

    public int getSimulations() {
        return simulations.get();
    }

    /*
     * select best possible node to simulate on
     */
    public synchronized Node selectNode(Node node){
        if (node.children.size() == 0){
            return node;
        }

        Node temp = node.children.get(0);

        for (Node n : node.children){
            if (UCB(n.getNodeValue(), n.visits, n.parent.visits) >= UCB(temp.getNodeValue(), temp.visits, temp.parent.visits)){
                temp = n;
            }

        }
        temp.addVisit();
        if (Math.random() > 0.3){
            return selectNode(temp);
        }
        return temp;

    }

    /*
     * updates parent nodes w/ wins and simulations
     */
    public void backpropagate(Node node, boolean success){
        while (node != null && node != root) {
            node.addSim();
            if (success) {
                node.addWin();
            }
            node = node.parent; // Move to the parent node
        }
    }

    /*
     * performs randomized simulation of chess game to completion
     */
    public void simulate(Node node){
        boolean success = false;
        addSimulation();
        
        //create new board, copy over necessary values
        notchess board = copyBoard(node.board);
        
        char turn = board.turn;
        String move;
        //long startTime = System.currentTimeMillis();

        while (board.turn != 'O'){
            turn = board.turn;
            
            if (!(board.canAnyMovePreventCheck(turn))) {
                if (turn == 'B') {
                    success = false;
                    backpropagate(node, success);
                    return;
                } else if (turn == 'W') {
                    success = true;
                    backpropagate(node, success);
                    return;
                }
                
            }
            
            move = randomMove(board);
            if (board.isValidMove(move)) {
                board.processMoveAI(move);
            }
            if (board.moves.size() > 200) {
                success = false;
                backpropagate(node, success);
                return;
            }
        }

        if (turn == 'B'){
            success = true;
        }

        //when loop is finished, update all nodes above
        backpropagate(node, success);
    }

    public void addChild(Node parent, Node child){
        parent.children.add(child);
    }

    /*
     * adds new node to tree
     */
    public void add(){
        Node parent = selectNode(root);
        Node temp = new Node(copyBoard(parent.board));
        addChild(parent, temp);
        temp.parent = parent;

        temp.board.processMoveAI(randomMove(temp.board));


        addNumNodes();

        simulate(temp);
    }

    /*
     * makes a copy of notchess object
     */
    public notchess copyBoard(notchess board){
        notchess copy = new notchess();
        copy.buttons = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                copy.buttons[i][j] = board.buttons[i][j].copy();
            }
        }
        
        copy.moves = new Stack<String>();
        copy.moves.addAll(board.moves);
        
        copy.turn = board.turn;
        copy.bPiecesLeft = board.bPiecesLeft;
        copy.wPiecesLeft = board.wPiecesLeft;
        copy.isInCheck = board.isInCheck;
        copy.gamemode = board.gamemode;

        return copy;
    }

    /*
     * upper confidence bounds formula to find best node to check
     */
    public double UCB(double value, int visits, int parentVisits){
        return value + Math.sqrt(Math.log(getSimulations()) / visits);
    }

    public String randomMove(notchess board){
        //create notchess ai with board copy
        notChessAI temp = new notChessAI(board);
        ArrayList<String> moves;
        moves = temp.generateMoves();
        String move = temp.pickMoveRandom(moves);

        return move;
    }

    /*
     * runs through the entire algorithm (i think) and returns the best move it's found
     */
    public notchess findBestMove(){
        while (getNumNodes() < MAX_NODES){
            add();
        }
        Node best = root.children.get(0);

        for (Node n : root.children){
            if (n.getNodeValue() > best.getNodeValue()){
                best = n;
            }
        }

        return best.board;
    }

    public notchess findBestMoveParallel(){
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                doThreadWork(bag);
            });
        }
        
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }

        Node best = root.children.get(0);

        for (Node n : root.children){
            if (n.getNodeValue() > best.getNodeValue()){
                best = n;
            }
        }

        return best.board;
    }

    public void doThreadWork(BagOfTasks bag){        
        while (getNumNodes() < MAX_NODES){
            Node parent = bag.get();
            if (parent == null) {
                //System.out.println("Null task return");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue; 
            }
            
            for (int i = 0; i < 3; i++) {
                Node temp = new Node(copyBoard(parent.board));
                synchronized (this) {
                    addChild(parent, temp);
                    addNumNodes();
                }
                bag.add(temp);
            }
            
            parent.board.processMoveAI(randomMove(parent.board));
            simulate(parent);
        }
    }
}

class Node {
    notchess board;
    int wins = 0;
    int simulations = 0;
    int visits = 0;
    Node parent;
    List<Node> children;

    public Node(notchess board) {
        this.board = board;
        children = new ArrayList<Node>();
    }

    /*
     * get "value" of node to determine if it's a viable option
     */
    synchronized public double getNodeValue(){
        if (simulations == 0){
            return 0;
        }
        return (double)wins / (double)simulations;
    }

    synchronized public void addWin(){
        wins++;
    }

    synchronized public void addSim(){
        simulations++;
    }

    synchronized public void addVisit(){
        visits++;
    }
}

class WorkerThread extends Thread{
    protected int threadID;
    protected BagOfTasks bag;

    public WorkerThread(int threadID, BagOfTasks bag){
        this.threadID = threadID;
        this.bag = bag;
    }
}

class BagOfTasks {
    private ConcurrentLinkedQueue<Node> tasks;

    public BagOfTasks(){
        this.tasks = new ConcurrentLinkedQueue<>();
    }
    
    public void add(Node node) {
        tasks.add(node);
    }

    public Node get(){
        return tasks.poll();
    }
}