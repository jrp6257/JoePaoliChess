package puzzles.tilt.ptui;

import puzzles.tilt.model.TiltModel;
import puzzles.tilt.model.Direction;
import puzzles.common.Observer;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.File;

/**
 * The {@code TiltPTUI} class represents the command-line user interface for the tilt puzzle game.
 * It allows the user to interact with the game by entering commands to tilt the board, load a new puzzle,
 * get hints, and quit the game.
 *
 * @see TiltModel
 * @see Direction
 * 
 * @author Maddox Van Sickel
 */
public class TiltPTUI implements Observer<TiltModel, String> {
    /** the model for the tilt game */
    private TiltModel model;

    /**
     * Constructor for the {@code TiltPTUI} class which initializes the model with a tilt file.
     * @param tiltFile the file to load
     */
    public TiltPTUI(File tiltFile) {
        model = new TiltModel(tiltFile);
        model.addObserver(this);
    }

    /**
     * Starts the command-line user interface for the tilt puzzle game.
     * It prompts the user for commands and processes them until the game is quit.
     */
    public void run() {
        try (Scanner in  = new Scanner(System.in)) {
            System.out.println(model.getStringBoard());
            System.out.println(getHelpMessage());
            while (true) {
                String command = in.nextLine().strip().toLowerCase();
                String[] splitCmd = command.split("\\s+");
                if (splitCmd.length > 2) {
                    System.out.println(getHelpMessage());
                    continue;
                }

                switch (splitCmd[0]) {
                    case "h":
                        model.getHint();
                        break;
                    case "l":
                        if (splitCmd.length == 2) {
                            Path tiltFilesPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                            File tiltFile = tiltFilesPath
                                                .resolve("data")
                                                .resolve("tilt")
                                                .resolve(splitCmd[1])
                                                .toFile();
                            model.loadBoard(tiltFile);
                        } else System.out.println(getHelpMessage());
                        break;
                    case "t":
                        if (splitCmd.length == 2) {
                            Direction direction = Direction.fromString(splitCmd[1]);
                            if (direction == Direction.NONE) {
                                System.out.println(getHelpMessage());
                                break;
                            } else model.tilt(direction);
                        } else System.out.println(getHelpMessage());
                        break;
                    case "q":
                        model.quit();
                        break;
                    case "r":
                        model.reset();
                        break;
                    default: System.out.println(getHelpMessage());
                }
            }
        }
    }

    @Override
    public void update(TiltModel model, String message) {
        System.out.println(message);
        System.out.println(model.getStringBoard());
    }

    /**
     * Returns a help message that describes the available commands for the tilt puzzle game.
     * @return a string containing the help message
     */
    private String getHelpMessage() {
        return new StringBuilder()
            .append("h(int)             -- hint next move")
            .append(System.lineSeparator())
            .append("l(oad) filename    -- load new puzzle file")
            .append(System.lineSeparator())
            .append("t(ilt) {N|S|E|W}   -- tilt the board in the given direction")
            .append(System.lineSeparator())
            .append("q(uit)             -- quit the game")
            .append(System.lineSeparator())
            .append("r(eset)            -- reset the current game")
            .append(System.lineSeparator())
            .toString();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltPTUI filename");
        } else {
            File tiltFile = new File(args[0]);
            TiltPTUI ptui = new TiltPTUI(tiltFile);
            ptui.run();
        }
    }
}
