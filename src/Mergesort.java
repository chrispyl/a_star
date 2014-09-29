import java.util.ArrayList;

public class Mergesort {
  private ArrayList<Integer> numbers;
  private ArrayList<Integer> helper;
  private int number;

  public void sort(ArrayList<Integer> values) {
    numbers = values;
    number = values.size();
    helper = new ArrayList<Integer>();
    for(int i=0; i<number; i++) helper.add(0); 
    mergesort(0, number - 1);
  }

  private void mergesort(int low, int high) {
    // check if low is smaller then high, if not then the array is sorted
    if (low < high) {
      // Get the index of the element which is in the middle
      int middle = low + (high - low) / 2;
      // Sort the left side of the array
      mergesort(low, middle);
      // Sort the right side of the array
      mergesort(middle + 1, high);
      // Combine them both
      merge(low, middle, high);
    }
  }

  private void merge(int low, int middle, int high) {

    // Copy both parts into the helper array
    for (int i = low; i <= high; i++) {
      helper.set(i, numbers.get(i));
    }

    int i = low;
    int j = middle + 1;
    int k = low;
    // Copy the smallest values from either the left or the right side back
    // to the original array
    while (i <= middle && j <= high) {
      if (Grid.tiles[helper.get(i)].getf() > Grid.tiles[helper.get(j)].getf()) { //xwris to = gia drunken bot
        numbers.set(k, helper.get(i));
        i++;
      } else {
        numbers.set(k, helper.get(j));
        j++;
      }
      k++;
    }
    // Copy the rest of the left side of the array into the target array
    while (i <= middle) {
      numbers.set(k, helper.get(i));
      k++;
      i++;
    }

  }
} 