package ego.sample;

import ego.engine.*;
import ego.engine.graph.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Dummy implements GameLogic {
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private float lightAngle;
    private Map map;
    private Vector3f ambientLight;
    private DirectionalLight directionalLight;
    private PointLight pointLight;
    private SpotLight spotLight;
    private static final float CAMERA_POS_STEP = 0.15f;

    private float spotAngle = 0;

    private float spotInc = 1;

    public Dummy() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0f, 3f, 0f);
        camera.movePosition(cameraInc.x, cameraInc.y, cameraInc.z);
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        map = new Map(5,5);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Point Light
        Vector3f lightPosition = new Vector3f(0, 2, 0);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        // Spot Light
        lightPosition = new Vector3f(0, 8f, 2f);
        PointLight sl_pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        att = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        sl_pointLight.setAttenuation(att);
        Vector3f coneDir = new Vector3f(0, -1, 0);
        float cutoff = (float) Math.cos(Math.toRadians(45));
        spotLight = new SpotLight(sl_pointLight, coneDir, cutoff);

        lightPosition = new Vector3f(-1, 0, 0);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_N)) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_M)) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
        }
        lightPos = spotLight.getPointLight().getPosition().z;
        if (window.isKeyPressed(GLFW_KEY_H)) {
            this.spotLight.getPointLight().getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW_KEY_J)) {
            this.spotLight.getPointLight().getPosition().z = lightPos - 0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
                cameraInc.y * CAMERA_POS_STEP,
                cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightMouseButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update spot light direction
        spotAngle += spotInc * 0.05f;
        if (spotAngle > 2) {
            spotInc = -1;
        } else if (spotAngle < -2) {
            spotInc = 1;
        }
        double spotAngleRad = Math.toRadians(spotAngle);
        Vector3f coneDir = spotLight.getConeDirection();
        coneDir.y = (float) Math.sin(spotAngleRad);

        // Update directional light direction, intensity and colour
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float)(Math.abs(lightAngle) - 80)/ 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, map.getBlocks(), ambientLight, pointLight, spotLight,
                directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        map.cleanup();
    }
}
