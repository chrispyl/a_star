import static java.lang.Math.floor;

public class BinaryHeap {
	
	Tile[] array;
	int iter=1;	//to iter deixnei thn thesi pou tha topotheththei to epomeno stoixeio molis ginei to insert prin to percolate up
	
	public boolean isEmpty()
	{
		if(iter==1) return true;
		return false;
	}
	
	public BinaryHeap(int size)
	{
		array = new Tile[size];
	}
	
	public Tile getMin()
	{
		Tile min=array[1];
		
		array[1]=array[iter-1];
		array[iter-1]=null; 	//keno
		iter--;
		
		int currentIndex=1;
		
		while(getLeftChildIndex(currentIndex)<=iter-1)
		{
			if(getRightChildIndex(currentIndex)<=iter-1)
			{
				if(array[currentIndex].getf()>array[getLeftChildIndex(currentIndex)].getf() || array[currentIndex].getf()>array[getRightChildIndex(currentIndex)].getf())
				{
					if(array[getLeftChildIndex(currentIndex)].getf()>array[getRightChildIndex(currentIndex)].getf())
					{
						Tile temp=array[getRightChildIndex(currentIndex)];
						array[getRightChildIndex(currentIndex)]=array[currentIndex];
						array[currentIndex]=temp;
						currentIndex=getRightChildIndex(currentIndex);
					}
					else
					{
						Tile temp=array[getLeftChildIndex(currentIndex)];
						array[getLeftChildIndex(currentIndex)]=array[currentIndex];
						array[currentIndex]=temp;
						currentIndex=getLeftChildIndex(currentIndex);
					}
				}
				else
				{
					break;
				}
			}
			else
			{
				if(array[currentIndex].getf()>array[getLeftChildIndex(currentIndex)].getf())
				{
					Tile temp=array[getLeftChildIndex(currentIndex)];
					array[getLeftChildIndex(currentIndex)]=array[currentIndex];
					array[currentIndex]=temp;
					currentIndex=getLeftChildIndex(currentIndex);
				}
				else
				{
					break;
				}
			}
		}
		
		return min;
	}
	
	public int getParent(int index)
	{
		return (int) floor(index/2);
	}
	
	public int getLeftChildIndex(int index)
	{
		return 2*index;
	}
	
	public int getRightChildIndex(int index)
	{
		return 2*index+1;
	}
	
	public void printArray()
	{
		for(int i=0; i<array.length; i++) System.out.print(" " + array[i] + " ");
		System.out.println();
	}
	
	public void insert(Tile element)
	{
		array[iter]=element;
		
		int currentIndexOfElement=iter;
		int parent=getParent(currentIndexOfElement);
		while(parent!=0 && array[parent].getf()>element.getf()) //leitourgei braxukuklwtika, an htan prwta h deuterh sunthhkh tha eprepe olo to block na empaine se mia if pou tha elegxe poso einai to iter giati otan einai 1 o parent einai 0 kai array[0] einai null opote pairnw nullpointerexception
		{
			Tile temp=array[parent];
			array[parent]=array[currentIndexOfElement];
			array[currentIndexOfElement]=temp;
			currentIndexOfElement=parent;
			parent=getParent(parent);
		}
		iter++;
	}
}

