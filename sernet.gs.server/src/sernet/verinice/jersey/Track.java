package sernet.verinice.jersey;

public class Track {
	 
	String title;
	String singer;
 
	public String getTitle() {
		return title;
	}
 
	public void setTitle(String title) {
		Test test = new Test();
		test.setTitle("sdv");
		System.out.println("TITEL von test:" + test.getTitle());
		this.title = title;
	}
 
	public String getSinger() {
		return singer;
	}
 
	public void setSinger(String singer) {
		this.singer = singer;
	}
 
	@Override
	public String toString() {
		return "Track [title=" + title + ", singer=" + singer + "]";
	}
 
}