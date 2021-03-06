package ego.engine;

public class Engine implements Runnable {

    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;

    private final Window window;
    private final Timer timer;
    private final GameLogic logic;
    private final MouseInput mouseInput;

    public Engine(String title, int width, int height, boolean vSync, GameLogic logic) throws Exception{
        window = new Window(title, width, height, vSync);
        mouseInput = new MouseInput();
        this.logic = logic;
        timer = new Timer();
    }


    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    public void init() throws Exception{
        window.init();
        timer.init();
        mouseInput.init(window);
        logic.init(window);
    }

    public void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()) {
                sync();
            }
        }
    }

    protected void cleanup(){
        logic.cleanup();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
        mouseInput.input(window);
        logic.input(window, mouseInput);
    }

    protected void update(float interval) {
        logic.update(interval, mouseInput);
    }

    protected void render() {
        logic.render(window);
        window.update();
    }
}
