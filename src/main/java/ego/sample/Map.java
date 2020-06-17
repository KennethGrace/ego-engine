package ego.sample;

import ego.engine.GameItem;
import ego.engine.graph.Material;
import ego.engine.graph.Mesh;
import ego.engine.graph.OBJLoader;
import ego.engine.graph.Texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private final List<GameItem> blocks;

    public Map(int length, int width) throws Exception{
        blocks = new ArrayList<>();
        //Random rand = new Random(614010);
        float reflectance = 0.1f;
        Texture texture = new Texture("textures/grassblock.png");
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);
        float scale = 0.5f;
        float xMin = (-width/2f)*(scale*2)+(scale);
        float xMax = (width/2f)*(scale*2)-(scale);
        float x = xMin;
        float z = (-length/2f)*(scale*2)+(scale);
        for (int i = 0; i < width*length; i++) {
            blocks.add(new GameItem(mesh));
            GameItem block = blocks.get(blocks.size() - 1);
            float y = 0;
            block.setPosition(x, y, z);
            block.setScale(scale);
            x += scale*2;
            if(x > xMax){
                x = xMin;
                z += scale*2;
            }
        }
    }

    public List<GameItem> getBlocks() {
        return blocks;
    }

    public void cleanup() {
        for (GameItem block : blocks) {
            block.getMesh().cleanUp();
        }
    }
}
