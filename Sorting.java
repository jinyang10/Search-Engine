package finalproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry; 


public class Sorting {

	/*
	 * This method takes as input an HashMap with values that are Comparable. It
	 * returns an ArrayList containing all the keys from the map, ordered in
	 * descending order based on the values they mapped to.
	 * 
	 * The time complexity for this method is O(n^2) as it uses bubble sort, where n
	 * is the number of pairs in the map.
	 */
	public static <K, V extends Comparable> ArrayList<K> slowSort(HashMap<K, V> results) {
		ArrayList<K> sortedUrls = new ArrayList<K>();
		sortedUrls.addAll(results.keySet()); // Start with unsorted list of urls

		int N = sortedUrls.size();
		for (int i = 0; i < N - 1; i++) {
			for (int j = 0; j < N - i - 1; j++) {
				if (results.get(sortedUrls.get(j)).compareTo(results.get(sortedUrls.get(j + 1))) < 0) {
					K temp = sortedUrls.get(j);
					sortedUrls.set(j, sortedUrls.get(j + 1));
					sortedUrls.set(j + 1, temp);
				}
			}
		}
		return sortedUrls;
	}

	/*
	 * This method takes as input an HashMap with values that are Comparable. It
	 * returns an ArrayList containing all the keys from the map, ordered in
	 * descending order based on the values they mapped to.
	 * 
	 * The time complexity for this method is O(n*log(n)), where n is the number of
	 * pairs in the map.
	 */
	public static <K, V extends Comparable> ArrayList<K> fastSort(HashMap<K, V> results) {
		// ADD YOUR CODE HERE
		ArrayList<K> sortedUrls = new ArrayList<K>();
		sortedUrls.addAll(results.keySet()); // Start with unsorted list of urls

		int N = sortedUrls.size();
		ArrayList<K> helper = new ArrayList<K>();

		// helper array to store sorted arrayList
		for (int i = 0; i < N; i++) {
			helper.add(sortedUrls.get(i));
		}
		mergeSort(results, sortedUrls, helper, 0, N - 1);

		return sortedUrls;
	}

	private static <K, V extends Comparable> void mergeSort(HashMap<K, V> result, ArrayList<K> list,
			ArrayList<K> helper, int leftIndex, int rightIndex) {
		if (leftIndex >= rightIndex) {
			return;
		}
		int middle = (leftIndex + rightIndex) / 2;
		mergeSort(result, list, helper, leftIndex, middle);
		mergeSort(result, list, helper, middle + 1, rightIndex);

		merge(result, list, helper, leftIndex, middle, rightIndex);

	}

	private static <K, V extends Comparable> void merge(HashMap<K, V> results, ArrayList<K> list, ArrayList<K> helper,
			int leftIndex, int middle, int rightIndex) {

		for (int i = leftIndex; i < rightIndex + 1; i++) {
			K key = list.get(i);
			helper.set(i, key);
		}

		int helperLeft = leftIndex;
		int helperRight = middle + 1;
		int current = leftIndex;

		while (helperLeft <= middle && helperRight <= rightIndex) {
			// if left is bigger, copy left into array and move pointer to next element in
			// "LEFT" array
			if (results.get(helper.get(helperLeft)).compareTo(results.get(helper.get(helperRight))) > 0) {

				list.set(current, helper.get(helperLeft));
				helperLeft++;

			}
			// if right is bigger, copy right into array and move pointer to next element
			// in right array
			else {

				list.set(current, helper.get(helperRight));
				helperRight++;

			}

			// moving to the next smallest element in the array
			current++;

		}

		int notCopied = middle - helperLeft;
		for (int i = 0; i < notCopied + 1; i++) {

			list.set(current + i, helper.get(helperLeft + i));

		}

	}

}