package finalproject;

import java.util.HashMap;
import java.util.ArrayList;

public class SearchEngine {
	public HashMap<String, ArrayList<String>> wordIndex; // this will contain a set of pairs (String, LinkedList of
															// Strings)
	public MyWebGraph internet;
	public XmlParser parser;

	public SearchEngine(String filename) throws Exception {
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}

	/*
	 * This does a graph traversal of the web, starting at the given url. For each
	 * new page seen, it updates the wordIndex, the web graph, and the set of
	 * visited vertices.
	 * 
	 * This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		// TODO : Add code here
		// create url vertex in webgraph (internet)

		this.internet.addVertex(url);

		if (!this.internet.getVisited(url)) {
			ArrayList<String> contents = this.parser.getContent(url);
			for (String word : contents) {
				word = word.toLowerCase();
				if (!this.wordIndex.containsKey(word)) {
					ArrayList<String> urlList = new ArrayList<String>();
					urlList.add(url);
					this.wordIndex.put(word, urlList);
				} else {
					ArrayList<String> urlList = this.wordIndex.get(word);
					urlList.add(url);
				}
			}
		}

		this.internet.setVisited(url, true);

		ArrayList<String> urls = this.parser.getLinks(url);

		for (String link : urls) {
			// check if url vertex exists / is visited already
			// if not, crawl and index
			if (!this.internet.getVisited(link)) {
				this.internet.addVertex(link);

				this.internet.addEdge(url, link);

				crawlAndIndex(link);
			} else {
				this.internet.addEdge(url, link);
			}

		}

	}

	/*
	 * This computes the pageRanks for every vertex in the web graph. It will only
	 * be called after the graph has been constructed using crawlAndIndex(). To
	 * implement this method, refer to the algorithm described in the assignment
	 * pdf.
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		// set all page ranks = 1
		ArrayList<String> urls = this.internet.getVertices();
		for (String url : urls) {
			this.internet.setPageRank(url, 1);
		}

		ArrayList<Double> previousPageRanks = new ArrayList<Double>();
		for (String url : urls) {

			previousPageRanks.add(this.internet.getPageRank(url));

		}

		boolean converged = false;

		while (!converged) {

			ArrayList<Double> newPageRanks = computeRanks(urls);
			int numUrls = urls.size();
			for (int i = 0; i < numUrls; i++) {
				Double prevPageRank = previousPageRanks.get(i);
				Double newPageRank = newPageRanks.get(i);
				Double difference = Math.abs(newPageRank - prevPageRank);

				// check if difference less than epsilon for all urls
				if (difference < epsilon) {
					converged = true;

				}
				// if ever we get difference > epsilon, compute page ranks again
				else {
					converged = false;
					break;
				}
			}
			setRanks(urls, newPageRanks);
			if (!converged) {
				previousPageRanks = newPageRanks;
			}

		}

		// rank of page is sum of all ranks of vertices going into page

		// TODO : Add code here
	}

	private void setRanks(ArrayList<String> urls, ArrayList<Double> ranks) {
		if (urls.size() != ranks.size()) {
			return;
		}
		for (int i = 0; i < urls.size(); i++) {

			String url = urls.get(i);
			Double rank = ranks.get(i);
			this.internet.setPageRank(url, rank);

		}

	}

	/*
	 * The method takes as input an ArrayList<String> representing the urls in the
	 * web graph and returns an ArrayList<double> representing the newly computed
	 * ranks for those urls. Note that the double in the output list is matched to
	 * the url in the input list using their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		// TODO : Add code here

		// list of ranks to hold value

		// for vertex in vertices, store in list of ranks with key url, value rank
		//
		ArrayList<Double> ranks = new ArrayList<Double>();

		for (String vertex : vertices) {

			ArrayList<String> inUrls = this.internet.getEdgesInto(vertex);
			Double inRanks = 0.0;

			for (String url : inUrls) {

				inRanks += this.internet.getPageRank(url) / this.internet.getOutDegree(url);

			}

			Double rank = 0.5 + 0.5 * inRanks;
			ranks.add(rank);

		}

		return ranks;
	}

	/*
	 * Returns a list of urls containing the query, ordered by rank Returns an empty
	 * list if no web site contains the query.
	 * 
	 * This method should take about 25 lines of code.
	 */
	public ArrayList<String> getResults(String query) {
		// TODO: Add code here

		// get arraylist that contains query
		// create a hashmap mapping url to rank
		ArrayList<String> matchingUrls = this.wordIndex.get(query);
		query = query.toLowerCase();

		HashMap<String, Double> urlAndRank = new HashMap<String, Double>();

		for (String url : matchingUrls) {

			Double urlRank = this.internet.getPageRank(url);
			urlAndRank.put(url, urlRank);

		}

		ArrayList<String> results = Sorting.fastSort(urlAndRank);

		return results;
	}
}
