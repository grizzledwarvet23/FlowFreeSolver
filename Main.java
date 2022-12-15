import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

public class Main {

    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";

    public static final String WHITE = "\u001B[37m";

    public static HashMap<Character, String> map;
    public static HashMap<Character, Color> colorMap;

    public static void main(String[] args) {
        map = new HashMap<>();
        map.put('r', RED);
        map.put('b', BLUE);
        map.put('y', YELLOW);
        map.put('g', GREEN);
        map.put('w', WHITE);

        colorMap = new HashMap<>();
        colorMap.put('r', Color.RED);
        colorMap.put('b', Color.BLUE);
        colorMap.put('y', Color.YELLOW);
        colorMap.put('g', Color.GREEN);
        colorMap.put('w', Color.WHITE);
        colorMap.put('o', Color.ORANGE);
        colorMap.put('c', Color.CYAN);
        colorMap.put('p', Color.PINK);
        Scanner in = new Scanner(System.in);
        DrawingPanel panel = new DrawingPanel(800, 800);
        panel.setBackground(Color.BLACK);
        while (true) {
            //drawing panel imported to visualize the game board and algorithm
            panel.getGraphics().clearRect(0, 0, 800, 800);
            System.out.print("What is the size N of the grid?: ");
            int N = in.nextInt();
            in.nextLine();
            System.out.println();
            System.out.println("Input the grid: ");
            char[][] grid = new char[N][N];
            ArrayList<ColorPair> colors = new ArrayList<>();

            //construct shite
            for (int i = 0; i < N; i++) {
                String line = in.nextLine();
                in.nextLine();
                // System.out.println(line);
                for (int j = 0; j < N; j++) {
                    grid[i][j] = line.charAt(j);
                    if (grid[i][j] != '*') { //must be a color point
                        boolean found = false;
                        for (ColorPair color : colors) {
                            if (color.letter == grid[i][j]) {
                                color.endPoint = new Point(j, i);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            colors.add(new ColorPair(grid[i][j], j, i));
                        }
                        Graphics g = panel.getGraphics();

                        g.setColor(colorMap.get(Character.toLowerCase(grid[i][j])));
                        g.fillRect(20 * j, 20 * i, 20, 20);
                    }
                }
            }


            Collections.sort(colors);

            //iterate through colors, mark how many adjacent.
            //it is absolutely sure that startAdjacent + endAdjacent >= 2

            for (ColorPair color : colors) {
                countAdjacentSquares(color, grid);
            }

            Stopwatch watch = new Stopwatch();
            printGrid(grid);
            watch.start();
            solver(grid, colors, panel, 0, colors.get(0).startPoint.x, colors.get(0).startPoint.y);
            watch.stop();
            System.out.println();
            printGrid(grid);
            System.out.println(watch.time() + " seconds to solve");

            System.out.print("DO it again? Y/N");
            String line = in.nextLine();
            if (!line.toLowerCase().contains("y")) {
                break;
            }
        }

        //the grid is composed of letters for the color of line, like this
        /**
         *  ***B
         *  R***
         *  **R*
         *  ***B
         */
        //the output prints the color paths, like this
        /**
         *  ***B
         *  Rrrb
         *  **Rb
         *  ***B
         */
        //lowercase letters are path and uppercase are the endpoints
        //we can try a naive DFS approach: start with a color, and reach its endpoint
        //keep track of start points and endpoints so we can just iterate through them

        //recursive function: when a potential path found, go to the next color pair and find its
        // path.
    }

    static boolean solver(char[][] grid, List<ColorPair> colors, DrawingPanel panel, int index,
                          int currentX,
                          int currentY) {

        ColorPair pair = colors.get(index);

        if (currentX == pair.endPoint.x && currentY == pair.endPoint.y) { //if we are on the end
            if (index + 1 >= colors.size()) {
                return true;
            } else {
                ColorPair nextPair = colors.get(index + 1);
                boolean res = solver(grid, colors, panel, index + 1, nextPair.startPoint.x,
                        nextPair.startPoint.y);
                if (res) {
                    return true;
                }
            }
        } else {
            grid[currentY][currentX] = pair.pathLetter;
        }

        ColorPair[] directions = new ColorPair[4];
        directions[0] = new ColorPair('-', currentX - 1, currentY);
        directions[0].endPoint = pair.endPoint;
        directions[1] = new ColorPair('-', currentX, currentY - 1);
        directions[1].endPoint = pair.endPoint;
        directions[2] = new ColorPair('-', currentX + 1, currentY);
        directions[2].endPoint = pair.endPoint;
        directions[3] = new ColorPair('-', currentX, currentY + 1);
        directions[3].endPoint = pair.endPoint;

        Arrays.sort(directions);
        //rank directions based on which is closer to point


        for (int i = 0; i < directions.length; i++) {
            int newX = directions[i].startPoint.x;
            int newY = directions[i].startPoint.y;



            if (inBounds(grid, newX, newY) && (validSpot(grid, newX, newY) || grid[newY][newX] == pair.letter)  ) {
                grid[newY][newX] = pair.pathLetter;
                panel.getGraphics().setColor(colorMap.get(pair.pathLetter));
                panel.getGraphics().fillRect(newX * 20, newY * 20, 20, 20);
                ArrayList<Integer> oldAdjacentStarts = new ArrayList<>();
                ArrayList<Integer> oldAdjacentEnds = new ArrayList<>();

                boolean res = true;
                for(int c = index + 1; c < colors.size(); c++) {
                    ColorPair colorToCheck = colors.get(c);
                    oldAdjacentStarts.add(colorToCheck.startAdjacent);
                    oldAdjacentEnds.add(colorToCheck.endAdjacent);
                    checkConnected(grid, colorToCheck, newX, newY);
                    if(colorToCheck.endAdjacent == 0) {
                      //  res = false;
                    }
                }
                res = res && solver(grid, colors, panel, index, newX, newY);
                if (res) {
                    return true;
                }
                panel.getGraphics().setColor(Color.BLACK);
                panel.getGraphics().fillRect(newX * 20, newY * 20, 20, 20);
                grid[newY][newX] = '*';
                int ind = 0;
                for(int c = index + 1; c < colors.size(); c++) {
                    colors.get(c).startAdjacent = oldAdjacentStarts.get(ind);
                    colors.get(c).endAdjacent = oldAdjacentEnds.get(ind);
                    ind++;
                }
            }
        }
        return false;
    }

    private static void countAdjacentSquares(ColorPair color, char[][] grid) {
        int[] xDir = new int[]{-1, 0, 1, 0};
        int[] yDir = new int[]{0, -1, 0, 1};
        for (int i = 0; i < xDir.length; i++) {
            if (inBounds(grid, color.startPoint.x + xDir[i],  color.startPoint.y + yDir[i]) &&
                    validSpot(grid, color.startPoint.x + xDir[i], color.startPoint.y + yDir[i])) {
                color.startAdjacent++;
            }
            if (inBounds(grid, color.endPoint.x + xDir[i], color.endPoint.y + yDir[i]) &&
                    validSpot(grid, color.endPoint.x + xDir[i], color.endPoint.y + yDir[i])) {
                color.endAdjacent++;
            }
        }
    }

    private static void checkConnected(char[][] grid, ColorPair pair, int x, int y) {
        int[] xDir = new int[]{-1, 0, 1, 0};
        int[] yDir = new int[]{0, -1, 0, 1};
        for (int i = 0; i < xDir.length; i++) {
            int newX = x + xDir[i];
            int newY = y + yDir[i];
            if(inBounds(grid, newX, newY)) {
                //check if this matches a start/endpoint starting after index, in colors
//                for(int j = index + 1; j < colors.size(); j++) {
//                    ColorPair pair = colors.get(j);
                    if(pair.startPoint.x == newX && pair.startPoint.y == newY) {
                        pair.startAdjacent--;
                    } else if(pair.endPoint.x == newX && pair.endPoint.y == newY) {
                        pair.endAdjacent--;
                    }
//                }
            }
        }
      //  return null;
    }

    private static boolean inBounds(char[][] grid, int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid.length;
    }

    private static boolean validSpot(char[][] grid, int x, int y) {
        return grid[y][x] == '*';
    }

    private static void printGrid(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (char c : grid[i]) {
                if (map.get(c) != null) {
                    System.out.print(map.get(c) + c + map.get(c));
                } else {
                    System.out.print(map.get('w') + c + map.get('w'));
                }
            }
            System.out.println();
        }
    }

}

class ColorPair implements Comparable<ColorPair> {
    char letter;
    char pathLetter;
    Point startPoint;
    Point endPoint;

    //how many open squares are adjacent with the start and end points
    //useful in determining when one's path makes a future one impossible
    //neither should be 0
    int startAdjacent;
    int endAdjacent;

    public ColorPair(char letter, int startX, int startY) {
        this.letter = letter;
        pathLetter = Character.toLowerCase(letter);
        startPoint = new Point(startX, startY);

    }

    private double getDist() {
        return Math.sqrt(Math.pow(startPoint.x - endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y,
                2));
    }

    public int compareTo(ColorPair other) {
        if (getDist() - other.getDist() < 0) {
            return -1;
        } else if (getDist() - other.getDist() > 0) {
            return 1;
        }
        return 0;

    }


}

class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
