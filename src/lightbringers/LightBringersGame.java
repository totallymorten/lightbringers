package lightbringers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.HashMap;

import engine.JavaEngine;
import engine.Keys;

public class LightBringersGame extends JavaEngine
{
	

	final int SQ_W = 10;
	final int SQ_H = 10;
	
	FadingSquare[] sqrs = new FadingSquare[WIDTH / SQ_W * HEIGHT / SQ_H];
	
	final int SQ_PR_ROW = WIDTH / SQ_W;
	
	public LightBringersGame(int width, int height, double fps)
	{
		super(width, height, fps);
		
		float alpha = 1.0f;
		int row = 0;
		int col = 0;

		for (int i = 0; i < sqrs.length; i++)
		{
			sqrs[i] = new FadingSquare(col*SQ_W, row*SQ_H, SQ_W, SQ_H, alpha);
			
			alpha = 1.0f;
			//alpha -= (1.0 / sqrs.length);
			
			col++;

			if (i != 0 && i % SQ_PR_ROW == 0)
			{
				row++;
				col = 0;
			}

		}
		
		dude = new Dude();
		
	}
	
	Dude dude;

	private final boolean USE_CACHE = true;
	
	Color baseSqColor = Color.red;
	
	private static HashMap<Float, BufferedImage> imgCache = new HashMap<Float, BufferedImage>();

	private class FadingSquare
	{
		public FadingSquare(int x, int y, int w, int h, float alpha)
		{
			this.w = w;
			this.h = h;
			this.alpha = alpha;
			this.x = x;
			this.y = y;
		}
		
		public int w,h,x,y;
		public float alpha;
		public float fadeSpeed = 0.005f;
		
		
		public void render(Graphics2D g)
		{
			BufferedImage img;
			if (USE_CACHE)
			{
				img = imgCache.get(alpha);
				
				if (img == null)
				{
					img = renderImage();
					imgCache.put(alpha, img);
				}
				
				g.drawImage(img, x, y, null);
			}
			else
			{
				renderDirect(x,y,g);
			}
		}
		
		double fadeDelay = 0.01 * Math.pow(10, 9);
		
		double fadeTimer = fadeDelay;
		
		double view_dist = 150;
		
		public void update(double ns)
		{
			double dist = Math.sqrt(Math.pow(dude.x - x, 2) + Math.pow(dude.y - y, 2)); 
			
			if (dist < view_dist && alpha > dist / view_dist)
			{
				alpha -= fadeSpeed;				
			}
			else
			{
				if (fadeTimer > 0)
					fadeTimer -= ns;
				else
				{
					alpha += fadeSpeed;				
					fadeTimer = fadeDelay;
				}
				
			}
			
			if (alpha < 0)
			{
				alpha = 0;
			}
			else if (alpha > 1.0)
				alpha = 1.0f;

		}
		
		public void renderDirect(int x, int y, Graphics2D g)
		{
			// drawing first rect
			g.setColor(baseSqColor);
			g.fillRect(x, y, w, h);
				
			// drawing second rect
			g.setColor(new Color(0,0,0,alpha));
			g.fillRect(x, y, w, h);						
		}
		
		private BufferedImage renderImage()
		{
			BufferedImage bi;
			Graphics2D g2d;
			BufferedImageOp brOp;
		
			bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			g2d = (Graphics2D) bi.getGraphics();
			this.renderDirect(0,0,g2d);
			g2d.dispose();
			
			return bi;
		}
	}
	
	private class Dude
	{
		public int w=10, h=20;
		public int x=30,y=30;
		
		public Color col = Color.blue;
		
		double speedPrNs = 1.0;
		
		public void render(Graphics2D g2d)
		{
			g2d.setColor(col);
			g2d.fillRect(x, y, w, h);
		}
		
		public void update(double ns)
		{
			if (Keys.check(KeyEvent.VK_D))
				x += speedPrNs;
			if (Keys.check(KeyEvent.VK_S))
				y += speedPrNs;
			if (Keys.check(KeyEvent.VK_A))
				x -= speedPrNs;
			if (Keys.check(KeyEvent.VK_W))
				y -= speedPrNs;
		}
	}
	
	@Override
	public void render(Graphics2D g2d)
	{
		
		for (FadingSquare f : sqrs)
		{
			f.render(g2d);
		}
		
		renderStats(g2d,250);
		g2d.drawString("Cache size: " + imgCache.size(), WIDTH - 250, 80);
		
		dude.render(g2d);
		
		/*
		BufferedImage img = new BufferedImage(500,500,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.createGraphics();
		
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, 1000, 1000);
		
		g2d.setColor(Color.RED);
		g2d.fillRect(100, 100, 100, 100);
		
		g2d.setColor(Color.GREEN);
		g2d.fillRect(300, 300, 100, 100);

		g.setColor(new Color(0.0f,0.0f,0.0f,1.0f));
		//g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 0.4f));
		g.fillRect(0,0,500,500);
		
		
		g.setColor(new Color(0.0f,1.0f,0.0f,1.0f));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 0.4f));
		g.fill(new Rectangle2D.Double(x, y, 300, 300));
		
		g2d.drawImage(img,null,0,0);
		*/
		renderStats(g2d,250);
	}

	double x = 150, y = 150;
	double speedPrNs = 1.0;
	
	
	@Override
	public void update(double ns)
	{
		if (Keys.check(KeyEvent.VK_D))
			x += speedPrNs;
		if (Keys.check(KeyEvent.VK_S))
			y += speedPrNs;
		if (Keys.check(KeyEvent.VK_A))
			x -= speedPrNs;
		if (Keys.check(KeyEvent.VK_W))
			y -= speedPrNs;

		
		dude.update(ns);
		
		for (FadingSquare f : sqrs)
		{
			f.update(ns);
		}
	}

	@Override
	public void preExit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void handlePreCycle()
	{
		// TODO Auto-generated method stub

	}

	public static void main(String[] args)
	{
		(new Thread(new LightBringersGame(1000,1000,5000))).start();
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub
		
	}
}
