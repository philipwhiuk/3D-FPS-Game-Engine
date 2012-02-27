package fps3d;

public abstract class Entity {
	protected Vertex[] vertices;
	protected float xPos, yPos, zPos;
	protected float xStartPos, yStartPos, zStartPos;
	protected void render() {
		//TODO: Entity Render Function
	}
	abstract void update();
	public void resetPosition() {
		xPos = xStartPos; yPos = yStartPos; zPos = zStartPos;
	}
}
