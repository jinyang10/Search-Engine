package SearchEngine;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;

	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does a graph traversal of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 *
	 */
	public void crawlAndIndex(String url) throws Exception {

		// create url vertex in webgraph (internet)
		this.internet.addVertex(url);

		if (!this.internet.getVisited(url)) {
			ArrayList<String> contents = this.parser.getContent(url);
			for (String word : contents) {
				word = word.toLowerCase();
				if (!this.wordIndex.containsKey(word)) {
					ArrayList<String> urlList = new ArrayList<>();
					urlList.add(url);
					this.wordIndex.put(word, urlList);
				} else {
					ArrayList<String> urlList = this.wordIndex.get(word);
					urlList.add(url);
				}
			}
		}

		//DFS
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
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex().
	 *
	 */
	public void assignPageRanks(double epsilon) {

		ArrayList<String> vertices = internet.getVertices();
		int numV = vertices.size();
		//set all vertex ranks to 1
		for (String vertex : vertices) {
			internet.setPageRank(vertex, 1.0);
		}

		boolean converged = false;

		ArrayList<Double> prevRanks = new ArrayList<>();
		for (String v : vertices){
			prevRanks.add(internet.getPageRank(v));
		}

		while (!converged) {


			ArrayList<Double> newRanks = computeRanks(vertices);

			for (int i=0; i<numV; i++) {
				Double diff = newRanks.get(i) - prevRanks.get(i);

				// check if difference < epsilon for all urls
				if (diff < epsilon) {
					converged = true;

				//one of the urls has diff > epsilon, stop checking and recompute ranks
				} else {
					converged = false;
					break;
				}
			}
			//now set the page ranks to the newly computed ranks
			for (int i=0; i<vertices.size(); i++) {
				internet.setPageRank(vertices.get(i), newRanks.get(i));
			}
			if (!converged){
				prevRanks = newRanks;
			}
		}

	}

	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		ArrayList<Double> ranks = new ArrayList<>();

		// calculate rank for each vertex in the list
		for (String vertex : vertices) {
			double inRank = 0.0;

			for (String vertexInto : internet.getEdgesInto(vertex)) {
				inRank += internet.getPageRank(vertexInto) / internet.getOutDegree(vertexInto);

			}
			double rank = 0.5 + 0.5 * inRank;
			ranks.add(rank);
		}

		return ranks;
	}


	
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 */
	public ArrayList<String> getResults(String query) {
		ArrayList<String> urls = wordIndex.get(query);
		query = query.toLowerCase();
		HashMap<String, Double> urlandRank = new HashMap<>();

		for (String url : urls) {
			Double rank = internet.getPageRank(url);
			urlandRank.put(url, rank);
		}
		ArrayList<String> sortedUrls = Sorting.fastSort(urlandRank);
		return sortedUrls;
	}

	public static void main(String[] args) throws Exception {
		// set up the "internet"
		SearchEngine searchEngine = new SearchEngine("cs20Links.xml");
		searchEngine.crawlAndIndex("https://cs.mcgill.ca/");
		searchEngine.assignPageRanks(.001);
		System.out.println();

		// user input
		Scanner in = new Scanner(System.in);
		System.out.println("Enter your query: ");
		String userQuery = in.nextLine();
		ArrayList<String> results = searchEngine.getResults(userQuery);
		System.out.println("Search results: ");
		System.out.println();
		for (String res : results) {
			System.out.println(res);
		}


	}
}
