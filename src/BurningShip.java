import java.awt.geom.Rectangle2D;

public class BurningShip extends FractalGenerator {

    public static final int MAX_ITERATIONS = 2000;
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2.5;
        range.width = 4;
        range.height = 4;
    }

    @Override
    public int numIterations(double x, double y) {
        int counter = 0;
        double simpleValue = 0;
        double complexValue = 0;
        while (counter < MAX_ITERATIONS && simpleValue * simpleValue + complexValue * complexValue < 4) {
            double tempSimple = (simpleValue*simpleValue - complexValue*complexValue) + x;
            double tempComplex = Math.abs(2 * simpleValue * complexValue) + y;
            simpleValue = tempSimple;
            complexValue = tempComplex;
            counter++;
        }
        if (counter == MAX_ITERATIONS ) {
            return -1;
        }
        return counter;
    }

    @Override
    public String toString() {
        return "Burning Ship";
    }
}
