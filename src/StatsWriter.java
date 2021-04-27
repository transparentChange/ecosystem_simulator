
import java.io.FileWriter;
import java.io.IOException;

public class StatsWriter {
	private final int LIMIT = 800;
	private final String FILENAME = "populations.csv";
	
	private FileWriter writer = null;
	private int numIterations = 0;
	private boolean active = true;
	
	public StatsWriter() {
		try {
			writer = new FileWriter(FILENAME, false);
			writer.append("Grass");
			writer.append(",");
			writer.append("Sheep");
			writer.append(",");
			writer.append("Wolf");
			writer.append("\n");
			writer.flush();
			writer.close();

			writer = new FileWriter(FILENAME, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
}

	public void exit() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeOut(Organism[][] w) {
		/*
		if (numIterations > LIMIT) {
			if (active) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				active = false;
			} 

			return;
		}*/
		
		numIterations++;
		int countGrass = 0;
		int countSheep = 0;
		int countWolf = 0;
		for (int i = 0; i < w.length; i++) {
			for (int j = 0; j < w[0].length; j++) {
				if (w[i][j] instanceof Grass) {
					countGrass++;
				} else if (w[i][j] instanceof Sheep) {
					countSheep++;
				} else if (w[i][j] instanceof Wolf) {
					countWolf++;
				}
			}
		}
		
		double area = w.length * w[0].length;
		try {
		    writer.append(Double.toString(countGrass));
			writer.append(",");
			writer.append(Double.toString(countSheep));
			writer.append(",");
			writer.append(Double.toString(countWolf));
			writer.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
