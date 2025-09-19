package puzzles.chess.ptui;

import puzzles.chess.model.ChessConfig;
import puzzles.chess.model.ChessModel;
import puzzles.common.Observer;

import java.io.File;
import java.util.Scanner;

/**
 * @author Joe Paoli
 * PTUI for chess board
 */
public class ChessPTUI implements Observer<ChessModel, String> {
    private ChessModel model;


    /**
     * Constructor for PTUI
     * @param chessFile- file to load board
     */
    public ChessPTUI(File chessFile) {
        model = new ChessModel(chessFile);
        model.addObserver(this);
    }

    /**
     * Generates possible commands for PTUI
     * @return
     */
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("h(int)                     --hint next move\n");
        sb.append("l(oad) filename            --load new puzzle file \n");
        sb.append("s(elect) r c               --selects cell at r, c\n");
        sb.append("q(uit)                     --quit the game\n");
        sb.append("r(eset)                    --resets the current game\n");
        return sb.toString();
    }

    /**
     * Runs model methods based on input
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(model.ptuiString());
        System.out.println(getMessage());
        while (true) {
            String input = scanner.nextLine().trim();
            String[] tokens = input.split(" ");
            if (tokens.length == 0) continue;
            String command = tokens[0].toLowerCase();
            switch (command) {
                case "h", "hint":
                    model.getHint();
                    break;
                case "l", "load":
                    if (tokens.length < 2) {
                        System.out.println("Usage: load <filepath>");
                    } else {
                        File chessFile = new File(tokens[1]);
                        model.loadFile(chessFile);
                    }
                    break;
                case "s", "select":
                    if (tokens.length != 3) {
                        System.out.println("Usage: select <row> <col>");
                    } else {
                        model.movePiece(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
                    }
                    break;
                case "q", "quit":
                    model.quit();
                    return;
                case "r", "reset":
                    model.reset();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        }
    }

    /**
     * Updates the PTUI, printing message and new board
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(ChessModel model, String message) {
        System.out.println(message);
        System.out.println(model.ptuiString());
    }

    /**
     * Starts the PTUI
     * @param args- command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessPTUI filename");
        } else {
            File chessFile = new File(args[0]);
            ChessPTUI ptui = new ChessPTUI(chessFile);
            ptui.run();
        }
    }
}
