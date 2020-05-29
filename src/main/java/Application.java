import engine.Engine;
import engine.GameLogic;
import sample.Dummy;

public class Application {
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            GameLogic logic = new Dummy();
            Engine engine = new Engine("GAME", 800,500, vSync, logic);
            engine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
