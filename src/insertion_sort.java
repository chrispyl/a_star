import java.util.ArrayList;

public class insertion_sort {
	
	public void sort(ArrayList<Integer> openlist, boolean IsStartTile)
	{
		int number;
		if(IsStartTile==true)
		{
			number=8;
		}
		else
		{
			number=5;
		}
		
		for(int size=openlist.size(), i=size-1-number; i<size; i++) 
		{
			for(int j=i; j>0; --j)
			{
				if(Grid.tiles[openlist.get(j-1)].getf() < Grid.tiles[openlist.get(j)].getf())
				{
					int temp = openlist.get(j);
					openlist.set(j, openlist.get(j-1));
					openlist.set(j-1, temp);
				}
			}
		}
	}
}
