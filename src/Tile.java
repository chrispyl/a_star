import java.awt.event. *;

import javax.swing. *;

public class Tile extends JButton implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	private int h, f, g, pointsTo;
	private final int number;
	//int x, y;
	//boolean changed=false;
	public static int tileNumberChanged=-1;		//when no change occurs stays -1, if a button is pressed it takes its' number
	/*
	public int getx()
	{
		return x;
	}
	
	public int gety()
	{
		return y;
	}
	*/
	public void setPointsTo(int pointsto)
	{
		this.pointsTo=pointsto;
	}
	
	public  int getPointsTo()
	{
		return pointsTo;
	}
	
	public void setg(int g)
	{
		this.g=g;
	}
	
	public int getg()
	{
		return g;
	}
	
	public void seth(int h)
	{
		this.h=h;
	}
	
	public int geth()
	{
		return h;
	}
	
	public void setf(int f)
	{
		this.f=f;
	}
	
	public int getf()
	{
		return f;
	}
	
	public int getNumber()
	{
		return number;
	}
	
	public Tile(int number)
	{
		super();
		//x=number/dimension;
		//y=number%dimension;
		this.number=number;
	        setBorderPainted(false);
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(Grid.doingSomethingElse==false)
		{
			if(getBackground() == Grid.barriercolor)
			{
				setBackground(Grid.mapcolor);
			}
	    	else if(getBackground()==Grid.startcolor || getBackground()==Grid.destcolor)
	    	{
	    		//do nothing
	    	}
			else
			{
				setBackground(Grid.barriercolor);
			}
	    	//changed=true;
	    	tileNumberChanged=number;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		setToolTipText("F:"+f+" G:"+g+" H:"+h);
		/*
		for(int i=0;i<8;i++) System.out.println();
		System.out.println("f:"+f+" h:"+h+" g:"+g+" pointsto:"+pointsto+" number:"+number);
		*/
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
