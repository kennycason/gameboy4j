package gameboy.graphics;

import gameboy.GameBoy;
import gameboy.keyboard.KeyBoard;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Screen extends Canvas {
	
	private GameBoy gb = GameBoy.getInstance();
	
	private int width = 160;
	
	private int height = 144;
	
	private int zoom = 1;
	
	private int pixelSize = 2;
	
	private int[] data;


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The stragey that allows us to use accelerate page flipping */

	private BufferStrategy strategy;

	public Screen() {
	}

	public void init() {
		// create a frame
		JFrame container = new JFrame("GameBoy Emulator");

		// get hold the content of the frame and set up the
		// resolution
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(pixelSize * width(), pixelSize * height()));
		panel.setLayout(null);

		// setup our canvas size and put it into the content of the frame
		setBounds(0, 0, pixelSize * width(), pixelSize * height());
		panel.add(this);

		// Tell AWT not to bother repainting our canvas since we're
		// going to do that our self in accelerated mode
		setIgnoreRepaint(true);

		// finally make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// add a listener to respond to the user closing the window. If they
		// do we'd like to exit
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// add a key input system (defined below) to our canvas
		// so we can respond to key pressed
		addKeyListener(gb.keyboard);
		requestFocus();

		// create the buffering strategy which will allow AWT
		// to manage our accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		data = new int[width * height * 4];
		for(int i = 0; i < data.length; i++) {
			data[i] = 0xFF;
		}
	}

	public void run() {
		draw();
	}

	private void draw() {
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, pixelSize * width(), pixelSize * height());

		// draw axis
		//g.drawLine(0, (dim * squareSize), (dim * squareSize) * 2, (dim * squareSize));

		// and flip the buffer over
		g.dispose();
		strategy.show();
	}


	
	public void drawSquare(int x, int y, Color c, Graphics2D g) {
		g.setColor(c);
		Rectangle2D rectangle = new Rectangle2D.Float(x * pixelSize, y
				* pixelSize, pixelSize, pixelSize);
		g.fill(rectangle);
		g.draw(rectangle);
	}

	
	public int width() {
		return width;
	}

	public int height() {
		return height;
	}
	
	public void zoom(int zoom) {
		this.zoom = zoom;
	}
	
	public int zoom() {
		return zoom;
	}
	
	
	
}
