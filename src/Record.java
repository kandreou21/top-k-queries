public class Record implements Comparable<Record>{
	private int id;
	private Float lowerBound;
	private Float totalScore;
	int fileShown; //1 seq1, 2 seq2
	Long timestamp = System.nanoTime();
	 
	public Record(int id, float lowerBound, int fileShown) {
		this.id = id;
		this.lowerBound = lowerBound;
		this.totalScore = null;
		this.fileShown = fileShown;
	}
	
	public int getId() {
		return id;
	}
	
	public float getLowerBound() {
		return lowerBound;
	}
	
	public Float getTotalScore() {
		return totalScore;
	}
	
	public void setTotalScore(float score) {	//increase the lowerBound by a score
		this.totalScore = lowerBound + score;
	}
	
	public float getCurrentScore() {
		if (totalScore == null)
			return lowerBound;
		return totalScore;
	}

	public int compareTo(Record o) {	//check gia null sto total score 
		float result = this.getCurrentScore() - o.getCurrentScore();
		if (result > 0) {
			return 1;
		} else if (result < 0) {
			return -1;
		} else {
			return o.timestamp.compareTo(timestamp);
		}	
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", lowerBound=" + lowerBound + ", totalScore=" + totalScore + ", currentScore=" + this.getCurrentScore() + "]";
	}
}
