import java.awt. *;
import javax.swing. *;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.abs;

public class Grid extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int dimension=165;
	static Tile[] tiles;
	private int start;        //cyan
	private int destination;  //red
	private int cross_cost=5; //cross movement cost
	private int diag_cost=7;  //diagonal movement cost
	
	private ArrayList<Integer> explored = new ArrayList<Integer>();  //the nodes it steps upon
	private ArrayList<Integer> checked = new ArrayList<Integer>();   //the nodes it checks even if they are obstacles, used in order not to redraw every time a change of color happens that doesn't affect the path
	JTextField gcrossTextfield;						
	JTextField gdiagTextfield;	
	
	stateButton change_start_dest;
	stateButton set_Button;						
	stateButton defaultsButton;
	stateButton manhattanButton;
	stateButton diagonalButton;
	stateButton cutButton;
	JLabel heuristicsLabel;
	private boolean manhattanheu=true;
	private boolean diagheu=false;
	private boolean cutcorners=false;
	 
	static final Color exploredcolor = new Color(242/255f, 240/255f, 167/255f);
	static final Color pathcolor = new Color(227/255f, 147/255f, 61/255f);
	static final Color destcolor = new Color(219/255f, 33/255f, 33/255f);
	static final Color startcolor = new Color(146/255f, 232/255f, 223/255f);
	static final Color barriercolor = Color.DARK_GRAY;
	static final Color mapcolor = Color.GRAY;
	static final Color westbuttoncolor = new Color(206/255f, 209/255f, 205/255f);
	static final Color westbuttonforegroundcolor = null;
	
	public static boolean doingSomethingElse=false; //if a_star or reset or drawpath is already running, this will prevent the user to press anything
	
	public boolean exist(int tilenumber, int previoustile)
	{
		if(tilenumber>=(dimension*dimension) || tilenumber<0) return false;
		if(tilenumber%dimension==0 && (previoustile+1)%dimension==0) return false; //this and the one below are put in order to prevent the map from working in a cyclic manner
		if((tilenumber+1)%dimension==0 && previoustile%dimension==0) return false;
		/*
		int x=tilenumber/dimension, y=tilenumber%dimension;	//den xrhsimopoiw ap eutheias ta x,y twn nodes gt an den uparxoun oi komboi kollhse
		if(x<0 || x>(dimension-1) || y<0 || y>(dimension-1)) return false;*/  //if i want to use x, y i put these
		return true;
	}
	
	public void parentsAndGFH(int tile, int currentTile, BinaryHeap openlist, ArrayList<Integer> closedlist)
	{
		int step;
		if(tiles[tile].getBackground()!=barriercolor && closedlist.get(tile)==0)
		{
			if((tile+1==currentTile) || (tile-1==currentTile) || (tile+dimension==currentTile) || (tile-dimension==currentTile))
			{
				step=cross_cost;
			}
			else
			{
				step=diag_cost;
			}
			
			if(openlist.containsKey(tile)==false)
			{
				tiles[tile].seth(calculateH(tile));
				tiles[tile].setPointsTo(currentTile);
				tiles[tile].setg(tiles[currentTile].getg()+step);
				tiles[tile].setf(tiles[tile].getg()+tiles[tile].geth());
				openlist.insert(tiles[tile]);
			}
			else
			{
				if(tiles[currentTile].getg()+step<tiles[tile].getg()) 
				{
					tiles[tile].setPointsTo(currentTile);
					tiles[tile].setg(tiles[currentTile].getg()+step);
					tiles[tile].setf(tiles[tile].getg()+tiles[tile].geth());
				}
			}
		}
	}
	
	public void reset()
	{
		setVisible(false);		 
		for(int size=explored.size(), i=0; i<size; i++)  
		{
			int tilenumber=explored.get(i);
			Color tilecolor=tiles[tilenumber].getBackground();
			if(tilecolor==pathcolor || tilecolor==exploredcolor) tiles[tilenumber].setBackground(mapcolor);
		}
		setVisible(true);	
		explored.clear();
		checked.clear();
	}

	public void a_star()
	{
		Long time=System.nanoTime();
		int d=dimension*dimension;
		boolean reseted=false;
		BinaryHeap openlist = new BinaryHeap(d);
		ArrayList<Integer> closedlist = new ArrayList<Integer>();
		for(int size=dimension*dimension, i=0; i<size; i++) {closedlist.add(0);}
		int currentTile=start;
		tiles[start].seth(calculateH(start));
		tiles[start].setf(tiles[start].geth()); 	//for nicer tooltip reasons
		tiles[start].setg(0);
		tiles[destination].setPointsTo(-1); //helps in drawPath to check if it reached desitnation
		while(currentTile!=destination)
		{
			closedlist.set(currentTile, 1);
			if(currentTile>dimension && currentTile<d-dimension && currentTile%dimension!=0  && (currentTile+1)%dimension!=0)
			{
				parentsAndGFH(currentTile-1, currentTile, openlist, closedlist); checked.add(currentTile-1);
				parentsAndGFH(currentTile+1, currentTile, openlist, closedlist);  checked.add(currentTile+1);
				parentsAndGFH(currentTile-dimension, currentTile, openlist, closedlist); checked.add(currentTile-dimension);
				parentsAndGFH(currentTile+dimension, currentTile, openlist, closedlist); checked.add(currentTile+dimension);
				if(cutcorners)
				{
					if(tiles[currentTile-1].getBackground()!=barriercolor && tiles[currentTile-dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile-dimension-1, currentTile, openlist, closedlist); checked.add(currentTile-dimension-1);}
					if(tiles[currentTile-1].getBackground()!=barriercolor && tiles[currentTile+dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile+dimension-1, currentTile, openlist, closedlist); checked.add(currentTile+dimension-1);}
					if(tiles[currentTile+1].getBackground()!=barriercolor && tiles[currentTile-dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile-dimension+1, currentTile, openlist, closedlist); checked.add(currentTile-dimension+1);}
					if(tiles[currentTile+1].getBackground()!=barriercolor && tiles[currentTile+dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile+dimension+1, currentTile, openlist, closedlist); checked.add(currentTile+dimension-1);}
				}
				else
				{
					parentsAndGFH(currentTile-dimension-1, currentTile, openlist, closedlist); checked.add(currentTile-dimension-1);
					parentsAndGFH(currentTile+dimension-1, currentTile, openlist, closedlist); checked.add(currentTile+dimension-1);
					parentsAndGFH(currentTile-dimension+1, currentTile, openlist, closedlist); checked.add(currentTile-dimension+1);
					parentsAndGFH(currentTile+dimension+1, currentTile, openlist, closedlist); checked.add(currentTile+dimension+1);
				}
			}
			else
			{	
				if(exist(currentTile-1, currentTile)) {parentsAndGFH(currentTile-1, currentTile, openlist, closedlist); checked.add(currentTile-1);}
				if(exist(currentTile+1, currentTile)) {parentsAndGFH(currentTile+1, currentTile, openlist, closedlist);  checked.add(currentTile+1);}
				if(exist(currentTile-dimension, currentTile)) {parentsAndGFH(currentTile-dimension, currentTile, openlist, closedlist); checked.add(currentTile-dimension);}
				if(exist(currentTile+dimension, currentTile)) {parentsAndGFH(currentTile+dimension, currentTile, openlist, closedlist); checked.add(currentTile+dimension);}
				if(cutcorners)
				{
					if(exist(currentTile-1, currentTile) && exist(currentTile-dimension, currentTile) && tiles[currentTile-1].getBackground()!=barriercolor && tiles[currentTile-dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile-dimension-1, currentTile, openlist, closedlist); checked.add(currentTile-dimension-1);}
					if(exist(currentTile-1, currentTile) && exist(currentTile+dimension, currentTile) && tiles[currentTile-1].getBackground()!=barriercolor && tiles[currentTile+dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile+dimension-1, currentTile, openlist, closedlist); checked.add(currentTile+dimension-1);}
					if(exist(currentTile+1, currentTile) && exist(currentTile-dimension, currentTile) && tiles[currentTile+1].getBackground()!=barriercolor && tiles[currentTile-dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile-dimension+1, currentTile, openlist, closedlist); checked.add(currentTile-dimension+1);}
					if(exist(currentTile+1, currentTile) && exist(currentTile+dimension, currentTile) && tiles[currentTile+1].getBackground()!=barriercolor && tiles[currentTile+dimension].getBackground()!=barriercolor) {parentsAndGFH(currentTile+dimension+1, currentTile, openlist, closedlist); checked.add(currentTile+dimension-1);}
				}
				else
				{
					if(exist(currentTile-dimension-1, currentTile)) {parentsAndGFH(currentTile-dimension-1, currentTile, openlist, closedlist); checked.add(currentTile-dimension-1);}
					if(exist(currentTile+dimension-1, currentTile)) {parentsAndGFH(currentTile+dimension-1, currentTile, openlist, closedlist); checked.add(currentTile+dimension-1);}
					if(exist(currentTile-dimension+1, currentTile)) {parentsAndGFH(currentTile-dimension+1, currentTile, openlist, closedlist); checked.add(currentTile-dimension+1);}
					if(exist(currentTile+dimension+1, currentTile)) {parentsAndGFH(currentTile+dimension+1, currentTile, openlist, closedlist); checked.add(currentTile+dimension+1);}
				}
			}
			
			if(openlist.heapEmpty())
			{
				reseted=true;
				break;
			}

			currentTile=openlist.getMin().getNumber();
			//System.out.print(tiles[currentTile].getNumber()+" ");
			if(currentTile!=destination) explored.add(currentTile);
		}
		System.out.println((System.nanoTime()-time)*0.000001+" ms");
		if(reseted) reset();	//it could be located inside the openlist.isEmpty() brackets but it would be counted in the execution time a_star does 
	}
	
	public void drawPath()
	{
		final long wait=400/dimension;  

		for(int size=explored.size(), i=0; i<size; i++)
		{
			tiles[explored.get(i)].setBackground(exploredcolor);
		//	mysleep(wait);
		}
		
		int i=destination;
		if(tiles[destination].getPointsTo()!=-1) //if a* couldn't finish, so there isn't a path to destination
		{
			while(i!=start)
			{
				i=tiles[i].getPointsTo();
				if(i!=start) tiles[i].setBackground(pathcolor);
			//	mysleep(wait);
			}
		}
	}
	
	public int calculateH(int tile) 
	{
		int h=0;
		int tilex=tile/dimension, tiley=tile%dimension, destinationx=destination/dimension, destinationy=destination%dimension;
		int distx=abs(tilex-destinationx);       
		int disty=abs(tiley-destinationy);
		if(diagheu)	//diagonal shorcut
		{
			if(distx>disty)
			{
				h = diag_cost*disty + cross_cost*(distx-disty);
			}
			else
			{
				h = diag_cost*distx + cross_cost*(disty-distx);
			}
		}
		else  // else do manhattan distance
		{
			h=cross_cost*(distx+disty);	//it could also be without *cross_cost but this improves the heuristic, only for manhattan distance //H should be in the same scale with cross_cost
		} 
		return h;
	}					   
	
	public void mysleep(long howmuch)
	{
		try {		
			Thread.sleep(howmuch);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	public void beautifyMap()
	{
		final int d=dimension*dimension;
		
		for(int i=0; i<d; i++)
		{
			if(exist(i+1,i) && exist(i+dimension, i) && exist(i+dimension+1, i))
			{
				if(tiles[i].getBackground()==barriercolor && tiles[i+dimension+1].getBackground()==barriercolor)
				{
					tiles[i+1].setBackground(barriercolor);
					tiles[i+dimension].setBackground(barriercolor);
				}
				else if(tiles[i+1].getBackground()==barriercolor && tiles[i+dimension].getBackground()==barriercolor)
				{
					tiles[i].setBackground(barriercolor);
					tiles[i+dimension+1].setBackground(barriercolor);
				}
			}
		}	
		
		for(int j=0; j<3; j++)
		{
			for(int i=0; i<d; i++)
			{
				int count=0;
				if(tiles[i].getBackground()==barriercolor)
				{
					if(exist(i-1, i) && tiles[i-1].getBackground()==barriercolor) count++;
					if(exist(i+1, i) && tiles[i+1].getBackground()==barriercolor) count++;
					if(exist(i-dimension-1, i) && tiles[i-dimension-1].getBackground()==barriercolor) count++;
					if(exist(i-dimension+1, i) && tiles[i-dimension+1].getBackground()==barriercolor) count++;
					if(exist(i+dimension-1, i) && tiles[i+dimension-1].getBackground()==barriercolor) count++;
					if(exist(i+dimension+1, i) && tiles[i+dimension+1].getBackground()==barriercolor) count++;
					if(exist(i-dimension, i) && tiles[i-dimension].getBackground()==barriercolor) count++;
					if(exist(i+dimension, i) && tiles[i+dimension].getBackground()==barriercolor) count++;
				}
				
				if(j==0 || j==1)
				{
					if(count<4) tiles[i].setBackground(mapcolor);
				}
				else //j==2
				{
					if(count==0) tiles[i].setBackground(mapcolor);
				}
			}
		}
		tiles[start].setBackground(startcolor);
		tiles[destination].setBackground(destcolor);
	}
	
	public void populateGrid()
	{
		final int d=dimension*dimension;
		final int chanceOfBarrier=25; //%
		Random rand = new Random();
		rand.setSeed(System.nanoTime());
		start=rand.nextInt(d);
		do
		{
			destination=rand.nextInt(d);
		}while(start==destination);
		tiles = new Tile[d];
		
		for(int i=0; i<d; i++)
		{
			tiles[i]= new Tile(i);
			if(i!=destination && i!=start)
			{
				if(rand.nextInt(100)<chanceOfBarrier) 
				{
					tiles[i].setBackground(barriercolor);
				}
				else
				{
					tiles[i].setBackground(mapcolor);
				}
			}
			else if(i==start)
			{
				tiles[i].setBackground(startcolor);
			}
			else
			{
				tiles[i].setBackground(destcolor);
			}
		}
	}
	
	public void createGUI()
	{
		final int d=dimension*dimension;
		JPanel mapPanel = new JPanel();
		GridLayout mapLayout = new GridLayout(dimension, dimension, 0, 0);
		mapPanel.setLayout(mapLayout);
		for(int i=0; i<d; i++)	mapPanel.add(tiles[i]);
		
		JPanel basePanel = new JPanel();
		BorderLayout baseLayout = new BorderLayout();
		basePanel.setLayout(baseLayout);	
		
		JPanel westPanel = new JPanel();		
		GridLayout westPanelGridLayout = new GridLayout(15, 1, 0, 2);
		westPanel.setLayout(westPanelGridLayout);
		
		change_start_dest = new stateButton("Change start and destination");
		change_start_dest.setBackground(westbuttoncolor);
		change_start_dest.setForeground(westbuttonforegroundcolor);
		change_start_dest.setOpaque(true);
		
		set_Button = new stateButton("Set");
		set_Button.setBackground(westbuttoncolor);
		set_Button.setForeground(westbuttonforegroundcolor);
		set_Button.setOpaque(true);
		
		defaultsButton = new stateButton("Defaults");
		defaultsButton.setBackground(westbuttoncolor);
		defaultsButton.setForeground(westbuttonforegroundcolor);
		defaultsButton.setOpaque(true);
		
		manhattanButton = new stateButton("Manhattan Distance");
		manhattanButton.setBackground(westbuttoncolor);
		manhattanButton.setForeground(westbuttonforegroundcolor);
		manhattanButton.setOpaque(true);
		
		diagonalButton = new stateButton("Diagonal shorcut");
		diagonalButton.setBackground(westbuttoncolor);
		diagonalButton.setForeground(westbuttonforegroundcolor);
		diagonalButton.setOpaque(true);
		
		cutButton = new stateButton("Switch to cut corners");
		cutButton.setBackground(westbuttoncolor);
		cutButton.setForeground(westbuttonforegroundcolor);
		cutButton.setOpaque(true);
		
		gcrossTextfield = new JTextField(Integer.toString(cross_cost), 8);
		gcrossTextfield.setToolTipText("Cross movement cost");
		gdiagTextfield= new JTextField(Integer.toString(diag_cost), 8);
		gdiagTextfield.setToolTipText("Diagonal movement cost");
		
		JPanel movcostlabelPanel = new JPanel();
		JLabel costs = new JLabel("Set cross and diagonal costs:");
		BorderLayout weightsLayout = new BorderLayout();
		movcostlabelPanel.setLayout(weightsLayout);
		movcostlabelPanel.add(costs, BorderLayout.SOUTH);
		
		JPanel heuristicsPanel = new JPanel();
		heuristicsLabel = new JLabel("Heuristics: current is Manhattan distance");
		BorderLayout heuLayout = new BorderLayout();
		heuristicsPanel.setLayout(heuLayout);
		heuristicsPanel.add(heuristicsLabel, BorderLayout.SOUTH);
		
		JPanel cutCornersPanel = new JPanel();
		JLabel cutCornersLabel = new JLabel("Cut corners:");
		BorderLayout cutLayout = new BorderLayout();
		cutCornersPanel.setLayout(cutLayout);
		cutCornersPanel.add(cutCornersLabel, BorderLayout.SOUTH);
		
		JPanel movcostControls= new JPanel();
		movcostControls.add(set_Button);
		movcostControls.add(gcrossTextfield);
		movcostControls.add(gdiagTextfield);
		
		westPanel.add(change_start_dest);
		westPanel.add(movcostlabelPanel);
		westPanel.add(movcostControls);
		westPanel.add(defaultsButton);
		westPanel.add(heuristicsPanel);
		westPanel.add(manhattanButton);
		westPanel.add(diagonalButton);
		westPanel.add(cutCornersPanel);
		westPanel.add(cutButton);
		basePanel.add(westPanel, BorderLayout.LINE_END);
		basePanel.add(mapPanel, BorderLayout.CENTER);
		add(basePanel);
	}
	
	public void saveMap()
	{
		final int d=dimension*dimension;
		PrintWriter writer;
		try {
			writer = new PrintWriter("map.txt", "UTF-8");
			for(int i=0; i<d; i++) 
			{
				if(tiles[i].getBackground()==barriercolor) 
				{
					writer.print(1);
				}
				else
				{
					writer.print(0);
				}
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Grid()
	{
		super("A*");
		setSize(795, 560); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		populateGrid();
		//beautifyMap();
		//saveMap();
		createGUI();
		setLocationRelativeTo(null); //gia na einai sto kentro ths othonhs
		setVisible(true);
		setResizable(false);
	}
	
	public static void main(String[] args)
	{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true"); //to prevent a JDK bug
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		 
		Grid frame = new Grid();
		//final int d=frame.dimension*frame.dimension;
		final long sleep=100;
		
		frame.mysleep(500);		//in order not to start before the grid is filled
		
		frame.a_star();
		frame.drawPath();
		while(true)
		{
			if(frame.cutButton.state==true)
			{
				frame.cutButton.state=false;
				frame.cutcorners=!frame.cutcorners;
				if(frame.cutcorners==true)
				{
					frame.cutButton.setText("Switch to not to cut corners");
				}
				else
				{
					frame.cutButton.setText("Switch to cut corners");
				}
				Grid.doingSomethingElse=true;
				frame.reset();
				frame.a_star();
				frame.drawPath();
				Grid.doingSomethingElse=false;
			}
			
			if(frame.manhattanButton.state==true)
			{
				frame.manhattanButton.state=false;
				frame.manhattanheu=true;
				frame.diagheu=false;
				frame.heuristicsLabel.setText("Heuristics: current is Manhattan distance");
				Grid.doingSomethingElse=true;
				frame.reset();
				frame.a_star();
				frame.drawPath();
				Grid.doingSomethingElse=false;
			}
			
			if(frame.diagonalButton.state==true)
			{
				frame.diagonalButton.state=false;
				frame.diagheu=true;
				frame.manhattanheu=false;
				frame.heuristicsLabel.setText("Heuristics: current is diagonal shorcut");
				Grid.doingSomethingElse=true;
				frame.reset();
				frame.a_star();
				frame.drawPath();
				Grid.doingSomethingElse=false;
			}
			
			if(frame.defaultsButton.state==true)
			{
				frame.defaultsButton.state=false;
				frame.cross_cost=5;	
				frame.diag_cost=7;
				frame.gcrossTextfield.setText("5");
				frame.gdiagTextfield.setText("7");
				Grid.doingSomethingElse=true;
				frame.reset();
				frame.a_star();
				frame.drawPath();
				Grid.doingSomethingElse=false;
			}
			
			if(frame.set_Button.state==true)
			{
				frame.set_Button.state=false;
				frame.cross_cost=Integer.parseInt(frame.gcrossTextfield.getText());
				frame.diag_cost=Integer.parseInt(frame.gdiagTextfield.getText());
				Grid.doingSomethingElse=true;
				frame.reset();
				frame.a_star();
				frame.drawPath();
				Grid.doingSomethingElse=false;
			}
			
			if(frame.change_start_dest.state==true) 
			{
				frame.change_start_dest.state=false;
				frame.reset();
				boolean startset=false;
				boolean destset=false;
			    Grid.tiles[frame.start].setBackground(mapcolor);
			    Grid.tiles[frame.destination].setBackground(mapcolor);
			    while(!startset)	//mhn proxwrhseis an den pathsei kapou sto xarth gia start
			    {
			    	frame.mysleep(sleep);

			    	if(Tile.tileNumberChanged!=-1)
			    	{
			    		Grid.tiles[Tile.tileNumberChanged].setBackground(startcolor);
		    			frame.start=Tile.tileNumberChanged;
		    			startset=true;
		    			Tile.tileNumberChanged=-1;
			    	}
			    	/*
			    	for(int i=0; i<d; i++)
			    	{
			    		if(Grid.tiles[i].changed==true)
			    		{
			    			Grid.tiles[i].changed=false;
			    			Grid.tiles[i].setBackground(startcolor);
			    			frame.start=i;
			    			startset=true;
			    			break;
			    		}
			    	}*/
			    }
			    while(!destset)		//don't contunue if the user hasn't stepped somewhere for destination
			    {
			    	frame.mysleep(sleep);

			    	if(Tile.tileNumberChanged!=-1)
			    	{
			    		if(Tile.tileNumberChanged!=frame.start) //to prevent destination be in the same spot as start
			    		{
				    		Grid.tiles[Tile.tileNumberChanged].setBackground(destcolor);
			    			frame.destination=Tile.tileNumberChanged;
			    			destset=true;
			    			Tile.tileNumberChanged=-1;
			    		}
			    	}
			    	
			    	/*
			    	for(int i=0; i<d; i++)
			    	{
			    		if(Grid.tiles[i].changed==true)
			    		{
			    			Grid.tiles[i].changed=false;
			    			Grid.tiles[i].setBackground(destcolor);
			    			frame.destination=i;
			    			destset=true;
			    			break;
			    		}
			    	}*/
			    }
			    Grid.doingSomethingElse=true;
			    frame.a_star();
			    frame.drawPath();
			    Grid.doingSomethingElse=false;
			}
			
			if(Tile.tileNumberChanged!=-1){
			//for(int i=0; i<d;i++)	//check if a button is pressed somewhere in the map
			//{
				//if(Grid.tiles[i].changed==true)
				//{
					//Grid.tiles[Tile.tileNumberChanged].changed=false;
					if(frame.checked.contains(Tile.tileNumberChanged) || frame.checked.isEmpty())
					{
						Grid.doingSomethingElse=true;
						frame.reset();
						frame.a_star();
						frame.drawPath();
						Grid.doingSomethingElse=false;
					}
					Tile.tileNumberChanged=-1;
					//break;
			}
				//}
			//}
			
			///////////////////////////////////////////////////////////////////////////////////////////////////
			frame.mysleep(sleep);	//to prevent the program from eating cpu cycles for no reason			
		}
	}	
}