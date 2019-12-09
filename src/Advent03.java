import static java.lang.System.console;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.Collectors;

class Direction {
    int x;
    int y;
    int distance;

    public Direction(int x, int y, int distance) {
        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    public static Direction from(String dir) {
        char dirChar = dir.charAt(0);

        int x = 0;
        int y = 0;
        switch (dirChar) {
        case 'U':
            y = 1;
            break;
        case 'R':
            x = 1;
            break;
        case 'D':
            y = -1;
            break;
        case 'L':
            x = -1;
            break;
        }

        int distance = Integer.parseInt(dir.substring(1));

        return new Direction(x, y, distance);
    }

    @Override
    public String toString() {
        return "Direction [distance=" + distance + ", x=" + x + ", y=" + y + "]";
    }
}

class Line {
    int x0;
    int y0;
    int x1;
    int y1;
    int totalDistance;
    Direction dir;

    public Line(int x0, int y0, int x1, int y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    static Line fromPointAndDir(int x0, int y0, Direction dir) {
        int x1 = x0 + dir.x * dir.distance;
        int y1 = y0 + dir.y * dir.distance;

        Line res = new Line(x0, y0, x1, y1);
        res.dir = dir;

        return res;
    }

    static Intersection intersects(Line l0, Line l1) {
        if (l0.getDirection() == 1) {
            Line tmp;
            tmp = l0;
            l0 = l1;
            l1 = tmp;
        }

        if (l0.getDirection() == l1.getDirection()) {
            return Intersection.Nil;
        }

        int[] xCoords = new int[] { l0.x0, l0.x1, l1.x0, l1.x1 };
        int[] yCoords = new int[] { l0.y0, l0.y1, l1.y0, l1.y1 };
        Arrays.sort(xCoords);
        Arrays.sort(yCoords);

        if (l1.x0 <= xCoords[0] || l1.x0 >= xCoords[3]) {
            return Intersection.Nil;
        }
        if (l0.y0 <= yCoords[0] || l0.y0 >= yCoords[3]) {
            return Intersection.Nil;
        }

        int iX = xCoords[1];
        int iY = yCoords[1];
        Intersection p = new Intersection(iX, iY);
        p.dist1 = l0.totalDistance + Intersection.dist(l0.x0, l0.y0, iX, iY);
        p.dist2 = l1.totalDistance + Intersection.dist(l1.x0, l1.y0, iX, iY);

        return p;
    }

    public int getDirection() {
        if (y0 == y1) {
            return 0;
        }
        if (x0 == x1) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "<" + x0 + ", " + y0 + ", " + x1 + ", " + y1 + "[" + totalDistance + "," + dir.distance + "]>";
    }
}

class Intersection {
    int x;
    int y;

    int dist1;
    int dist2;

    static Intersection Nil = new Intersection(Integer.MIN_VALUE, Integer.MIN_VALUE);

    Intersection(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "P<" + x + ", " + y + ", D[" + dist1 + "," + dist2  + "," + (dist1 + dist2) + "]>";
    }

    static int dist(int x0, int y0, int x1, int y1) {
        return (
            Math.abs(x0 - x1) +
            Math.abs(y0 - y1)
        );
    }
}

class Advent03 {
    static ArrayList<Direction> argToDirections(String arg) {
        return Arrays.stream(arg.split(",")).map(String::trim).map(Direction::from)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    static ArrayList<Line> directionsToLines(ArrayList<Direction> dirs) {
        int x0 = 0;
        int y0 = 0;

        ArrayList<Line> lines = new ArrayList<>();
        int totalDist = 0;
        for (Direction d : dirs) {
            Line line = Line.fromPointAndDir(x0, y0, d);
            line.totalDistance = totalDist;

            totalDist += d.distance;
            lines.add(line);

            x0 = line.x1;
            y0 = line.y1;
        }

        return lines;
    }

    public static void testLineCtor() {
        Line l0 = Line.fromPointAndDir(0, 0, Direction.from("R8"));
        System.out.println(l0);
    }

    public static void testLineCtor2() {
        Line l0 = Line.fromPointAndDir(0, 0, Direction.from("R8"));
        System.out.println(l0);
        Line l1 = Line.fromPointAndDir(l0.x1, l0.y1, Direction.from("U5"));
        System.out.println(l1);
    }

    public static void testIntersects() {
        Line l0 = new Line(5, 1, 5, 4);
        Line l1 = new Line(-5, 3, 7, 3);

        Console console = System.console();
        console.format("%s %d\n", l0, l0.getDirection());
        console.format("%s %d\n", l1, l1.getDirection());

        Intersection p = Line.intersects(l0, l1);
        console.format("the intercept %s", p);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.print("You must provide a file with a wire in each line\n");
            System.exit(2);
        }

        String path = args[0];
        ArrayList<Line> linesW1;
        ArrayList<Line> linesW2;
        try (BufferedReader br =
                     new BufferedReader(new FileReader(path))) {
            linesW1 = directionsToLines(argToDirections(br.readLine()));

            linesW2 = directionsToLines(argToDirections(br.readLine()));
        }



        ArrayList<Intersection> crosses = new ArrayList<>();
        for (Line l0 : linesW1) {
            for (Line l1 : linesW2) {
                Intersection in = Line.intersects(l0, l1);
                if (in != Intersection.Nil) {
                    crosses.add(in);
                }
            }
        }

        console().printf("%s\n", crosses);
        OptionalInt min = crosses.stream().mapToInt(p -> Math.abs(p.x) + Math.abs(p.y)).min();
        if (min.isEmpty()) {
            return;
        }

        console().printf("\nans1 %s\n", min.getAsInt());
        OptionalInt min2 = crosses.stream().mapToInt(p -> p.dist1 + p.dist2).min();

        min2.ifPresentOrElse(num -> console().printf("ans2 %s\n", num), () -> console().printf("Can't find suitable answer"));
    }
}