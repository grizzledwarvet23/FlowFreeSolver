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

    static int c = 0;

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

        while(true) {
            //drawing panel imported to visualize the game board and algorithm
            DrawingPanel panel = new DrawingPanel(800, 800);
            panel.setBackground(Color.BLACK);
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
                        g.fillRect(20*j, 20*i, 20, 20);
                    }
                }
            }
            Collections.sort(colors);

            Stopwatch watch = new Stopwatch();
            printGrid(grid);
            watch.start();
            solver(grid, colors, panel, 0, colors.get(0).startPoint.x, colors.get(0).startPoint.y);
            watch.stop();
            System.out.println();
            printGrid(grid);
            System.out.println(watch.time() + " seconds to solve");

            System.out.println("DO it again? Y/N");
            System.out.println();
            String line = in.nextLine();
            if(!line.toLowerCase().equals('y')) {
                break;
            }
        }

        //the grid is composed of letters for the color of line, like this
        /**
         *  ***B
         *  R***
         *  **R*
         *  ***B
         *
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
        //

    }

    static boolean solver(char[][] grid, List<ColorPair> colors, DrawingPanel panel, int index,
                          int currentX,
                       int currentY) {
        c++;
        //System.out.println("called " + c + " times. index: " + index);

        ColorPair pair = colors.get(index);

        if(currentX == pair.endPoint.x && currentY == pair.endPoint.y) { //if we are on the end
            if(index + 1 >= colors.size()) {
                return true;
            } else {
                ColorPair nextPair = colors.get(index + 1);
                boolean res = solver(grid, colors, panel, index + 1, nextPair.startPoint.x,
                        nextPair.startPoint.y);
                if(res) {
                    return true;
                }
            }
        } else {
            grid[currentY][currentX] = pair.pathLetter;
        }

        int[] dirX = new int[]{-1, 0, 1, 0};
        int[] dirY = new int[]{0, -1, 0, 1};
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


        for(int i = 0; i < dirX.length; i++) {
            int newX = directions[i].startPoint.x;
            int newY = directions[i].startPoint.y;
            if(inBounds(grid, newX, newY) && (validSpot(grid, newX, newY) || grid[newY][newX] == pair.letter)) {
                grid[newY][newX] = pair.pathLetter;
                panel.getGraphics().setColor(colorMap.get(pair.pathLetter));
                panel.getGraphics().fillRect(newX * 20, newY * 20, 20, 20);
                boolean res = solver(grid, colors, panel, index, newX, newY);
                if(res) { return true; }
                panel.getGraphics().setColor(Color.BLACK);
                panel.getGraphics().fillRect(newX * 20, newY * 20, 20, 20);
                grid[newY][newX] = '*';
            }
        }
        return false;
    }

    private static boolean inBounds(char[][] grid, int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid.length;
    }

    private static boolean validSpot(char[][] grid, int x, int y) {
        return grid[y][x] == '*';
    }

    private static void printGrid(char[][] grid) {
        for(int i = 0; i < grid.length; i++) {
            for(char c : grid[i]) {
                if(map.get(c) != null) {
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

    public ColorPair(char letter, int startX, int startY) {
        this.letter = letter;
        pathLetter = Character.toLowerCase(letter);
        startPoint = new Point(startX, startY);

    }

    private double getDist() {
        return Math.sqrt(Math.pow(startPoint.x- endPoint.x, 2) + Math.pow(startPoint.y - endPoint.y,
                2));
    }

    public int compareTo(ColorPair other) {
        if(getDist() - other.getDist() < 0) {
            return -1;
        } else if(getDist() - other.getDist() > 0) {
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
